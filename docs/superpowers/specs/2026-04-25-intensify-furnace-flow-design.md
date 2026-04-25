# Intensify Furnace Flow Redesign for NeoForge 1.21

Date: 2026-04-25
Branch: `neoforge-1.21`
Status: Draft approved in chat, pending implementation

## Context

The current NeoForge 1.21 port has reached a working baseline, but the furnace-based intensify flow drifted away from the original `1.20.1` semantics.

Recent fixes around white-burning, stale pending state, and furnace menu timing improved some symptoms, but they did so by adding more external tracking:

- global tracked furnace state
- pending furnace operation tags
- input-side temporary operation markers
- menu-open keep-alive behavior

This direction is not acceptable as the long-term design. It is too timing-sensitive, too indirect, and too easy to break with new edge cases.

The user requirement is explicit:

- the flow must be logically correct
- the functionality must match the intended design
- the solution must not rely on ad-hoc timing patches

## Problem Statement

The current 1.21 implementation lets too much furnace behavior live outside the furnace recipe lifecycle.

That creates three classes of problems:

1. Source-of-truth confusion

The active operation may be represented by:

- the current fuel item
- `PendingFurnaceOperation`
- the input item's temporary operation marker
- the tracked furnace map

When multiple mechanisms can each "explain" the same furnace state, they can diverge.

2. Timing sensitivity

Whether a furnace starts or continues can depend on:

- whether the GUI is open
- whether the furnace was tracked this tick
- whether the input marker was written before recipe lookup
- whether a cleanup pass happened first

That is how bugs like "close and reopen the furnace to make it start" appeared.

3. Incorrect architectural boundary

In `1.20.1`, the recipe itself encoded whether a furnace cycle may start and whether it may continue.

In the current 1.21 port, recipe matching is only part of the truth, while event-side tracking tries to reconstruct furnace intent afterward.

That is backwards. The furnace should know whether a cycle is valid by asking the recipe, not by waiting for global event logic to reinterpret what just happened.

## Design Goals

The redesigned furnace flow must satisfy all of the following:

- A valid cycle starts immediately without requiring GUI reopen behavior.
- An invalid cycle never starts burning.
- Once a cycle starts, only that same recipe cycle may continue.
- Final intensify effects apply exactly once and only when the cycle really completes.
- The main logic lives in the recipe and furnace state, not in global tick-side tracking.
- The behavior matches the `1.20.1` mental model as closely as possible while remaining compatible with NeoForge 1.21.

## Non-Goals

This redesign does not aim to:

- redesign intensify gameplay
- change success rates or item balance
- add new furnace UX
- keep the recent tracking framework for diagnostic convenience

If a recent helper only exists to prop up the tracking architecture, it should be removed rather than preserved.

## Recommended Approach

Use the `1.20.1` recipe-driven state model as the canonical design.

This means:

- `IntensifyRecipe.matches(...)` becomes the single gate for starting and continuing a furnace cycle
- furnace persistent data stores the active recipe identity using `LAST_RECIPE`
- recipe completion applies the actual intensify effect
- event handlers no longer drive furnace progression

This is preferred over keeping a reduced tracking framework because a reduced tracking framework still leaves two competing sources of truth:

- recipe semantics
- event-side state reconstruction

The redesign should collapse those back into one.

## Target Flow

### 1. Before ignition

When furnace `litTime <= 0`:

- the recipe checks whether the input item is intensifiable
- the recipe checks whether the current fuel item is the correct intensify stone for that input state
- if valid, the furnace stores `LAST_RECIPE = this recipe id`
- if invalid, `LAST_RECIPE` is removed

At this stage, the furnace is deciding whether a new cycle is allowed to begin.

### 2. During an active cycle

When furnace `litTime > 0`:

- the recipe does not try to infer a new operation from current external state
- the recipe only checks whether `LAST_RECIPE` matches this recipe id
- if it matches, the cycle may continue
- if it does not match, this recipe does not participate

This preserves the original rule: once a cycle has started, continuation is bound to that cycle, not to whatever item or stone happens to be swapped around afterward.

### 3. Completion

When the furnace reaches the actual completion boundary:

- the recipe creates the final output item
- the intensify effect is applied exactly once
- the output is the authoritative final result

This replaces the current approach where event-side logic watches for result-slot changes and then mutates the result after the fact.

## Architectural Changes

### Keep

These responsibilities remain in `IntensifyForgeEventHandler`:

- recording furnace owner name
- item tooltip logic
- commands
- loot/drop hooks
- advancements that are unrelated to driving furnace progression

### Remove or Collapse

The following should be removed from the furnace control path:

- `TRACKED_FURNACES`
- `OPEN_FURNACE_MENUS`
- `PendingFurnaceOperation`
- input-side temporary operation markers used to drive recipe matching
- menu keep-alive logic
- global tick-driven "apply pending operation when result changes" logic
- white-burn prevention that only exists because the main cycle is not recipe-driven

### Re-center

The main furnace logic should move back into:

- `IntensifyRecipe.matches(...)`
- `IntensifyRecipe.assemble(...)`
- minimal furnace state helpers where needed

## Expected Code Movement

### `IntensifyRecipe`

This class becomes the core state machine again.

It should:

- distinguish between pre-ignition and active-burn behavior
- write or clear `LAST_RECIPE`
- gate continuation based on `LAST_RECIPE`
- own final intensify application at completion time

### `IntensifyForgeEventHandler`

This class should stop deciding:

- which operation a furnace is currently performing
- whether a result-slot change means intensify should happen
- whether an active furnace should continue because of extra tracked state

### `FurnaceHelper`

This helper may still expose raw furnace fields needed by recipe logic, but only as field accessors, not as a second state machine.

### `FurnaceTrackingRules`

This helper should disappear if its remaining purpose is only to support the tracking-based architecture.

If any rule from it is still useful after the redesign, it should be moved closer to recipe semantics rather than preserved as a tracking helper.

## Testing Strategy

The redesign must be validated with flow-level tests, not only "starts burning" checks.

Required cases:

1. Plain tool + eneng stone

- starts immediately
- completes
- output is marked as enenged

2. Enenged tool + strengthening stone

- starts immediately
- completes
- output goes through enhancement intensify behavior

3. Plain tool + strengthening stone

- does not start
- consumes nothing

4. Wrong stone during an existing cycle

- does not convert the cycle into a different operation

5. Open empty furnace, then place tool and stone

- starts without requiring close/reopen

6. Completion happens exactly once

- no post-result duplicate mutation

7. Persistence/continuation behavior

- if the cycle is already burning, continuation follows `LAST_RECIPE`, not freshly inferred external state

## Migration Strategy

Implementation should happen in this order:

1. Add or adjust regression tests to lock the intended `1.20.1` semantics.
2. Rebuild `IntensifyRecipe` around `LAST_RECIPE`.
3. Move final effect application back into recipe completion.
4. Remove event-side furnace progression logic.
5. Delete obsolete tracking helpers and constants.
6. Run full verification.

This order ensures we do not reintroduce the same bugs while deleting the current workaround system.

## Risks

### Completion-boundary differences in 1.21

The exact point where NeoForge 1.21 considers a furnace cycle "finished" must be confirmed carefully.

The implementation should preserve the `1.20.1` semantic outcome, not necessarily the exact old code shape.

### Duplicate application risk

If both recipe completion and event logic remain active, intensify effects could apply twice.

This is why the redesign must fully remove event-side completion handling instead of partially overlapping with it.

### Persistence edge cases

`LAST_RECIPE` must be treated as the authoritative cycle identity during burning, and it must be cleared when a new cycle is not valid.

Any ambiguity here will recreate white-burning under a different name.

## Acceptance Criteria

The redesign is complete when all of the following are true:

- furnace intensify behavior no longer depends on a tracked-furnace map
- opening and closing the GUI does not change whether a valid cycle starts
- eneng, strengthening, and eternal flows follow correct recipe semantics
- wrong fuel does not start or hijack a cycle
- completion applies exactly once
- all furnace regression GameTests pass
- `./gradlew build` passes

## Decision

Proceed with a full migration from the current tracking-driven furnace flow to the `1.20.1`-style recipe-driven flow centered on `LAST_RECIPE`.
