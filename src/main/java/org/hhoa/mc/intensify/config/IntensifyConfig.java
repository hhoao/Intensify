package org.hhoa.mc.intensify.config;

import static org.hhoa.mc.intensify.config.IntensifyConstants.ARMOR_NAME_CLASS_MAPPING;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.DefaultEnhancementIntensifySystem;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;

public class IntensifyConfig {
    private static DefaultEnengIntensifySystem defaultEnengIntensifySystem;
    private static DefaultEnhancementIntensifySystem defaultEnhancementIntensifySystem;

    public static final Integer DEFAULT_INTENSIFY_STONE_BURN_TIME =
            AbstractFurnaceBlockEntity.BURN_TIME_STANDARD;

    public static final Integer DEFAULT_INTENSIFY_STONE_EXPERIENCE = 5;

    private static HashMap<ArmorType, ToolIntensifyConfig> armorClassConfigMap;
    private static LinkedHashMap<String, ToolIntensifyConfig> toolConfigMap;

    public static void initialize() {
        defaultEnengIntensifySystem = new DefaultEnengIntensifySystem();
        defaultEnhancementIntensifySystem =
                new DefaultEnhancementIntensifySystem(1, 0.005, 1, 4, 10);

        List<ToolIntensifyConfig> toolIntensifyConfigs =
                ConfigLoader.loadToolIntensifyConfigFromDir("config/intensify");
        toolConfigMap = new LinkedHashMap<>();
        armorClassConfigMap = new HashMap<>();
        for (ToolIntensifyConfig toolIntensifyConfig : toolIntensifyConfigs) {
            if (toolIntensifyConfig.isEnable()) {
                Predicate<Item> itemMatcher =
                        IntensifyConstants.TOOL_NAME_CLASS_MAPPING.get(
                                toolIntensifyConfig.getName());
                if (itemMatcher == null) {
                    ArmorType type =
                            ARMOR_NAME_CLASS_MAPPING.get(toolIntensifyConfig.getName());
                    if (type == null) {
                        throw new RuntimeException(toolIntensifyConfig.getName());
                    }
                    armorClassConfigMap.put(type, toolIntensifyConfig);
                } else {
                    toolConfigMap.put(toolIntensifyConfig.getName(), toolIntensifyConfig);
                }
            }
        }
    }

    public static EnengIntensifySystem getEnengIntensifySystem() {
        return defaultEnengIntensifySystem;
    }

    public static EnhancementIntensifySystem getEnhancementIntensifySystem() {
        return defaultEnhancementIntensifySystem;
    }

    public static LinkedHashMap<String, ToolIntensifyConfig> getToolWeaponClassConfigMap() {
        return toolConfigMap;
    }

    public static HashMap<ArmorType, ToolIntensifyConfig> getArmorClassConfigMap() {
        return armorClassConfigMap;
    }

    public static ToolIntensifyConfig getToolIntensifyConfig(Item item) {
        HashMap<ArmorType, ToolIntensifyConfig> armorClassConfigMap = getArmorClassConfigMap();
        ArmorType armorType = IntensifyConstants.getArmorType(item);
        if (armorType != null) {
            return armorClassConfigMap.get(armorType);
        }

        LinkedHashMap<String, ToolIntensifyConfig> configsMap = getToolWeaponClassConfigMap();
        for (Map.Entry<String, ToolIntensifyConfig> configEntry : configsMap.entrySet()) {
            Predicate<Item> itemMatcher =
                    IntensifyConstants.TOOL_NAME_CLASS_MAPPING.get(configEntry.getKey());
            if (itemMatcher != null && itemMatcher.test(item)) {
                return configEntry.getValue();
            }
        }
        return null;
    }
}
