# Intensify NeoForge 1.21 Migration Design

**Date:** 2026-04-25
**Status:** Approved in chat, written for implementation
**Target Branch:** `neoforge-1.21`

## Goal

Create an `Intensify` `neoforge-1.21` branch that migrates the mod from `Forge 1.20.1` to `NeoForge 1.21`, updates the build to the modern NeoForge toolchain, and preserves `1.20.1` gameplay behavior as closely as possible while depending on `AttributesLib 1.21`.

## Current State

The current `Intensify` codebase is a `Forge 1.20.1` project using:

- `ForgeGradle 6`
- `Java 17`
- `mods.toml`
- Forge event buses and lifecycle wiring
- Forge capability APIs for first-login player data
- Forge global loot modifier registration and datagen
- A runtime dependency on `dev.shadowsoffire:attributeslib:1.20.1_1.0.0`

The local `AttributesLib` repository already has a `neoforge-1.21` branch and uses:

- `net.neoforged.moddev`
- `Java 21`
- `META-INF/neoforge.mods.toml`

This means the `Intensify` migration is both a Minecraft version upgrade and a platform/toolchain migration.

## Scope

### In Scope

- Move `Intensify` from `Forge 1.20.1` to `NeoForge 1.21`
- Switch build tooling to `net.neoforged.moddev`
- Upgrade runtime and compile toolchain requirements to `Java 21`
- Replace Forge mod metadata and run configuration conventions with NeoForge equivalents
- Update dependency wiring to use `AttributesLib 1.21`
- Restore all important `1.20.1` behaviors after the platform migration
- Produce a verified branch that builds and launches in a NeoForge 1.21 development environment

### Out of Scope

- Gameplay redesign
- Unrelated refactors
- Broad cleanup work that does not support the `1.21` migration
- Intentional balance changes
- Rewriting systems that already migrate cleanly with focused changes

## Constraints

- Only `Intensify` needs a new branch for this work
- The branch name must be `neoforge-1.21`
- `AttributesLib` may be fixed if needed, but `Intensify` should adapt first when the issue is only an API change
- User-visible behavior should stay aligned with `1.20.1` unless `NeoForge 1.21` requires an equivalent replacement

## Approaches Considered

### 1. Full one-pass migration

Port the entire codebase and all features in one uninterrupted change set.

**Pros**

- Direct route to the final shape
- No deliberate intermediate states

**Cons**

- Hard to isolate failures
- Delays first successful compile and launch
- High risk because build, lifecycle, registries, capabilities, GLM, and datagen all change at once

### 2. Baseline first, then recover feature parity

First make the project compile and launch as a NeoForge 1.21 mod with dependency resolution, then restore higher-risk behaviors in controlled passes.

**Pros**

- Fastest path to a verifiable baseline
- Problems surface early in the platform layer
- Easier to debug and review
- Best match for a Forge to NeoForge migration

**Cons**

- Intermediate commits may temporarily have partial feature parity

### 3. Module-by-module migration without a platform baseline

Try to migrate independent feature areas one at a time before the full platform conversion is stable.

**Pros**

- Clear modular sequencing

**Cons**

- Still blocked by unresolved toolchain and lifecycle differences
- Can create false progress if the base platform layer is not yet sound

## Selected Approach

Use **Approach 2: baseline first, then recover feature parity**.

This keeps the final target unchanged: `Intensify` on `NeoForge 1.21` with behavior closely matching `1.20.1`. The difference is only the order of delivery:

1. Establish a stable NeoForge 1.21 baseline.
2. Recover feature parity module by module.
3. Verify final behavior against the original mod.

## Migration Architecture

The migration will be handled in four layers.

### Layer 1: Platform Foundation

Purpose: make the project exist as a valid NeoForge 1.21 mod.

Primary changes:

- Replace ForgeGradle setup with `net.neoforged.moddev`
- Move to `Java 21`
- Update `gradle.properties` for `minecraft_version=1.21` and matching NeoForge settings
- Replace `META-INF/mods.toml` with `META-INF/neoforge.mods.toml` or equivalent generated metadata flow
- Rewire run configurations and metadata expansion
- Resolve the `AttributesLib 1.21` dependency for development and build

Success criteria:

- `./gradlew build` succeeds
- Development client launch starts loading the mod
- Mod metadata is recognized by NeoForge

### Layer 2: Base Registration

Purpose: restore object registration and configuration loading.

Primary code areas:

- `Intensify.java`
- `registry/ItemRegistry.java`
- `registry/RecipeRegistry.java`
- `registry/LootConditionsRegistry.java`
- `registry/LootRegistry.java`
- `registry/ConfigRegistry.java`

Primary changes:

- Update entrypoint construction to NeoForge 1.21 expectations
- Reconnect mod event bus and main event bus registrations
- Migrate deferred registers and registry holders where needed
- Rework config registration to the NeoForge lifecycle

Success criteria:

- Items, recipe serializers, loot condition types, and GLM serializers register without startup crashes
- Config specs still load and parse

### Layer 3: Behavior Compatibility

Purpose: preserve gameplay behavior.

Primary code areas:

- `IntensifyForgeEventHandler.java`
- capability classes in `capabilities/`
- recipe and loot implementation classes
- config-backed behavior helpers

Primary changes:

- Port event imports and event hookup to NeoForge APIs
- Replace first-login player capability storage with NeoForge data attachments if the old capability pattern is no longer appropriate
- Preserve right-click furnace ownership behavior
- Preserve dropped item modification and fishing-related logic
- Preserve command registration
- Preserve tooltip and advancement-related behavior

Success criteria:

- Core strengthening flow works
- First-login behavior still works
- Drop and loot modifications still trigger
- Commands and tooltips behave correctly

### Layer 4: Datagen and Resource Parity

Purpose: restore generated assets and data-driven resources.

Primary code areas:

- `IntensifyModEventHandler.java`
- datagen providers under `provider/`
- generated and static resource files under `src/main/resources` and `src/generated/resources`

Primary changes:

- Update `GatherDataEvent` usage and provider construction
- Update GLM datagen to current NeoForge conventions
- Preserve item model, recipe, loot table, and advancement generation
- Ensure generated data paths match NeoForge 1.21 expectations

Success criteria:

- Datagen completes
- Generated resources are structurally correct
- Runtime resource loading matches the expected behavior from `1.20.1`

## Compatibility Strategy

The migration follows this rule:

> Preserve gameplay and data behavior where possible, but replace outdated platform integration with NeoForge 1.21-native implementations.

This means:

- Keep visible player behavior stable
- Keep recipe and loot semantics stable
- Keep config meaning stable
- Replace old implementation mechanisms when `NeoForge 1.21` provides a better-supported equivalent

### Planned Intentional Internal Changes

- Replace Forge-specific mod metadata with NeoForge metadata
- Replace or refactor Forge-specific lifecycle wiring
- Migrate first-login entity state from custom capability infrastructure to NeoForge data attachments if necessary
- Update GLM registration and datagen to the current `NeoForgeRegistries` and codec model

These are implementation changes, not feature changes.

## Dependency Strategy

`Intensify` should depend on the local `AttributesLib 1.21` work during development, then keep the dependency declaration aligned with the publishable artifact once the library build is validated.

Order of operations:

1. Build or publish the local `AttributesLib` `neoforge-1.21` artifact.
2. Point `Intensify` development/build resolution at that artifact.
3. Adjust only for real API changes first.
4. Fix `AttributesLib` only if the library itself is broken for `1.21`.

## Risks

### High Risk

- Toolchain and dependency migration from ForgeGradle to ModDevGradle
- Player data persistence migration if capabilities no longer fit well
- GLM registration and datagen changes

### Medium Risk

- Event class and import changes across lifecycle and gameplay hooks
- Resource metadata and generated file path differences
- Registry holder type differences

### Low Risk

- Core item and recipe business logic once the platform layer is stable
- Config-backed text and probability data if config registration remains equivalent

## Validation Plan

Validation will happen in three waves.

### 1. Compile Validation

- Run `./gradlew build`
- Resolve all import, registry, metadata, dependency, and resource issues

### 2. Launch Validation

- Run the NeoForge development client
- Confirm the mod loads
- Confirm `AttributesLib 1.21` resolves and loads with it

### 3. Behavior Validation

Check the following manually or with focused testing helpers if practical:

- Intensify stones exist and are registered correctly
- Core strengthening flow still works
- Loot modifications still apply
- First-login logic still runs correctly
- Commands do not error
- Tooltip and advancement behavior remain functional

## Acceptance Criteria

### Baseline Acceptance

- `./gradlew build` passes
- The development client launches
- The mod loads in NeoForge 1.21
- Base items, serializers, and config registration succeed
- `AttributesLib 1.21` is resolved correctly

### Feature Parity Acceptance

- Core strengthening gameplay remains functional
- Loot modifier behavior matches the original intent
- Configs keep the same meaning as `1.20.1`
- Tooltips, commands, and advancement triggers do not regress
- No intentional gameplay simplification is left in the final migration state

## Implementation Order

1. Create and verify the `neoforge-1.21` branch.
2. Migrate the build and metadata layer.
3. Validate dependency resolution against `AttributesLib 1.21`.
4. Restore base registration and config loading.
5. Run a build and client baseline check.
6. Port high-risk behavior systems.
7. Restore datagen and resource parity.
8. Run final build and launch verification.
9. Perform focused gameplay validation for strengthening, loot, login state, commands, and tooltips.

## References

- NeoForge mod files: https://docs.neoforged.net/docs/1.21.4/gettingstarted/modfiles
- NeoForge registries: https://docs.neoforged.net/docs/1.21.8/concepts/registries
- NeoForge data attachments: https://docs.neoforged.net/docs/1.21.5/datastorage/attachments
- NeoForge global loot modifiers: https://docs.neoforged.net/docs/1.21.8/resources/server/loottables/glm/
