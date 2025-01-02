package org.hhoa.mc.intensify.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import org.hhoa.mc.intensify.util.ConfigLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolIntensifyConfig {
    public static final HashMap<String, Class<? extends Item>> TOOL_NAME_CLASS_MAPPING = new HashMap<>();
    public static final HashMap<String, ArmorItem.Type> ARMOR_NAME_CLASS_MAPPING = new HashMap<>();

    static {
        // tool
        TOOL_NAME_CLASS_MAPPING.put("hoe", HoeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("pickaxe", PickaxeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("shovel", ShovelItem.class);

        // tool and weapon
        TOOL_NAME_CLASS_MAPPING.put("axe", AxeItem.class);

        // weapon
        TOOL_NAME_CLASS_MAPPING.put("sword", SwordItem.class);
        TOOL_NAME_CLASS_MAPPING.put("bow", BowItem.class);
        TOOL_NAME_CLASS_MAPPING.put("crossbow", CrossbowItem.class);
        TOOL_NAME_CLASS_MAPPING.put("shield", ShieldItem.class);
        TOOL_NAME_CLASS_MAPPING.put("fishing_rod", FishingRodItem.class);

        // armor
        ARMOR_NAME_CLASS_MAPPING.put("leggings", ArmorItem.Type.LEGGINGS);
        ARMOR_NAME_CLASS_MAPPING.put("boots", ArmorItem.Type.BOOTS);
        ARMOR_NAME_CLASS_MAPPING.put("helmet", ArmorItem.Type.HELMET);
        ARMOR_NAME_CLASS_MAPPING.put("chestplate", ArmorItem.Type.CHESTPLATE);
    }

    private static HashMap<ArmorItem.Type, ToolIntensifyConfig> armorClassConfigMap;
    private static List<ToolIntensifyConfig> toolIntensifyConfigs;
    private static HashMap<Class<? extends Item>, ToolIntensifyConfig> classToolIntensifyConfigHashMap;
    private String name;
    private boolean enable;
    private List<AttributeConfig> attributes = new ArrayList<>();

    public static void initialize() {
        toolIntensifyConfigs=
            ConfigLoader.loadToolIntensifyConfigFromDir("config/intensify");
        classToolIntensifyConfigHashMap = new HashMap<>();
        armorClassConfigMap = new HashMap<>();
        for (ToolIntensifyConfig toolIntensifyConfig : toolIntensifyConfigs) {
            if (toolIntensifyConfig.enable) {
                Class<? extends Item> aClass = TOOL_NAME_CLASS_MAPPING.get(toolIntensifyConfig.name);
                if (aClass == null) {
                    ArmorItem.Type type = ARMOR_NAME_CLASS_MAPPING.get(toolIntensifyConfig.name);
                    if (type == null) {
                        throw new RuntimeException(toolIntensifyConfig.name);
                    }
                    armorClassConfigMap.put(type, toolIntensifyConfig);
                } else {
                    classToolIntensifyConfigHashMap.put(aClass, toolIntensifyConfig);
                }
            }
        }
    }

    public static HashMap<Class<? extends Item>, ToolIntensifyConfig> getToolWeaponClassConfigMap() {
        return classToolIntensifyConfigHashMap;
    }

    public static HashMap<ArmorItem.Type, ToolIntensifyConfig> getArmorClassConfigMap() {
        return armorClassConfigMap;
    }

    public static ToolIntensifyConfig getToolIntensifyConfig(Item item) {
        HashMap<ArmorItem.Type, ToolIntensifyConfig> armorClassConfigMap = ToolIntensifyConfig.getArmorClassConfigMap();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem) item;
            return armorClassConfigMap.get(armorItem.getType());
        } else {
            HashMap<Class<? extends Item>, ToolIntensifyConfig> configsMap = ToolIntensifyConfig.getToolWeaponClassConfigMap();
            for (Map.Entry<Class<? extends Item>, ToolIntensifyConfig> classToolIntensifyConfigEntry : configsMap.entrySet()) {
                if (classToolIntensifyConfigEntry.getKey().isInstance(item)) {
                    return classToolIntensifyConfigEntry.getValue();
                }
            }
        }
        return null;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<AttributeConfig> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeConfig> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class AttributeConfig {
        private Attribute type;
        private EnengConfig eneng;
        private List<GrowConfig> grows = new ArrayList<>();

        public Attribute getType() {
            return type;
        }

        public void setType(Attribute type) {
            this.type = type;
        }

        public EnengConfig getEneng() {
            return eneng;
        }

        public void setEneng(EnengConfig eneng) {
            this.eneng = eneng;
        }

        public List<GrowConfig> getGrows() {
            return grows;
        }

        public void setGrows(List<GrowConfig> grows) {
            this.grows = grows;
        }
    }

    public static class EnengConfig {
        private   boolean enable;
        private List<Double> value;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public List<Double> getValue() {
            return value;
        }

        public void setValue(List<Double> value) {
            Preconditions.checkArgument(!value.isEmpty() && value.size() <= 2);
            this.value = value;
        }
    }

    public static class GrowConfig {
        private GrowTypeEnum type;
        private Range<Integer> range;
        private Double value;
        private int speed;

        public GrowTypeEnum getType() {
            return type;
        }

        public void setType(GrowTypeEnum type) {
            this.type = type;
        }

        public Range<Integer> getRange() {
            return range;
        }

        public void setRange(List<Integer> rangeList) {
            int lower = rangeList.get(0);
            int upper = rangeList.get(1);
            if (upper == -1) {
                upper = Integer.MAX_VALUE;
            }
            this.range = Range.range(lower, BoundType.CLOSED, upper, BoundType.CLOSED);
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }
    }
    public enum GrowTypeEnum {
        FIXED,
        PROPORTIONAL
    }
}
