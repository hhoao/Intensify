# Mace Balance Design

Date: 2026-04-25

## Goal

Add a first-pass balance profile for the new `mace` weapon type in Intensify that:

- preserves `mace` as an independent type rather than reusing `sword`
- gives it a clear identity as a heavy armor-breaking melee weapon
- keeps it competitive with `sword` and `axe` without overtaking both

## Desired Identity

`mace` should feel like a low-frequency, high-quality hit weapon.

Its primary strengths are:

- high single-hit damage
- reliable armor penetration
- a small amount of bonus critical damage to reinforce "heavy hit" flavor

Its intended weaknesses are:

- very limited attack speed scaling
- no critical chance scaling
- no sustain-oriented melee utility such as life steal

This keeps `mace` distinct from existing melee options:

- `sword`: balanced, faster, more general-purpose
- `axe`: broader melee pressure with stronger armor shred identity
- `mace`: heavier single-hit pressure with piercing focus

## Attribute Structure

The `mace` default config should keep exactly these attributes:

- `generic.attack_damage`
- `armor_pierce`
- `armor_shred`
- `crit_damage`
- `generic.attack_knockback`
- `generic.attack_speed`

The config should deliberately exclude:

- `crit_chance`
- `life_steal`
- any ranged or tool-specific attributes

## Balance Rules

### 1. Attack damage is the main axis

`mace` should scale harder on raw hit damage than `sword`, but not so hard that it invalidates `axe`.

### 2. Armor pierce is the secondary axis

`mace` should have a clearly visible anti-armor identity through `armor_pierce`, but it should not exceed the upper-end piercing profile used by more specialized weapon configs.

### 3. Armor shred exists, but is intentionally lighter than axe

`armor_shred` is included only to support the fantasy of crushing armor over time. It must remain meaningfully below `axe` so `axe` keeps its own niche.

### 4. Critical damage is flavor, not the core engine

`crit_damage` should be present in moderate amounts, but `mace` should not gain `crit_chance`. The weapon should feel rewarding on heavy hits without becoming a crit-stack build.

### 5. Attack speed growth stays capped and low

`attack_speed` exists only to smooth early feel. It should stop growing early so the weapon never turns into a fast melee archetype.

## Recommended Numbers

### `generic.attack_damage`

- eneng: `[1.2, 3.0]`
- grows:
  - `1-8`: fixed `1.0`, speed `1`
  - `9+`: proportional `0.08`

### `armor_pierce`

- eneng: `[1.0, 3.0]`
- grows:
  - `1-6`: fixed `0.8`, speed `1`
  - `7+`: proportional `0.04`

### `armor_shred`

- eneng: `[0.04, 0.1]`
- grows:
  - `5-20`: fixed `0.035`, speed `3`
  - `21+`: proportional `0.015`, speed `4`

### `crit_damage`

- eneng: `[0.08, 0.18]`
- grows:
  - `3+`: fixed `0.05`, speed `2`

### `generic.attack_knockback`

- eneng: `[0.2, 0.6]`
- grows:
  - `1-8`: fixed `0.08`, speed `1`

### `generic.attack_speed`

- eneng: `[0.05, 0.15]`
- grows:
  - `1-4`: fixed `0.04`, speed `1`
  - `5+`: no further growth

## Implementation Scope

This design only changes the default `mace.toml` balance profile.

It does not change:

- type recognition logic
- enhancement algorithms
- Intensify core formulas
- `sword` or `axe` configs

## Verification

Implementation is considered correct when:

- `mace.toml` reflects the approved attribute list and ranges
- `MaceSupportTest` still passes
- `./gradlew build --console=plain` passes

