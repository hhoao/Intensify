package org.hhoa.mc.intensify.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.jetbrains.annotations.NotNull;

public class StoneDropoutProbabilityConfig {
    private final HashMap<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>> stoneRate;

    private final HashMap<DropTypeEnum, ForgeConfigSpec.ConfigValue<Double>> defaultProbabilities;

    private final ForgeConfigSpec.ConfigValue<Double> totalRate;

    private final Map<DropTypeEnum, Map<String, ForgeConfigSpec.ConfigValue<Double>>>
            stoneDropProbability;

    public static Pair<ForgeConfigSpec, StoneDropoutProbabilityConfig> create() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        StoneDropoutProbabilityConfig stoneDropoutProbabilityConfig =
                new StoneDropoutProbabilityConfig(builder);
        ForgeConfigSpec build = builder.build();
        return Pair.of(build, stoneDropoutProbabilityConfig);
    }

    public ForgeConfigSpec.ConfigValue<Double> getTotalRate() {
        return totalRate;
    }

    public Optional<Item> dropStone(DropTypeEnum dropTypeEnum, Object key) {
        Map<?, Double> probabilities = getEntityStoneDropOutProbabilities(dropTypeEnum);
        Double probability =
                probabilities.getOrDefault(key, defaultProbabilities.get(dropTypeEnum).get())
                        * totalRate.get();

        double v = ThreadLocalRandom.current().nextDouble();
        if (v <= probability) {
            double totalValue = 0;
            for (Map.Entry<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>> entry :
                    stoneRate.entrySet()) {
                ForgeConfigSpec.ConfigValue<Double> value = entry.getValue();
                totalValue += value.get();
            }
            double v1 = probability / totalValue;
            for (Map.Entry<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>> entry :
                    stoneRate.entrySet()) {
                if (v <= v1 * entry.getValue().get()) {
                    IntensifyStoneType key1 = entry.getKey();
                    switch (key1) {
                        case ENENG_STONE:
                            return Optional.of(ItemRegistry.ENENG_STONE.get());
                        case STRENGTHENING_STONE:
                            return Optional.of(ItemRegistry.STRENGTHENING_STONE.get());
                        case ETERNAL_STONE:
                            return Optional.of(ItemRegistry.ETERNAL_STONE.get());
                        case PROTECTION_STONE:
                            return Optional.of(ItemRegistry.PROTECTION_STONE.get());
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Double getStoneDropOutProbability(
            IntensifyStoneType intensifyStone, DropTypeEnum dropTypeEnum, Object key) {
        Map<?, Double> probabilities = getEntityStoneDropOutProbabilities(dropTypeEnum);
        Double stoneRate = this.stoneRate.get(intensifyStone).get();
        Double stoneDropProperties =
                probabilities.getOrDefault(key, defaultProbabilities.get(dropTypeEnum).get())
                        * totalRate.get();
        return stoneDropProperties * stoneRate;
    }

    private Map<?, Double> getEntityStoneDropOutProbabilities(DropTypeEnum dropTypeEnum) {
        Map<String, ForgeConfigSpec.ConfigValue<Double>> mineralBlocksProbability =
                stoneDropProbability.get(dropTypeEnum);
        Map<Object, Double> stoneDropoutProbability = new HashMap<>();
        for (Map.Entry<String, ForgeConfigSpec.ConfigValue<Double>> blockNameWithProbability :
                mineralBlocksProbability.entrySet()) {
            ResourceLocation resourceLocation =
                    new ResourceLocation(blockNameWithProbability.getKey());
            Object value;
            if (dropTypeEnum == DropTypeEnum.MINERAL_BLOCK_DESTROYED) {
                value = ForgeRegistries.BLOCKS.getValue(resourceLocation);
            } else if (dropTypeEnum == DropTypeEnum.FISHING) {
                value = ForgeRegistries.ITEMS.getValue(resourceLocation);
            } else if (dropTypeEnum == DropTypeEnum.MOB_KILLED) {
                value = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
            } else {
                throw new RuntimeException("No such drop type");
            }
            stoneDropoutProbability.put(value, blockNameWithProbability.getValue().get());
        }
        return stoneDropoutProbability;
    }

    private StoneDropoutProbabilityConfig(ForgeConfigSpec.Builder builder) {
        builder.push("probabilities");

        // 矿石概率配置
        stoneDropProbability = new HashMap<>();
        configureMineProbability(builder);

        // 钓鱼概率配置
        configureFishing(builder);

        // 生物概率配置
        configureMob(builder);

        defaultProbabilities = configureDefault(builder);
        totalRate = builder.define("total_rate", 1.0);
        stoneRate = configureStone(builder);
    }

    private @NotNull HashMap<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>>
            configureStone(ForgeConfigSpec.Builder builder) {
        final HashMap<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>> stoneRate;
        builder.push("stone_rate");
        stoneRate = new HashMap<>();
        stoneRate.put(
                IntensifyStoneType.STRENGTHENING_STONE,
                builder.define(IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(), 1.0));
        stoneRate.put(
                IntensifyStoneType.ENENG_STONE,
                builder.define(IntensifyStoneType.ENENG_STONE.getIdentifier(), 0.1));
        stoneRate.put(
                IntensifyStoneType.ETERNAL_STONE,
                builder.define(IntensifyStoneType.ETERNAL_STONE.getIdentifier(), 0.001));
        stoneRate.put(
                IntensifyStoneType.PROTECTION_STONE,
                builder.define(IntensifyStoneType.PROTECTION_STONE.getIdentifier(), 0.01));
        builder.pop();
        return stoneRate;
    }

    private @NotNull HashMap<DropTypeEnum, ForgeConfigSpec.ConfigValue<Double>> configureDefault(
            ForgeConfigSpec.Builder builder) {
        final HashMap<DropTypeEnum, ForgeConfigSpec.ConfigValue<Double>> defaultProbabilities;
        builder.push("defaults");
        defaultProbabilities = new HashMap<>();
        ForgeConfigSpec.ConfigValue<Double> defaultFishing =
                builder.define(DropTypeEnum.FISHING.getIdentify(), 0.045);
        ForgeConfigSpec.ConfigValue<Double> defaultMineralBlockDestroyed =
                builder.define(DropTypeEnum.MINERAL_BLOCK_DESTROYED.getIdentify(), 0.0);
        ForgeConfigSpec.ConfigValue<Double> defaultMobKilled =
                builder.define(DropTypeEnum.MOB_KILLED.getIdentify(), 0.015);

        defaultProbabilities.put(DropTypeEnum.FISHING, defaultFishing);
        defaultProbabilities.put(
                DropTypeEnum.MINERAL_BLOCK_DESTROYED, defaultMineralBlockDestroyed);
        defaultProbabilities.put(DropTypeEnum.MOB_KILLED, defaultMobKilled);

        builder.pop();
        return defaultProbabilities;
    }

    private void configureMob(ForgeConfigSpec.Builder builder) {
        builder.push(DropTypeEnum.MOB_KILLED.getIdentify());
        HashMap<String, Double> mobsProbability = new HashMap<>();
        mobsProbability.put(getRegistryName(EntityType.WITHER), 10.0);
        mobsProbability.put(getRegistryName(EntityType.ENDER_DRAGON), 100.0);
        mobsProbability.put(getRegistryName(EntityType.SKELETON_HORSE), 0.8);
        mobsProbability.put(getRegistryName(EntityType.ZOMBIE_HORSE), 0.8);
        mobsProbability.put(getRegistryName(EntityType.IRON_GOLEM), 0.05);
        mobsProbability.put(getRegistryName(EntityType.DOLPHIN), 0.08);
        mobsProbability.put(getRegistryName(EntityType.GUARDIAN), 0.05);
        mobsProbability.put(getRegistryName(EntityType.ELDER_GUARDIAN), 0.05);
        mobsProbability.put(getRegistryName(EntityType.PARROT), 0.05);
        mobsProbability.put(getRegistryName(EntityType.FOX), 0.05);
        mobsProbability.put(getRegistryName(EntityType.PANDA), 0.05);
        mobsProbability.put(getRegistryName(EntityType.POLAR_BEAR), 0.05);
        mobsProbability.put(getRegistryName(EntityType.ALLAY), 0.05);
        mobsProbability.put(getRegistryName(EntityType.SLIME), 0.02);
        mobsProbability.put(getRegistryName(EntityType.WARDEN), 0.4);
        Map<String, ForgeConfigSpec.ConfigValue<Double>> mobProbability =
                createProbabilityConfig(builder, mobsProbability);
        stoneDropProbability.put(DropTypeEnum.MOB_KILLED, mobProbability);
        builder.pop();
    }

    private void configureFishing(ForgeConfigSpec.Builder builder) {
        builder.push(DropTypeEnum.FISHING.getIdentify());
        Map<String, ForgeConfigSpec.ConfigValue<Double>> fishProbability =
                createProbabilityConfig(
                        builder,
                        Map.of(
                                getRegistryName(Items.COD), 0.02,
                                getRegistryName(Items.SALMON), 0.04,
                                getRegistryName(Items.TROPICAL_FISH), 0.2,
                                getRegistryName(Items.PUFFERFISH), 0.08));
        stoneDropProbability.put(DropTypeEnum.FISHING, fishProbability);
        builder.pop();
    }

    private void configureMineProbability(ForgeConfigSpec.Builder builder) {
        builder.push(DropTypeEnum.MINERAL_BLOCK_DESTROYED.getIdentify());

        HashMap<String, Double> blocksProbability = new HashMap<>();
        blocksProbability.put(getRegistryName(Blocks.COAL_ORE), 0.02);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_COAL_ORE), 0.025);

        blocksProbability.put(getRegistryName(Blocks.COPPER_ORE), 0.025);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_COPPER_ORE), 0.03);

        blocksProbability.put(getRegistryName(Blocks.IRON_ORE), 0.04);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_IRON_ORE), 0.045);

        blocksProbability.put(getRegistryName(Blocks.GOLD_ORE), 0.05);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_GOLD_ORE), 0.055);
        blocksProbability.put(getRegistryName(Blocks.NETHER_GOLD_ORE), 0.03);

        blocksProbability.put(getRegistryName(Blocks.REDSTONE_ORE), 0.03);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_REDSTONE_ORE), 0.035);

        blocksProbability.put(getRegistryName(Blocks.LAPIS_ORE), 0.03);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_LAPIS_ORE), 0.035);

        blocksProbability.put(getRegistryName(Blocks.DIAMOND_ORE), 0.1);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_DIAMOND_ORE), 0.105);

        blocksProbability.put(getRegistryName(Blocks.EMERALD_ORE), 0.1);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_EMERALD_ORE), 0.105);

        blocksProbability.put(getRegistryName(Blocks.NETHER_QUARTZ_ORE), 0.01);

        blocksProbability.put(getRegistryName(Blocks.ANCIENT_DEBRIS), 0.2);

        Map<String, ForgeConfigSpec.ConfigValue<Double>> mineralBlocksProbability =
                createProbabilityConfig(builder, blocksProbability);
        stoneDropProbability.put(DropTypeEnum.MINERAL_BLOCK_DESTROYED, mineralBlocksProbability);
        builder.pop();
    }

    private Map<String, ForgeConfigSpec.ConfigValue<Double>> createProbabilityConfig(
            ForgeConfigSpec.Builder builder, Map<String, Double> defaults) {
        Map<String, ForgeConfigSpec.ConfigValue<Double>> configMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : defaults.entrySet()) {
            configMap.put(
                    entry.getKey(),
                    builder.comment("Probability for " + entry.getKey())
                            .defineInRange(entry.getKey(), entry.getValue(), 0.0, 1.0));
        }
        return configMap;
    }

    private static String getRegistryName(Object registryObject) {
        if (registryObject instanceof net.minecraft.world.level.block.Block block) {
            return ForgeRegistries.BLOCKS.getKey(block).toString();
        } else if (registryObject instanceof net.minecraft.world.item.Item item) {
            return ForgeRegistries.ITEMS.getKey(item).toString();
        } else if (registryObject instanceof net.minecraft.world.entity.EntityType<?> entityType) {
            return ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString();
        }
        throw new IllegalArgumentException("Unsupported registry object: " + registryObject);
    }
}
