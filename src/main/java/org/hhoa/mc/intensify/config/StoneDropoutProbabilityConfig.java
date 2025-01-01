package org.hhoa.mc.intensify.config;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class StoneDropoutProbabilityConfig {
    private final HashMap<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>> stoneRate;

    private final HashMap<DropTypeEnum, ForgeConfigSpec.ConfigValue<Double>> defaultProbabilities;

    private final ForgeConfigSpec.ConfigValue<Integer> totalRate;

    public HashMap<DropTypeEnum, ForgeConfigSpec.ConfigValue<Double>> getDefaultProbabilities() {
        return defaultProbabilities;
    }

    public ForgeConfigSpec.ConfigValue<Integer> getTotalRate() {
        return totalRate;
    }

    protected static Pair<ForgeConfigSpec, StoneDropoutProbabilityConfig> create() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        StoneDropoutProbabilityConfig stoneDropoutProbabilityConfig = new StoneDropoutProbabilityConfig(builder);
        ForgeConfigSpec build = builder.build();
        return Pair.of(build, stoneDropoutProbabilityConfig);
    }

    public Double getStoneDropoutProbability(IntensifyStoneType intensifyStone,
                                             DropTypeEnum dropTypeEnum,
                                             Object key) {
        Map<?, Double> probabilities = getProbabilities(dropTypeEnum);

        Double rate = stoneRate.get(intensifyStone).get();
        return probabilities.getOrDefault(key, rate);
    }

    public Optional<Item> dropStone(DropTypeEnum dropTypeEnum,
                                    Object key) {
        Map<?, Double> probabilities = getProbabilities(dropTypeEnum);
        Double probability = probabilities.get(key);

        double v = ThreadLocalRandom.current().nextDouble();
        if (v <= probability * totalRate.get()) {
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

    public Map<?, Double> getStoneProbabilities(IntensifyStoneType intensifyStone,
                                                DropTypeEnum dropTypeEnum) {
        Map<?, Double> probabilities = getProbabilities(dropTypeEnum);
        Double rate = stoneRate.get(intensifyStone).get();
        for (Map.Entry<?, Double> doubleEntry : probabilities.entrySet()) {
            doubleEntry.setValue(doubleEntry.getValue() * rate);
        }

        return probabilities;
    }


    private final Map<DropTypeEnum, Map<String, ForgeConfigSpec.ConfigValue<Double>>> stoneDropProbability;

    public HashMap<IntensifyStoneType, ForgeConfigSpec.ConfigValue<Double>> getStoneRate() {
        return stoneRate;
    }

    public Map<DropTypeEnum, Map<String, ForgeConfigSpec.ConfigValue<Double>>> getStoneDropProbability() {
        return stoneDropProbability;
    }

    public Map<?, Double> getProbabilities(DropTypeEnum dropTypeEnum) {
        StoneDropoutProbabilityConfig configValueMap = Config.getStoneDropoutProbabilityConfig();
        Map<String, ForgeConfigSpec.ConfigValue<Double>> mineralBlocksProbability =
            configValueMap.getStoneDropProbability().get(dropTypeEnum);
        Map<Object, Double> stoneDropoutProbability = new HashMap<>();
        for (Map.Entry<String, ForgeConfigSpec.ConfigValue<Double>> blockNameWithProbability : mineralBlocksProbability.entrySet()) {
            ResourceLocation resourceLocation = new ResourceLocation(blockNameWithProbability.getKey());
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
        builder.push(DropTypeEnum.MINERAL_BLOCK_DESTROYED.getIdentify());
        stoneDropProbability = new HashMap<>();
        HashMap<String, Double> blocksProbability = new HashMap<>();

        blocksProbability.put(getRegistryName(Blocks.COAL_ORE), 0.001);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_COAL_ORE), 0.0015);
        blocksProbability.put(getRegistryName(Blocks.COPPER_ORE), 0.04);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_COPPER_ORE), 0.045);
        blocksProbability.put(getRegistryName(Blocks.IRON_ORE), 0.01);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_IRON_ORE), 0.015);
        blocksProbability.put(getRegistryName(Blocks.GOLD_ORE), 0.015);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_GOLD_ORE), 0.015);
        blocksProbability.put(getRegistryName(Blocks.REDSTONE_ORE), 0.002);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_REDSTONE_ORE), 0.0025);
        blocksProbability.put(getRegistryName(Blocks.LAPIS_ORE), 0.002);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_LAPIS_ORE), 0.002);
        blocksProbability.put(getRegistryName(Blocks.DIAMOND_ORE), 0.08);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_DIAMOND_ORE), 0.085);
        blocksProbability.put(getRegistryName(Blocks.EMERALD_ORE), 0.08);
        blocksProbability.put(getRegistryName(Blocks.DEEPSLATE_EMERALD_ORE), 0.085);
        blocksProbability.put(getRegistryName(Blocks.NETHER_QUARTZ_ORE), 0.001);
        blocksProbability.put(getRegistryName(Blocks.ANCIENT_DEBRIS), 0.);
        Map<String, ForgeConfigSpec.ConfigValue<Double>> mineralBlocksProbability =
            createProbabilityConfig(builder, blocksProbability);
        stoneDropProbability.put(DropTypeEnum.MINERAL_BLOCK_DESTROYED, mineralBlocksProbability);
        builder.pop();

        // 钓鱼概率配置
        builder.push(DropTypeEnum.FISHING.getIdentify());
        Map<String, ForgeConfigSpec.ConfigValue<Double>> fishProbability = createProbabilityConfig(builder, Map.of(
            getRegistryName(Items.COD), 0.01,
            getRegistryName(Items.SALMON), 0.01,
            getRegistryName(Items.TROPICAL_FISH), 0.01,
            getRegistryName(Items.PUFFERFISH), 0.01
        ));
        stoneDropProbability.put(DropTypeEnum.FISHING, fishProbability);
        builder.pop();

        // 生物概率配置
        builder.push(DropTypeEnum.MOB_KILLED.getIdentify());
        Map<String, ForgeConfigSpec.ConfigValue<Double>> mobProbability = createProbabilityConfig(builder, Map.of(
            getRegistryName(EntityType.ZOMBIE), 0.01
        ));
        stoneDropProbability.put(DropTypeEnum.MOB_KILLED, mobProbability);
        builder.pop();

        builder.push("defaults");
        defaultProbabilities = new HashMap<>();
        ForgeConfigSpec.ConfigValue<Double> defaultFishing =
            builder.define(DropTypeEnum.FISHING.getIdentify(), 0.004);
        ForgeConfigSpec.ConfigValue<Double> defaultMineralBlockDestroyed =
            builder.define(DropTypeEnum.MINERAL_BLOCK_DESTROYED.getIdentify(), 0.0);
        ForgeConfigSpec.ConfigValue<Double> defaultMobKilled =
            builder.define(DropTypeEnum.MOB_KILLED.getIdentify(), 0.002);

        defaultProbabilities.put(DropTypeEnum.FISHING, defaultFishing);
        defaultProbabilities.put(DropTypeEnum.MINERAL_BLOCK_DESTROYED, defaultMineralBlockDestroyed);
        defaultProbabilities.put(DropTypeEnum.MOB_KILLED, defaultMobKilled);

        builder.pop();

        totalRate = builder.define("total_rate", 1);
        builder.push("stone_rate");
        stoneRate = new HashMap<>();
        stoneRate.put(IntensifyStoneType.STRENGTHENING_STONE,
            builder.define(IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
                0.1));
        stoneRate.put(IntensifyStoneType.ENENG_STONE,
            builder.define(IntensifyStoneType.ENENG_STONE.getIdentifier(),
                0.05));
        stoneRate.put(IntensifyStoneType.ETERNAL_STONE,
            builder.define(IntensifyStoneType.ETERNAL_STONE.getIdentifier(),
                0.0001));
        stoneRate.put(IntensifyStoneType.PROTECTION_STONE,
            builder.define(IntensifyStoneType.PROTECTION_STONE.getIdentifier(),
                0.001));
        builder.pop();
    }

    private Map<String, ForgeConfigSpec.ConfigValue<Double>> createProbabilityConfig(
        ForgeConfigSpec.Builder builder, Map<String, Double> defaults) {
        Map<String, ForgeConfigSpec.ConfigValue<Double>> configMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : defaults.entrySet()) {
            configMap.put(entry.getKey(), builder
                .comment("Probability for " + entry.getKey())
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
