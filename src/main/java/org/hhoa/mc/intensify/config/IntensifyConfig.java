package org.hhoa.mc.intensify.config;

import static org.hhoa.mc.intensify.config.IntensifyConstants.ARMOR_NAME_CLASS_MAPPING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.DefaultEnhancementIntensifySystem;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;

public class IntensifyConfig {
    private static final Logger LOGGER = LogManager.getLogger(IntensifyConfig.class);
    private static DefaultEnengIntensifySystem defaultEnengIntensifySystem;
    private static DefaultEnhancementIntensifySystem defaultEnhancementIntensifySystem;

    public static final Integer DEFAULT_INTENSIFY_STONE_BURN_TIME = 200;

    public static final Integer DEFAULT_INTENSIFY_STONE_EXPERIENCE = 5;

    private static HashMap<EntityEquipmentSlot, ToolIntensifyConfig> armorClassConfigMap;
    private static HashMap<Class<? extends Item>, ToolIntensifyConfig>
            classToolIntensifyConfigHashMap;

    public static void initialize() {
        defaultEnengIntensifySystem = new DefaultEnengIntensifySystem();
        defaultEnhancementIntensifySystem =
                new DefaultEnhancementIntensifySystem(1, 0.005, 1, 4, 10);

        List<ToolIntensifyConfig> toolIntensifyConfigs =
                ConfigLoader.loadToolIntensifyConfigFromDir("config/intensify");
        classToolIntensifyConfigHashMap = new HashMap<>();
        armorClassConfigMap = new HashMap<>();
        for (ToolIntensifyConfig toolIntensifyConfig : toolIntensifyConfigs) {
            if (toolIntensifyConfig.isEnable()) {
                Class<? extends Item> aClass =
                        IntensifyConstants.TOOL_NAME_CLASS_MAPPING.get(
                                toolIntensifyConfig.getName());
                if (aClass == null) {
                    EntityEquipmentSlot type =
                            ARMOR_NAME_CLASS_MAPPING.get(toolIntensifyConfig.getName());
                    if (type == null) {
                        LOGGER.warn(
                                "Skipping unsupported intensify config for {} on 1.12.2",
                                toolIntensifyConfig.getName());
                        continue;
                    }
                    armorClassConfigMap.put(type, toolIntensifyConfig);
                } else {
                    classToolIntensifyConfigHashMap.put(aClass, toolIntensifyConfig);
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

    public static HashMap<Class<? extends Item>, ToolIntensifyConfig>
            getToolWeaponClassConfigMap() {
        return classToolIntensifyConfigHashMap;
    }

    public static HashMap<EntityEquipmentSlot, ToolIntensifyConfig> getArmorClassConfigMap() {
        return armorClassConfigMap;
    }

    public static ToolIntensifyConfig getToolIntensifyConfig(Item item) {
        if (item == null) {
            return null;
        }
        HashMap<EntityEquipmentSlot, ToolIntensifyConfig> armorClassConfigMap =
                getArmorClassConfigMap();
        if (item instanceof ItemArmor) {
            ItemArmor armorItem = (ItemArmor) item;
            return armorClassConfigMap == null ? null : armorClassConfigMap.get(armorItem.armorType);
        } else {
            HashMap<Class<? extends Item>, ToolIntensifyConfig> configsMap =
                    getToolWeaponClassConfigMap();
            if (configsMap == null) {
                return null;
            }
            for (Map.Entry<Class<? extends Item>, ToolIntensifyConfig>
                    classToolIntensifyConfigEntry : configsMap.entrySet()) {
                if (classToolIntensifyConfigEntry.getKey().isInstance(item)) {
                    return classToolIntensifyConfigEntry.getValue();
                }
            }
        }
        return null;
    }
}
