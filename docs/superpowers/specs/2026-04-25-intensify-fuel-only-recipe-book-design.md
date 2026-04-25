# Intensify 1.21.8 Fuel-Only Furnace Recipe Book Design

## Background

`Intensify` currently exposes three furnace recipe-book entries for:

- `eneng_stone`
- `strengthening_stone`
- `eternal_stone`

These entries are intended to teach players the furnace-based enhancement rules. However, the current interaction feels too much like a real smelting recipe: clicking a recipe can behave like a normal furnace placement flow, which is misleading for a system where the equipment input must remain player-chosen.

The desired interaction is closer to vanilla recipe-book ergonomics while preserving `Intensify` semantics:

- If the player has the required stone, clicking the recipe should place only the stone into the furnace fuel slot.
- If the player does not have the stone, the recipe-book interaction should behave like vanilla hinting/highlighting rather than silently doing nothing.
- The equipment input slot must never be auto-filled by these three display recipes.

## Goals

- Keep the three `Intensify` furnace recipe-book entries.
- Make those entries behave as fuel-only helper recipes.
- Preserve vanilla-feeling feedback:
  - has item -> place it
  - no item -> highlight/hint it
- Never auto-place any weapon or armor into the furnace input slot.
- Avoid changing the real `Intensify` furnace enhancement execution flow.

## Non-Goals

- No changes to actual enhancement matching or settlement.
- No changes to mob/mining drops, tooltip behavior, or balance.
- No attempt to make recipe-book auto-complete a full enhancement setup.
- No redesign of the three display recipe resources themselves beyond metadata needed for interaction.

## User Experience

The furnace recipe book still shows three entries:

1. `曦能石` rule
2. `强化石` rule
3. `永恒石` rule

When the player clicks one of these entries:

- If the player inventory contains the corresponding stone, the recipe book attempts to place that stone into the fuel slot.
- If the player inventory does not contain that stone, the recipe book should still surface the normal vanilla-style visual feedback/highlighting path.
- The input slot remains fully manual. The player must choose and place the equipment themselves.

Expected result:

- The entries remain discoverable from the furnace recipe book.
- Clicking them is helpful, but not misleading.
- The system no longer feels like a fake full recipe that takes control away from the player.

## Approach Options

### Option 1: Menu-Level Placement Intercept

Intercept recipe-book placement for these three display recipes at the furnace menu / placement layer, and rewrite the placement behavior into fuel-only placement.

Pros:

- Best match for the desired vanilla-like interaction.
- Narrow behavior change.
- Keeps the execution system unchanged.

Cons:

- Requires working with the `1.21.8` recipe-book placement chain.

### Option 2: Container-Level Cleanup

Allow vanilla placement to start, then undo any input-slot placement and preserve only fuel-slot changes.

Pros:

- Potentially smaller entry point.

Cons:

- Patch-like and fragile.
- More likely to conflict with vanilla menu assumptions.

### Option 3: Fully Custom Click Handler

Replace placement behavior for these display recipes with an `Intensify`-specific custom interaction flow.

Pros:

- Maximum control.

Cons:

- Highest maintenance cost.
- Furthest from vanilla behavior.

## Decision

Use **Option 1: Menu-Level Placement Intercept**.

This gives the closest result to vanilla recipe-book ergonomics while keeping the scope narrow and preserving the existing enhancement execution model.

## Design

### 1. Recipe Metadata

`FurnaceRecipeBookDisplayRecipe` should explicitly identify itself as a fuel-only display recipe and expose which stone item is placeable.

This metadata is not for enhancement logic. It is only for recipe-book interaction routing.

### 2. Placement Handling

At the furnace menu / placement path:

- Normal furnace recipes keep vanilla behavior.
- `Intensify` fuel-only display recipes are recognized specially.
- Placement attempts only target the furnace fuel slot.
- Input slot placement is blocked for these three display recipes.

### 3. Inventory Feedback

Placement should preserve the vanilla-feeling distinction between:

- item available -> place into slot
- item unavailable -> highlight/hint path

This feature must not degrade into a silent no-op when the player lacks the stone.

### 4. Enhancement Safety

This design intentionally allows the furnace to start burning if:

- the player already has valid equipment in the input slot, and
- the clicked recipe successfully places the required stone into the fuel slot

That is considered correct behavior because the player remains in control of equipment choice.

What is explicitly forbidden:

- Auto-placing example swords or other sample equipment
- Changing enhancement legality checks
- Changing outcome settlement behavior

## Affected Areas

Expected touch points:

- `FurnaceRecipeBookDisplayRecipe`
- recipe serializer / metadata shape if needed
- furnace menu or recipe-book placement interception path
- tests for display recipe interaction behavior

The existing execution path should remain unchanged:

- `IntensifyRecipe`
- furnace enhancement mixin logic
- actual enhancement settlement

## Risks

### Vanilla Placement Assumptions

Vanilla furnace recipe-book flows may assume a normal input + fuel recipe shape. The intercept must be narrow and only apply to `Intensify` fuel-only display recipes.

### Feedback Regression

If the implementation bypasses too much of vanilla placement flow, "missing item" feedback may be lost. The implementation must preserve vanilla-style hint/highlight behavior.

### Overreach Into Normal Recipes

Ordinary furnace recipes such as ores, food, and blocks must remain untouched.

## Validation

### Functional Validation

- Clicking `曦能石` recipe only targets the fuel slot.
- Clicking `强化石` recipe only targets the fuel slot.
- Clicking `永恒石` recipe only targets the fuel slot.
- No display recipe click places any sample weapon/armor into the input slot.

### Inventory Feedback Validation

- With matching stone present in inventory, the stone is placed into fuel.
- Without matching stone in inventory, the interaction follows vanilla-style feedback/highlighting rather than a silent failure.

### Isolation Validation

- Normal furnace recipes behave unchanged.
- Existing `Intensify` furnace execution tests still pass.

## Acceptance Criteria

- The three display recipes remain visible in the furnace recipe book.
- Clicking them never auto-fills the furnace input slot.
- Clicking them can place only the corresponding stone into the fuel slot.
- Missing-stone clicks preserve vanilla-like feedback.
- No regression in existing enhancement execution behavior.
