# Intensify Furnace Recipe Book Display Design

Date: 2026-04-25
Branch: `neoforge-1.21.8`

## Goal

Add `Intensify` strengthening rules to the vanilla furnace recipe book as read-only display entries.

This feature is intended to improve discoverability only. It must not change the real furnace strengthening flow, add auto-fill behavior, or alter recipe execution.

## Scope

The furnace recipe book will show three generic `Intensify` rules:

1. `eneng`
   - Meaning: any supported equipment can be enabled with `Eneng Stone`
2. `strengthening`
   - Meaning: already-enabled equipment can be strengthened with `Strengthening Stone`
3. `eternal`
   - Meaning: already-enabled equipment can be made eternal with `Eternal Stone`

These entries are display-only and do not participate in real furnace matching or result generation.

## Non-Goals

- No recipe-book auto-fill
- No changes to the actual furnace strengthening implementation
- No new strengthening mechanics
- No fine-grained split by weapon or armor type
- No dedicated custom UI outside the vanilla furnace recipe book

## Current Context

`Intensify` currently uses a custom furnace execution flow. Real strengthening is controlled by the furnace internals and `IntensifyRecipe` subclasses, not by vanilla smelting matching.

Current `IntensifyRecipe` data exposed to display systems is intentionally placeholder-oriented:

- placeholder ingredient/result values exist for network safety
- real execution happens elsewhere

Because of that, directly exposing the existing execution recipes to the recipe book would produce misleading or ugly entries. The display layer must therefore be separated from the execution layer.

## Recommended Approach

Introduce a dedicated display-only recipe layer for the furnace recipe book.

This layer should:

- provide three representative furnace recipe book entries
- use sample items for presentation
- never participate in real strengthening logic
- never change the current furnace flow

This keeps responsibilities clean:

- execution recipes remain responsible for real logic
- display recipes remain responsible for player-facing recipe book presentation

## Display Model

Each displayed entry should be a generic rule card, not a literal equipment-specific recipe.

Recommended representative items:

- sample tool: `diamond_sword`
- sample stones:
  - `Eneng Stone`
  - `Strengthening Stone`
  - `Eternal Stone`

Recommended displayed entries:

1. `diamond_sword + Eneng Stone -> enabled sample sword`
2. `enabled sample sword + Strengthening Stone -> strengthened sample sword`
3. `enabled sample sword + Eternal Stone -> eternal sample sword`

The result item should visually communicate "example outcome" rather than imply a hard-coded real product.

## Architecture

### Execution Layer

Keep the current execution path unchanged:

- furnace mixin drives real strengthening flow
- `IntensifyRecipe` subclasses continue to define real strengthening rules
- start/continuation/completion semantics remain untouched

### Display Layer

Add a separate thin display-only recipe structure that exists only so the furnace recipe book can show meaningful entries.

Recommended structure:

- a small base class or helper for furnace display-only recipes
- three display entries:
  - `display/eneng`
  - `display/strengthening`
  - `display/eternal`

These entries should be registered and synchronized like normal recipe-book-visible recipes, but must never be treated as real strengthening recipes.

## UX Rules

To avoid misleading players:

- the entries should be treated as examples, not exact per-item recipes
- clicking them must not auto-place items into the furnace
- they must not imply that only `diamond_sword` is supported
- they must not imply that the shown result item is the exact literal output for every case

Using one consistent sample item across all three entries makes the rule set easier to read and avoids clutter.

## Testing Strategy

### Static Validation

Verify that exactly three display entries are present in the built recipe set.

### Isolation Validation

Verify existing furnace behavior does not regress:

- enabling still works
- strengthening still works
- eternal still works
- invalid flows still fail correctly
- furnace-only restrictions still hold

### Display Validation

Add a focused test that verifies:

- the three display entries exist
- each entry uses the expected stone
- each entry uses the expected sample item
- the display entries are not used by the real strengthening execution path

## Risks

### Misleading Display

If the recipe book presents these entries like literal recipes, players may assume only one item type is supported.

Mitigation:

- use a generic sample consistently
- treat the feature as rule illustration, not exact item crafting

### Logic Coupling

If display data is merged into the real execution recipes, future furnace logic changes may accidentally break the recipe book or vice versa.

Mitigation:

- keep display recipes separate from execution recipes

### Auto-Fill Confusion

If recipe-book interaction tries to place items automatically, it will conflict with the mod's real strengthening semantics.

Mitigation:

- do not support auto-fill in this feature

## Acceptance Criteria

- The furnace recipe book shows three `Intensify` display entries
- The entries correspond to `Eneng`, `Strengthening`, and `Eternal`
- The entries use representative sample visuals rather than placeholder furnace data
- Clicking the entries does not auto-fill the furnace
- Existing strengthening logic behaves exactly as before

## Implementation Boundary

This feature is complete when recipe book discoverability is improved without any behavioral change to real strengthening.

If a design choice would change actual strengthening behavior, that change is out of scope for this feature and should be handled separately.
