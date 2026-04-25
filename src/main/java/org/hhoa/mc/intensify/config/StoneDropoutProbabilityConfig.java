package org.hhoa.mc.intensify.config;

import java.io.File;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class StoneDropoutProbabilityConfig {
    private static final Map<IntensifyStoneType, Double> DEFAULT_STONE_RATE =
            new EnumMap<>(IntensifyStoneType.class);
    private static final Map<DropTypeEnum, Double> DEFAULT_DROP_TYPE_RATE =
            new EnumMap<>(DropTypeEnum.class);
    private static final Map<DropTypeEnum, Map<String, Double>> DEFAULT_DROP_PROBABILITIES =
            new EnumMap<>(DropTypeEnum.class);

    static {
        DEFAULT_STONE_RATE.put(IntensifyStoneType.STRENGTHENING_STONE, 1.0D);
        DEFAULT_STONE_RATE.put(IntensifyStoneType.ENENG_STONE, 0.1D);
        DEFAULT_STONE_RATE.put(IntensifyStoneType.ETERNAL_STONE, 0.001D);
        DEFAULT_STONE_RATE.put(IntensifyStoneType.PROTECTION_STONE, 0.01D);

        DEFAULT_DROP_TYPE_RATE.put(DropTypeEnum.FISHING, 0.045D);
        DEFAULT_DROP_TYPE_RATE.put(DropTypeEnum.MINERAL_BLOCK_DESTROYED, 0.0D);
        DEFAULT_DROP_TYPE_RATE.put(DropTypeEnum.MOB_KILLED, 0.015D);

        DEFAULT_DROP_PROBABILITIES.put(
                DropTypeEnum.MINERAL_BLOCK_DESTROYED, createMineDefaults());
        DEFAULT_DROP_PROBABILITIES.put(DropTypeEnum.FISHING, createFishingDefaults());
        DEFAULT_DROP_PROBABILITIES.put(DropTypeEnum.MOB_KILLED, createMobDefaults());
    }

    private final Map<IntensifyStoneType, Double> stoneRate = new EnumMap<>(IntensifyStoneType.class);
    private final Map<DropTypeEnum, Double> defaultProbabilities = new EnumMap<>(DropTypeEnum.class);
    private final Map<DropTypeEnum, Map<String, Double>> stoneDropProbability =
            new EnumMap<>(DropTypeEnum.class);
    private final ConfigRegistry.MutableDoubleValue totalRate =
            new ConfigRegistry.MutableDoubleValue("total_rate", 1.0D);
    private Configuration configuration;

    public void initialize(File file) {
        this.configuration = new Configuration(file);
        sync();
    }

    public ConfigRegistry.MutableDoubleValue getTotalRate() {
        return this.totalRate;
    }

    public void save() {
        if (this.configuration == null) {
            return;
        }
        writeCurrentValues();
        if (this.configuration.hasChanged()) {
            this.configuration.save();
        }
    }

    public Optional<Item> dropStone(DropTypeEnum dropTypeEnum, Object key) {
        double probability =
                getProbabilityFor(dropTypeEnum, key) * this.totalRate.get();
        if (ThreadLocalRandom.current().nextDouble() > probability) {
            return Optional.empty();
        }

        double totalWeight = 0.0D;
        for (double value : this.stoneRate.values()) {
            totalWeight += value;
        }
        if (totalWeight <= 0.0D) {
            return Optional.empty();
        }

        double selection = ThreadLocalRandom.current().nextDouble(totalWeight);
        double consumed = 0.0D;
        for (Map.Entry<IntensifyStoneType, Double> entry : this.stoneRate.entrySet()) {
            consumed += entry.getValue();
            if (selection <= consumed) {
                return Optional.of(resolveStone(entry.getKey()));
            }
        }

        return Optional.of(resolveStone(IntensifyStoneType.STRENGTHENING_STONE));
    }

    public Optional<Item> dropStone(
            IntensifyStoneType intensifyStone, DropTypeEnum dropTypeEnum, Object key) {
        double probability = getStoneDropOutProbability(intensifyStone, dropTypeEnum, key);
        if (ThreadLocalRandom.current().nextDouble() >= probability) {
            return Optional.empty();
        }
        return Optional.of(resolveStone(intensifyStone));
    }

    public Double getStoneDropOutProbability(
            IntensifyStoneType intensifyStone, DropTypeEnum dropTypeEnum, Object key) {
        double stoneWeight = this.stoneRate.get(intensifyStone);
        return getProbabilityFor(dropTypeEnum, key) * this.totalRate.get() * stoneWeight;
    }

    private void sync() {
        if (this.configuration == null) {
            return;
        }

        this.configuration.load();
        this.totalRate.set(readDouble("general", this.totalRate.getKey(), 1.0D));

        this.stoneRate.clear();
        for (Map.Entry<IntensifyStoneType, Double> entry : DEFAULT_STONE_RATE.entrySet()) {
            this.stoneRate.put(
                    entry.getKey(),
                    readDouble("stone_rate", entry.getKey().getIdentifier(), entry.getValue()));
        }

        this.defaultProbabilities.clear();
        for (Map.Entry<DropTypeEnum, Double> entry : DEFAULT_DROP_TYPE_RATE.entrySet()) {
            this.defaultProbabilities.put(
                    entry.getKey(),
                    readDouble("defaults", entry.getKey().getIdentify(), entry.getValue()));
        }

        this.stoneDropProbability.clear();
        for (Map.Entry<DropTypeEnum, Map<String, Double>> entry :
                DEFAULT_DROP_PROBABILITIES.entrySet()) {
            Map<String, Double> configured = new LinkedHashMap<>();
            for (Map.Entry<String, Double> probability : entry.getValue().entrySet()) {
                configured.put(
                        probability.getKey(),
                        readDouble(
                                entry.getKey().getIdentify(),
                                probability.getKey(),
                                probability.getValue()));
            }
            this.stoneDropProbability.put(entry.getKey(), configured);
        }

        save();
    }

    private void writeCurrentValues() {
        this.configuration
                .get("general", this.totalRate.getKey(), this.totalRate.getDefaultValue())
                .set(this.totalRate.get());

        for (Map.Entry<IntensifyStoneType, Double> entry : this.stoneRate.entrySet()) {
            this.configuration
                    .get("stone_rate", entry.getKey().getIdentifier(), entry.getValue())
                    .set(entry.getValue());
        }

        for (Map.Entry<DropTypeEnum, Double> entry : this.defaultProbabilities.entrySet()) {
            this.configuration
                    .get("defaults", entry.getKey().getIdentify(), entry.getValue())
                    .set(entry.getValue());
        }

        for (Map.Entry<DropTypeEnum, Map<String, Double>> entry : this.stoneDropProbability.entrySet()) {
            for (Map.Entry<String, Double> probability : entry.getValue().entrySet()) {
                this.configuration
                        .get(entry.getKey().getIdentify(), probability.getKey(), probability.getValue())
                        .set(probability.getValue());
            }
        }
    }

    private double getProbabilityFor(DropTypeEnum dropTypeEnum, Object key) {
        Map<String, Double> probabilities = this.stoneDropProbability.get(dropTypeEnum);
        String lookupKey = toLookupKey(key);
        if (lookupKey != null && probabilities.containsKey(lookupKey)) {
            return probabilities.get(lookupKey);
        }
        return this.defaultProbabilities.get(dropTypeEnum);
    }

    private double readDouble(String category, String key, double defaultValue) {
        return this.configuration.get(category, key, defaultValue).getDouble(defaultValue);
    }

    private static Item resolveStone(IntensifyStoneType type) {
        switch (type) {
            case ENENG_STONE:
                return ItemRegistry.ENENG_STONE;
            case ETERNAL_STONE:
                return ItemRegistry.ETERNAL_STONE;
            case PROTECTION_STONE:
                return ItemRegistry.PROTECTION_STONE;
            case STRENGTHENING_STONE:
            default:
                return ItemRegistry.STRENGTHENING_STONE;
        }
    }

    private static String toLookupKey(Object key) {
        if (key == null) {
            return null;
        }
        if (key instanceof ItemStack) {
            ItemStack stack = (ItemStack) key;
            ResourceLocation id = Item.REGISTRY.getNameForObject(stack.getItem());
            return id == null ? null : id.toString() + "." + stack.getMetadata();
        }
        if (key instanceof Block) {
            ResourceLocation id = Block.REGISTRY.getNameForObject((Block) key);
            return id == null ? null : id.toString();
        }
        if (key instanceof Item) {
            ResourceLocation id = Item.REGISTRY.getNameForObject((Item) key);
            return id == null ? null : id.toString();
        }
        if (key instanceof Entity) {
            ResourceLocation id = EntityList.getKey((Entity) key);
            return id == null ? null : id.toString();
        }
        if (key instanceof ResourceLocation) {
            return key.toString();
        }
        if (key instanceof String) {
            return (String) key;
        }
        return null;
    }

    private static Map<String, Double> createMineDefaults() {
        Map<String, Double> defaults = new LinkedHashMap<>();
        defaults.put(getRegistryName(Blocks.COAL_ORE), 0.02D);
        defaults.put(getRegistryName(Blocks.IRON_ORE), 0.04D);
        defaults.put(getRegistryName(Blocks.GOLD_ORE), 0.05D);
        defaults.put(getRegistryName(Blocks.REDSTONE_ORE), 0.03D);
        defaults.put(getRegistryName(Blocks.LAPIS_ORE), 0.03D);
        defaults.put(getRegistryName(Blocks.DIAMOND_ORE), 0.1D);
        defaults.put(getRegistryName(Blocks.EMERALD_ORE), 0.1D);
        defaults.put(getRegistryName(Blocks.QUARTZ_ORE), 0.01D);
        return defaults;
    }

    private static Map<String, Double> createFishingDefaults() {
        Map<String, Double> defaults = new LinkedHashMap<>();
        defaults.put(getRegistryName(new ItemStack(Items.FISH, 1, 0)), 0.02D);
        defaults.put(getRegistryName(new ItemStack(Items.FISH, 1, 1)), 0.04D);
        defaults.put(getRegistryName(new ItemStack(Items.FISH, 1, 2)), 0.2D);
        defaults.put(getRegistryName(new ItemStack(Items.FISH, 1, 3)), 0.08D);
        return defaults;
    }

    private static Map<String, Double> createMobDefaults() {
        Map<String, Double> defaults = new LinkedHashMap<>();
        defaults.put("minecraft:wither", 10.0D);
        defaults.put("minecraft:ender_dragon", 100.0D);
        defaults.put("minecraft:skeleton_horse", 0.8D);
        defaults.put("minecraft:zombie_horse", 0.8D);
        defaults.put("minecraft:villager_golem", 0.05D);
        defaults.put("minecraft:guardian", 0.05D);
        defaults.put("minecraft:elder_guardian", 0.05D);
        defaults.put("minecraft:parrot", 0.05D);
        defaults.put("minecraft:polar_bear", 0.05D);
        defaults.put("minecraft:slime", 0.02D);
        return defaults;
    }

    private static String getRegistryName(Block block) {
        ResourceLocation id = Block.REGISTRY.getNameForObject(block);
        return id == null ? "" : id.toString();
    }

    private static String getRegistryName(ItemStack stack) {
        ResourceLocation id = Item.REGISTRY.getNameForObject(stack.getItem());
        return id == null ? "" : id.toString() + "." + stack.getMetadata();
    }
}
