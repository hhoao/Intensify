package org.hhoa.mc.intensify.config;

import static org.hhoa.mc.intensify.config.IntensifyConstants.ARMOR_NAME_CLASS_MAPPING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.DefaultEnhancementIntensifySystem;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;

public class IntensifyConfig {
    private static DefaultEnengIntensifySystem defaultEnengIntensifySystem;
    private static DefaultEnhancementIntensifySystem defaultEnhancementIntensifySystem;

    public static final Integer DEFAULT_INTENSIFY_STONE_BURN_TIME = 200;

    public static final Integer DEFAULT_INTENSIFY_STONE_EXPERIENCE = 5;

    private static HashMap<EquipmentSlotType, ToolIntensifyConfig> armorClassConfigMap;
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
                    EquipmentSlotType type =
                            ARMOR_NAME_CLASS_MAPPING.get(toolIntensifyConfig.getName());
                    if (type == null) {
                        throw new RuntimeException(toolIntensifyConfig.getName());
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

    public static HashMap<EquipmentSlotType, ToolIntensifyConfig> getArmorClassConfigMap() {
        return armorClassConfigMap;
    }

    public static ToolIntensifyConfig getToolIntensifyConfig(Item item) {
        HashMap<EquipmentSlotType, ToolIntensifyConfig> armorClassConfigMap =
                getArmorClassConfigMap();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem) item;
            return armorClassConfigMap.get(armorItem.getEquipmentSlot());
        } else {
            HashMap<Class<? extends Item>, ToolIntensifyConfig> configsMap =
                    getToolWeaponClassConfigMap();
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
