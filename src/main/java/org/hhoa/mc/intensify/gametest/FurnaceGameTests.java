package org.hhoa.mc.intensify.gametest;

import static org.hhoa.mc.intensify.Intensify.MODID;
import static org.hhoa.mc.intensify.config.IntensifyConstants.ENENGED_TAG_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.BuiltinTestFunctions;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.hhoa.mc.intensify.IntensifyForgeEventHandler;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.hhoa.mc.intensify.util.FurnaceHelper;
import org.hhoa.mc.intensify.util.ItemModifierHelper;

public final class FurnaceGameTests {
    private static final BlockPos FURNACE_POS = new BlockPos(1, 1, 1);
    private static final ResourceLocation TEMPLATE_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "furnace_progress");

    private FurnaceGameTests() {}

    public static void enengStartsCookingForPlainTool(GameTestHelper helper) {
        FurnaceBlockEntity furnace =
                setUpFurnace(
                        helper,
                        new ItemStack(Items.DIAMOND_SWORD),
                        new ItemStack(ItemRegistry.ENENG_STONE.get()));

        helper.runAtTickTime(
                10,
                () -> assertFurnaceCooking(helper, furnace, "expected eneng furnace to progress"));
        helper.runAtTickTime(11, helper::succeed);
    }

    public static void enengFinishesAndMarksTool(GameTestHelper helper) {
        FurnaceBlockEntity furnace =
                setUpFurnace(
                        helper,
                        new ItemStack(Items.DIAMOND_SWORD),
                        new ItemStack(ItemRegistry.ENENG_STONE.get()));

        helper.runAtTickTime(
                220,
                () -> {
                    ItemStack result = furnace.getItem(2);
                    if (result.isEmpty()) {
                        helper.fail(Component.literal("expected eneng furnace to produce a result"));
                    }
                    if (!ItemModifierHelper.getBooleanTag(result, ENENGED_TAG_ID)) {
                        helper.fail(
                                Component.literal(
                                        "expected eneng furnace result to be marked as enenged"));
                    }
                });
        helper.runAtTickTime(221, helper::succeed);
    }

    public static void emptyFurnaceStartsWhenItemsAddedLater(GameTestHelper helper) {
        helper.setBlock(FURNACE_POS, Blocks.FURNACE);
        FurnaceBlockEntity furnace = helper.getBlockEntity(FURNACE_POS, FurnaceBlockEntity.class);

        helper.runAtTickTime(
                10,
                () -> {
                    furnace.setItem(0, new ItemStack(Items.DIAMOND_SWORD));
                    furnace.setItem(1, new ItemStack(ItemRegistry.ENENG_STONE.get()));
                    furnace.setChanged();
                });
        helper.runAtTickTime(
                20,
                () ->
                        assertFurnaceCooking(
                                helper,
                                furnace,
                                "expected empty furnace to start after items were added"));
        helper.runAtTickTime(21, helper::succeed);
    }

    public static void strengtheningStartsCookingForEnengedTool(GameTestHelper helper) {
        ItemStack tool = new ItemStack(Items.DIAMOND_SWORD);
        ItemModifierHelper.putBooleanTag(tool, ENENGED_TAG_ID, true);
        FurnaceBlockEntity furnace =
                setUpFurnace(
                        helper, tool, new ItemStack(ItemRegistry.STRENGTHENING_STONE.get()));

        helper.runAtTickTime(
                10,
                () ->
                        assertFurnaceCooking(
                                helper, furnace, "expected strengthening furnace to progress"));
        helper.runAtTickTime(11, helper::succeed);
    }

    public static void strengtheningDoesNotLightForPlainTool(GameTestHelper helper) {
        FurnaceBlockEntity furnace =
                setUpFurnace(
                        helper,
                        new ItemStack(Items.DIAMOND_SWORD),
                        new ItemStack(ItemRegistry.STRENGTHENING_STONE.get()));

        helper.runAtTickTime(
                10,
                () -> {
                    if (FurnaceHelper.getLitTime(furnace) > 0) {
                        helper.fail(Component.literal("plain tool should not consume strengthening stone"));
                    }
                    if (FurnaceHelper.getCookingProgress(furnace) > 0) {
                        helper.fail(
                                Component.literal(
                                        "plain tool should not gain furnace cooking progress"));
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail(
                                Component.literal(
                                        "plain tool should not white-burn strengthening stone"));
                    }
                });
        helper.runAtTickTime(11, helper::succeed);
    }

    public static void strengtheningStoneDoesNotContinueAnEnengCycle(GameTestHelper helper) {
        FurnaceBlockEntity furnace =
                setUpFurnace(
                        helper,
                        new ItemStack(Items.DIAMOND_SWORD),
                        new ItemStack(ItemRegistry.ENENG_STONE.get()));

        helper.runAtTickTime(
                190,
                () -> furnace.setItem(1, new ItemStack(ItemRegistry.STRENGTHENING_STONE.get())));
        helper.runAtTickTime(
                220,
                () -> {
                    ItemStack result = furnace.getItem(2);
                    if (!ItemModifierHelper.getBooleanTag(result, ENENGED_TAG_ID)) {
                        helper.fail(Component.literal("expected first eneng cycle to finish successfully"));
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail(Component.literal("strengthening stone was consumed to continue eneng"));
                    }
                    if (FurnaceHelper.getLitTime(furnace) > 0) {
                        helper.fail(Component.literal("wrong stone should not relight furnace after eneng"));
                    }
                });
        helper.runAtTickTime(221, helper::succeed);
    }

    public static void staleLastRecipeDoesNotLetStrengtheningStoneStart(GameTestHelper helper) {
        FurnaceBlockEntity furnace =
                setUpFurnace(
                        helper,
                        new ItemStack(Items.DIAMOND_SWORD),
                        new ItemStack(ItemRegistry.STRENGTHENING_STONE.get()));
        furnace.getPersistentData().putString("LastRecipe", "intensify:eneng_stone");
        furnace.setChanged();

        helper.runAtTickTime(
                10,
                () -> {
                    if (FurnaceHelper.getLitTime(furnace) > 0) {
                        helper.fail(
                                Component.literal(
                                        "stale last recipe state relit the furnace with strengthening"));
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail(
                                Component.literal(
                                        "stale last recipe state consumed strengthening stone"));
                    }
                });
        helper.runAtTickTime(11, helper::succeed);
    }

    public static void blastFurnaceDoesNotRunIntensifyFlow(GameTestHelper helper) {
        helper.setBlock(FURNACE_POS, Blocks.BLAST_FURNACE);
        BlastFurnaceBlockEntity furnace =
                helper.getBlockEntity(FURNACE_POS, BlastFurnaceBlockEntity.class);
        furnace.setItem(0, new ItemStack(Items.DIAMOND_SWORD));
        furnace.setItem(1, new ItemStack(ItemRegistry.ENENG_STONE.get()));
        furnace.setChanged();

        helper.runAtTickTime(
                10,
                () -> {
                    if (FurnaceHelper.getLitTime(furnace) > 0) {
                        helper.fail(Component.literal("blast furnace should not start intensify flow"));
                    }
                    if (FurnaceHelper.getCookingProgress(furnace) > 0) {
                        helper.fail(
                                Component.literal("blast furnace should not gain intensify progress"));
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail(Component.literal("blast furnace should not consume eneng stone"));
                    }
                });
        helper.runAtTickTime(11, helper::succeed);
    }

    public static void recipeBookDisplayRecipesLoadAndRemainNonExecutable(GameTestHelper helper) {
        assertDisplayRecipe(helper, "recipe_book_display/eneng_stone", ItemRegistry.ENENG_STONE.get().getDefaultInstance());
        assertDisplayRecipe(
                helper,
                "recipe_book_display/strengthening_stone",
                ItemRegistry.STRENGTHENING_STONE.get().getDefaultInstance());
        assertDisplayRecipe(helper, "recipe_book_display/eternal_stone", ItemRegistry.ETERNAL_STONE.get().getDefaultInstance());
        helper.succeed();
    }

    public static void playerLoginAwardsDisplayRecipesAndRemovesLegacyEntries(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        resetRecipes(helper, player, "recipe_book_display/eneng_stone");
        resetRecipes(helper, player, "recipe_book_display/strengthening_stone");
        resetRecipes(helper, player, "recipe_book_display/eternal_stone");
        IntentsifyRecipeBookAsserts.assertMissing(helper, player, "recipe_book_display/eneng_stone");
        IntentsifyRecipeBookAsserts.assertMissing(helper, player, "recipe_book_display/strengthening_stone");
        IntentsifyRecipeBookAsserts.assertMissing(helper, player, "recipe_book_display/eternal_stone");

        awardLegacyRecipe(helper, player, "eneng_stone");
        awardLegacyRecipe(helper, player, "strengthening_stone");
        awardLegacyRecipe(helper, player, "intensify_stone");

        new IntensifyForgeEventHandler().onPlayerLogin(new PlayerEvent.PlayerLoggedInEvent(player));

        IntentsifyRecipeBookAsserts.assertPresent(helper, player, "recipe_book_display/eneng_stone");
        IntentsifyRecipeBookAsserts.assertPresent(helper, player, "recipe_book_display/strengthening_stone");
        IntentsifyRecipeBookAsserts.assertPresent(helper, player, "recipe_book_display/eternal_stone");
        IntentsifyRecipeBookAsserts.assertMissing(helper, player, "eneng_stone");
        IntentsifyRecipeBookAsserts.assertMissing(helper, player, "strengthening_stone");
        IntentsifyRecipeBookAsserts.assertMissing(helper, player, "intensify_stone");
        helper.succeed();
    }

    public static void recipeBookDisplayPlacementOnlyFillsFuelSlot(GameTestHelper helper) {
        helper.setBlock(FURNACE_POS, Blocks.FURNACE);
        FurnaceBlockEntity furnace = helper.getBlockEntity(FURNACE_POS, FurnaceBlockEntity.class);
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().add(new ItemStack(Items.DIAMOND_SWORD));
        player.getInventory().add(ItemRegistry.ENENG_STONE.get().getDefaultInstance());
        int stoneCountBeforePlacement = player.getInventory().countItem(ItemRegistry.ENENG_STONE.get());

        FurnaceMenu menu =
                new FurnaceMenu(0, player.getInventory(), furnace, new SimpleContainerData(4));
        RecipeHolder<?> recipeHolder =
                recipeHolder(helper, "recipe_book_display/eneng_stone")
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "missing display recipe for placement test"));

        RecipeBookMenu.PostPlaceAction action =
                menu.handlePlacement(false, false, recipeHolder, helper.getLevel(), player.getInventory());

        if (!menu.getSlot(0).getItem().isEmpty()) {
            helper.fail(
                    Component.literal(
                            "display recipe placement should not write to furnace input slot"));
            return;
        }

        if (!menu.getSlot(1).getItem().is(ItemRegistry.ENENG_STONE.get())) {
            helper.fail(
                    Component.literal(
                            "display recipe placement should put the intensify stone in the fuel slot"));
            return;
        }

        int stoneCountAfterPlacement = player.getInventory().countItem(ItemRegistry.ENENG_STONE.get());
        if (stoneCountAfterPlacement != stoneCountBeforePlacement - 1) {
            helper.fail(
                    Component.literal(
                            "non-creative display placement should consume exactly one intensify stone"));
            return;
        }

        if (action != RecipeBookMenu.PostPlaceAction.NOTHING) {
            helper.fail(
                    Component.literal(
                            "successful fuel-only placement should not request ghost recipe feedback"));
            return;
        }

        helper.succeed();
    }

    public static void mineralBlockDropsAddConfiguredStones(GameTestHelper helper) {
        double originalTotalRate = ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().get();
        double originalStrengtheningRate =
                ConfigRegistry.stoneDropoutProbabilityConfig
                        .getStoneRate(IntensifyStoneType.STRENGTHENING_STONE)
                        .get();
        double originalEnengRate =
                ConfigRegistry.stoneDropoutProbabilityConfig
                        .getStoneRate(IntensifyStoneType.ENENG_STONE)
                        .get();
        double originalProtectionRate =
                ConfigRegistry.stoneDropoutProbabilityConfig
                        .getStoneRate(IntensifyStoneType.PROTECTION_STONE)
                        .get();
        double originalEternalRate =
                ConfigRegistry.stoneDropoutProbabilityConfig
                        .getStoneRate(IntensifyStoneType.ETERNAL_STONE)
                        .get();
        try {
            ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().set(100.0D);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.STRENGTHENING_STONE)
                    .set(1.0D);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.ENENG_STONE)
                    .set(0.0D);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.PROTECTION_STONE)
                    .set(0.0D);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.ETERNAL_STONE)
                    .set(0.0D);

            List<ItemStack> helperDrops =
                    IntensifyForgeEventHandler.createMiningStoneDrops(
                            Blocks.DIAMOND_ORE.defaultBlockState(), true, false);
            if (helperDrops.isEmpty()) {
                helper.fail(
                        Component.literal(
                                "expected direct mining helper to create strengthening stone, probability="
                                        + ConfigRegistry.stoneDropoutProbabilityConfig
                                                .getStoneDropOutProbability(
                                                        IntensifyStoneType.STRENGTHENING_STONE,
                                                        org.hhoa.mc.intensify.enums.DropTypeEnum
                                                                .MINERAL_BLOCK_DESTROYED,
                                                        Blocks.DIAMOND_ORE)));
                return;
            }

            BlockPos orePos = new BlockPos(2, 1, 1);
            helper.setBlock(orePos, Blocks.DIAMOND_ORE);
            ServerPlayer player = helper.makeMockServerPlayerInLevel();
            List<ItemEntity> drops = new ArrayList<>();
            BlockDropsEvent event =
                    new BlockDropsEvent(
                            helper.getLevel(),
                            orePos,
                            Blocks.DIAMOND_ORE.defaultBlockState(),
                            null,
                            drops,
                            player,
                            new ItemStack(Items.IRON_PICKAXE));

            new IntensifyForgeEventHandler().onBlockDrops(event);

            boolean foundStone =
                    drops.stream()
                            .map(ItemEntity::getItem)
                            .anyMatch(stack -> stack.is(ItemRegistry.STRENGTHENING_STONE.get()));
            if (!foundStone) {
                helper.fail(
                        Component.literal(
                                "expected block drops event to contain strengthening stone, got "
                                        + drops));
                return;
            }
        } finally {
            ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().set(originalTotalRate);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.STRENGTHENING_STONE)
                    .set(originalStrengtheningRate);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.ENENG_STONE)
                    .set(originalEnengRate);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.PROTECTION_STONE)
                    .set(originalProtectionRate);
            ConfigRegistry.stoneDropoutProbabilityConfig
                    .getStoneRate(IntensifyStoneType.ETERNAL_STONE)
                    .set(originalEternalRate);
        }

        helper.succeed();
    }

    public static void register(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition> environment =
                event.registerEnvironment(
                        ResourceLocation.fromNamespaceAndPath(MODID, "default"),
                        new TestEnvironmentDefinition.AllOf(List.of()));

        register(event, "eneng_starts_cooking_for_plain_tool", 80, environment, FurnaceGameTests::enengStartsCookingForPlainTool);
        register(event, "eneng_finishes_and_marks_tool", 260, environment, FurnaceGameTests::enengFinishesAndMarksTool);
        register(event, "empty_furnace_starts_when_items_added_later", 80, environment, FurnaceGameTests::emptyFurnaceStartsWhenItemsAddedLater);
        register(event, "strengthening_starts_cooking_for_enenged_tool", 80, environment, FurnaceGameTests::strengtheningStartsCookingForEnengedTool);
        register(event, "strengthening_does_not_light_for_plain_tool", 80, environment, FurnaceGameTests::strengtheningDoesNotLightForPlainTool);
        register(event, "strengthening_stone_does_not_continue_an_eneng_cycle", 260, environment, FurnaceGameTests::strengtheningStoneDoesNotContinueAnEnengCycle);
        register(event, "stale_last_recipe_does_not_let_strengthening_stone_start", 80, environment, FurnaceGameTests::staleLastRecipeDoesNotLetStrengtheningStoneStart);
        register(event, "blast_furnace_does_not_run_intensify_flow", 80, environment, FurnaceGameTests::blastFurnaceDoesNotRunIntensifyFlow);
        register(event, "recipe_book_display_recipes_load_and_remain_non_executable", 40, environment, FurnaceGameTests::recipeBookDisplayRecipesLoadAndRemainNonExecutable);
        register(event, "player_login_awards_display_recipes_and_removes_legacy_entries", 40, environment, FurnaceGameTests::playerLoginAwardsDisplayRecipesAndRemovesLegacyEntries);
        register(event, "recipe_book_display_placement_only_fills_fuel_slot", 40, environment, FurnaceGameTests::recipeBookDisplayPlacementOnlyFillsFuelSlot);
        register(event, "mineral_block_drops_add_configured_stones", 40, environment, FurnaceGameTests::mineralBlockDropsAddConfiguredStones);
    }

    private static FurnaceBlockEntity setUpFurnace(
            GameTestHelper helper, ItemStack tool, ItemStack fuel) {
        helper.setBlock(FURNACE_POS, Blocks.FURNACE);
        FurnaceBlockEntity furnace = helper.getBlockEntity(FURNACE_POS, FurnaceBlockEntity.class);
        furnace.setItem(0, tool);
        furnace.setItem(1, fuel);
        furnace.setChanged();
        return furnace;
    }

    private static void assertFurnaceCooking(
            GameTestHelper helper, FurnaceBlockEntity furnace, String message) {
        if (FurnaceHelper.getLitTime(furnace) <= 0) {
            helper.fail(Component.literal(message + ": furnace did not light"));
        }
        if (FurnaceHelper.getCookingTotalTime(furnace) <= 0) {
            helper.fail(Component.literal(message + ": cooking total time stayed at zero"));
        }
        if (FurnaceHelper.getCookingProgress(furnace) <= 0) {
            helper.fail(Component.literal(message + ": cooking progress never started"));
        }
    }

    private static void assertDisplayRecipe(GameTestHelper helper, String path, ItemStack expectedFuel) {
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(MODID, path);
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
        Optional<?> optionalHolder = helper.getLevel().getServer().getRecipeManager().byKey(recipeKey);
        if (optionalHolder.isEmpty()) {
            helper.fail(Component.literal("missing display recipe: " + recipeId));
            return;
        }

        RecipeHolder<?> holder = (RecipeHolder<?>) optionalHolder.get();
        if (!(holder.value() instanceof FurnaceRecipeBookDisplayRecipe displayRecipe)) {
            helper.fail(Component.literal("expected display recipe type for: " + recipeId));
            return;
        }

        if (displayRecipe.matches(new SingleRecipeInput(new ItemStack(Items.DIAMOND_SWORD)), helper.getLevel())) {
            helper.fail(Component.literal("display recipe should never match live furnace input: " + recipeId));
            return;
        }

        List<RecipeDisplay> displays = displayRecipe.display();
        if (displays.size() != 1) {
            helper.fail(Component.literal("display recipe should expose exactly one recipe book display: " + recipeId));
            return;
        }

        if (!(displays.get(0) instanceof FurnaceRecipeDisplay furnaceDisplay)) {
            helper.fail(Component.literal("display recipe should expose FurnaceRecipeDisplay: " + recipeId));
            return;
        }

        if (!(furnaceDisplay.fuel() instanceof SlotDisplay.ItemStackSlotDisplay fuelDisplay)) {
            helper.fail(Component.literal("display recipe should keep explicit fuel display instead of AnyFuel: " + recipeId));
            return;
        }

        if (!ItemStack.isSameItemSameComponents(fuelDisplay.stack(), expectedFuel)) {
            helper.fail(
                    Component.literal(
                            "display recipe fuel did not match expected intensify stone: "
                                    + recipeId
                                    + " -> "
                                    + fuelDisplay.stack()));
        }
    }

    private static void awardLegacyRecipe(GameTestHelper helper, ServerPlayer player, String path) {
        Optional<RecipeHolder<?>> recipeHolder = recipeHolder(helper, path);
        if (recipeHolder.isEmpty()) {
            helper.fail(
                    Component.literal(
                            "missing legacy recipe for test: "
                                    + recipeKey(path).location()));
            return;
        }
        player.awardRecipes(List.of(recipeHolder.get()));
    }

    private static void resetRecipes(GameTestHelper helper, ServerPlayer player, String path) {
        recipeHolder(helper, path).ifPresent(holder -> player.resetRecipes(List.of(holder)));
    }

    private static Optional<RecipeHolder<?>> recipeHolder(GameTestHelper helper, String path) {
        return helper.getLevel().getServer().getRecipeManager().byKey(recipeKey(path));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(
                Registries.RECIPE,
                ResourceLocation.fromNamespaceAndPath(MODID, path));
    }

    private static final class IntentsifyRecipeBookAsserts {
        private static void assertPresent(GameTestHelper helper, ServerPlayer player, String path) {
            ResourceKey<Recipe<?>> recipeKey = recipeKey(path);
            if (!player.getRecipeBook().contains(recipeKey)) {
                helper.fail(Component.literal("expected player recipe book to contain " + recipeKey.location()));
            }
        }

        private static void assertMissing(GameTestHelper helper, ServerPlayer player, String path) {
            ResourceKey<Recipe<?>> recipeKey = recipeKey(path);
            if (player.getRecipeBook().contains(recipeKey)) {
                helper.fail(Component.literal("expected player recipe book to exclude " + recipeKey.location()));
            }
        }
    }

    private static void register(
            RegisterGameTestsEvent event,
            String id,
            int timeoutTicks,
            Holder<TestEnvironmentDefinition> environment,
            Consumer<GameTestHelper> test) {
        ResourceLocation testId = ResourceLocation.fromNamespaceAndPath(MODID, id);
        TestData<Holder<TestEnvironmentDefinition>> testData =
                new TestData<>(environment, TEMPLATE_ID, timeoutTicks, 0, true);
        event.registerTest(
                testId,
                data ->
                        new FunctionGameTestInstance(BuiltinTestFunctions.ALWAYS_PASS, data) {
                            @Override
                            public void run(GameTestHelper helper) {
                                test.accept(helper);
                            }
                        },
                testData);
    }
}
