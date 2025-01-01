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
import net.minecraftforge.common.ForgeConfigSpec;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.util.ConfigLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToolIntensifyConfig {
    public static final HashMap<String, Class<? extends Item>> TOOL_NAME_CLASS_MAPPING = new HashMap<>();

    static {
        TOOL_NAME_CLASS_MAPPING.put("sword", SwordItem.class);
        TOOL_NAME_CLASS_MAPPING.put("hoe", HoeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("axe", AxeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("shovel", ShovelItem.class);
        TOOL_NAME_CLASS_MAPPING.put("pickaxe", PickaxeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("bow", BowItem.class);
        TOOL_NAME_CLASS_MAPPING.put("crossbow", CrossbowItem.class);
        TOOL_NAME_CLASS_MAPPING.put("shield", ShieldItem.class);
        TOOL_NAME_CLASS_MAPPING.put("fish_rod", FishingRodItem.class);
        TOOL_NAME_CLASS_MAPPING.put("armor", ArmorItem.class);
    }

    private static List<ToolIntensifyConfig> toolIntensifyConfigs;
    private static HashMap<Class<? extends Item>, ToolIntensifyConfig> classToolIntensifyConfigHashMap;
    private String name;
    private boolean enable;
    private List<AttributeConfig> attributes = new ArrayList<>();

    public static void initialize() {
        toolIntensifyConfigs=
            ConfigLoader.loadToolIntensifyConfigFromDir("config/intensify");
        classToolIntensifyConfigHashMap = new HashMap<>();
        for (ToolIntensifyConfig toolIntensifyConfig : toolIntensifyConfigs) {
            if (toolIntensifyConfig.enable) {
                Class<? extends Item> aClass = TOOL_NAME_CLASS_MAPPING.get(toolIntensifyConfig.name);
                classToolIntensifyConfigHashMap.put(aClass, toolIntensifyConfig);
            }
        }
    }

    public static HashMap<Class<? extends Item>, ToolIntensifyConfig> getClassConfigMap() {
        return classToolIntensifyConfigHashMap;
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
    }
    public enum GrowTypeEnum {
        FIXED,
        PROPORTIONAL
    }
}
