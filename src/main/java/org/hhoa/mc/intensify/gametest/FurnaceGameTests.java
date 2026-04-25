package org.hhoa.mc.intensify.gametest;

import static org.hhoa.mc.intensify.Intensify.MODID;
import static org.hhoa.mc.intensify.config.IntensifyConstants.ENENGED_TAG_ID;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.BuiltinTestFunctions;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import org.hhoa.mc.intensify.registry.ItemRegistry;
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
