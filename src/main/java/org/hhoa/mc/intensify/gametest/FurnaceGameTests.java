package org.hhoa.mc.intensify.gametest;

import static org.hhoa.mc.intensify.Intensify.MODID;
import static org.hhoa.mc.intensify.config.IntensifyConstants.ENENGED_TAG_ID;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.util.FurnaceHelper;
import org.hhoa.mc.intensify.util.ItemModifierHelper;

@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
public final class FurnaceGameTests {
    private static final BlockPos FURNACE_POS = new BlockPos(1, 1, 1);

    private FurnaceGameTests() {}

    @GameTest(template = "furnace_progress", timeoutTicks = 80)
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

    @GameTest(template = "furnace_progress", timeoutTicks = 260)
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
                        helper.fail("expected eneng furnace to produce a result");
                    }
                    if (!ItemModifierHelper.getBooleanTag(result, ENENGED_TAG_ID)) {
                        helper.fail("expected eneng furnace result to be marked as enenged");
                    }
                });
        helper.runAtTickTime(221, helper::succeed);
    }

    @GameTest(template = "furnace_progress", timeoutTicks = 80)
    public static void emptyFurnaceStartsWhenItemsAddedLater(GameTestHelper helper) {
        helper.setBlock(FURNACE_POS, Blocks.FURNACE);
        FurnaceBlockEntity furnace = helper.getBlockEntity(FURNACE_POS);

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

    @GameTest(template = "furnace_progress", timeoutTicks = 80)
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

    @GameTest(template = "furnace_progress", timeoutTicks = 80)
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
                        helper.fail("plain tool should not consume strengthening stone");
                    }
                    if (FurnaceHelper.getCookingProgress(furnace) > 0) {
                        helper.fail("plain tool should not gain furnace cooking progress");
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail("plain tool should not white-burn strengthening stone");
                    }
                });
        helper.runAtTickTime(11, helper::succeed);
    }

    @GameTest(template = "furnace_progress", timeoutTicks = 260)
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
                        helper.fail("expected first eneng cycle to finish successfully");
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail("strengthening stone was consumed to continue eneng");
                    }
                    if (FurnaceHelper.getLitTime(furnace) > 0) {
                        helper.fail("wrong stone should not relight furnace after eneng");
                    }
                });
        helper.runAtTickTime(221, helper::succeed);
    }

    @GameTest(template = "furnace_progress", timeoutTicks = 80)
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
                        helper.fail("stale last recipe state relit the furnace with strengthening");
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail("stale last recipe state consumed strengthening stone");
                    }
                });
        helper.runAtTickTime(11, helper::succeed);
    }

    @GameTest(template = "furnace_progress", timeoutTicks = 80)
    public static void blastFurnaceDoesNotRunIntensifyFlow(GameTestHelper helper) {
        helper.setBlock(FURNACE_POS, Blocks.BLAST_FURNACE);
        BlastFurnaceBlockEntity furnace = helper.getBlockEntity(FURNACE_POS);
        furnace.setItem(0, new ItemStack(Items.DIAMOND_SWORD));
        furnace.setItem(1, new ItemStack(ItemRegistry.ENENG_STONE.get()));
        furnace.setChanged();

        helper.runAtTickTime(
                10,
                () -> {
                    if (FurnaceHelper.getLitTime(furnace) > 0) {
                        helper.fail("blast furnace should not start intensify flow");
                    }
                    if (FurnaceHelper.getCookingProgress(furnace) > 0) {
                        helper.fail("blast furnace should not gain intensify progress");
                    }
                    if (furnace.getItem(1).getCount() != 1) {
                        helper.fail("blast furnace should not consume eneng stone");
                    }
                });
        helper.runAtTickTime(11, helper::succeed);
    }

    private static FurnaceBlockEntity setUpFurnace(
            GameTestHelper helper, ItemStack tool, ItemStack fuel) {
        helper.setBlock(FURNACE_POS, Blocks.FURNACE);
        FurnaceBlockEntity furnace = helper.getBlockEntity(FURNACE_POS);
        furnace.setItem(0, tool);
        furnace.setItem(1, fuel);
        furnace.setChanged();
        return furnace;
    }

    private static void assertFurnaceCooking(
            GameTestHelper helper, FurnaceBlockEntity furnace, String message) {
        if (FurnaceHelper.getLitTime(furnace) <= 0) {
            helper.fail(message + ": furnace did not light");
        }
        if (FurnaceHelper.getCookingTotalTime(furnace) <= 0) {
            helper.fail(message + ": cooking total time stayed at zero");
        }
        if (FurnaceHelper.getCookingProgress(furnace) <= 0) {
            helper.fail(message + ": cooking progress never started");
        }
    }
}
