package org.hhoa.mc.intensify;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntensifyForgeEventHandlerTest {
    @BeforeClass
    public static void setupMinecraft() {
        if (!Bootstrap.isRegistered()) {
            Bootstrap.register();
        }
    }

    @Before
    public void setupConfig() throws Exception {
        configureDeterministicMiningDrops(ConfigRegistry.stoneDropoutProbabilityConfig);
    }

    @Test
    public void miningOreCreatesIndependentStoneDropsWhenProbabilityAllows() {
        List<ItemStack> drops =
                IntensifyForgeEventHandler
                        .createMiningStoneDrops(
                                Blocks.DIAMOND_ORE.getDefaultState(), true, false, false);

        Assert.assertEquals(4, drops.size());
        Assert.assertTrue(containsItem(drops, ItemRegistry.STRENGTHENING_STONE));
        Assert.assertTrue(containsItem(drops, ItemRegistry.ENENG_STONE));
        Assert.assertTrue(containsItem(drops, ItemRegistry.ETERNAL_STONE));
        Assert.assertTrue(containsItem(drops, ItemRegistry.PROTECTION_STONE));
    }

    @Test
    public void miningOreSkipsPlayerPlacedBlocks() {
        Assert.assertFalse(
                IntensifyForgeEventHandler
                        .createMiningStoneDrops(
                                Blocks.DIAMOND_ORE.getDefaultState(), true, false, true)
                        .iterator()
                        .hasNext());
    }

    @Test
    public void miningOreSkipsSilkTouch() {
        Assert.assertFalse(
                IntensifyForgeEventHandler
                        .createMiningStoneDrops(
                                Blocks.DIAMOND_ORE.getDefaultState(), true, true, false)
                        .iterator()
                        .hasNext());
    }

    @Test
    public void mobKillUsesConfiguredEntityKeyWhenPlayerDirectlyKills() {
        Assert.assertTrue(
                IntensifyForgeEventHandler
                        .createMobKillStoneDrop(
                                new ResourceLocation("minecraft", "villager_golem"), true, true)
                        .isPresent());
    }

    @Test
    public void mobKillSkipsIndirectPlayerDamage() {
        Assert.assertFalse(
                IntensifyForgeEventHandler
                        .createMobKillStoneDrop(
                                new ResourceLocation("minecraft", "villager_golem"), true, false)
                        .isPresent());
    }

    @SuppressWarnings("unchecked")
    private static void configureDeterministicMiningDrops(StoneDropoutProbabilityConfig config)
            throws Exception {
        Map<IntensifyStoneType, Double> stoneRate =
                (Map<IntensifyStoneType, Double>) getField(config, "stoneRate");
        stoneRate.clear();
        for (IntensifyStoneType stoneType : IntensifyStoneType.values()) {
            stoneRate.put(stoneType, 1.0D);
        }

        Map<DropTypeEnum, Double> defaultProbabilities =
                (Map<DropTypeEnum, Double>) getField(config, "defaultProbabilities");
        defaultProbabilities.clear();
        defaultProbabilities.put(DropTypeEnum.MINERAL_BLOCK_DESTROYED, 0.0D);

        Map<DropTypeEnum, Map<String, Double>> stoneDropProbability =
                (Map<DropTypeEnum, Map<String, Double>>) getField(config, "stoneDropProbability");
        stoneDropProbability.clear();
        Map<String, Double> mining = new LinkedHashMap<>();
        mining.put("minecraft:diamond_ore", 1.0D);
        stoneDropProbability.put(DropTypeEnum.MINERAL_BLOCK_DESTROYED, mining);
        Map<String, Double> mobs = new LinkedHashMap<>();
        mobs.put("minecraft:villager_golem", 1.0D);
        stoneDropProbability.put(DropTypeEnum.MOB_KILLED, mobs);

        ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().set(1.0D);
    }

    private static Object getField(StoneDropoutProbabilityConfig config, String name)
            throws Exception {
        Field field = StoneDropoutProbabilityConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(config);
    }

    private static boolean containsItem(List<ItemStack> drops, net.minecraft.item.Item item) {
        for (ItemStack drop : drops) {
            if (drop.getItem() == item) {
                return true;
            }
        }
        return false;
    }
}
