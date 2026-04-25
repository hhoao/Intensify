package org.hhoa.mc.intensify.config;

import java.util.HashMap;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

public class IntensifyConstants {
    public static final String FURNACE_OWNER_TAG_ID = "Owner";
    public static final String ENENGED_TAG_ID = "enenged";
    public static final String LAST_RECIPE_TAG_ID = "LastRecipe";

    public static final HashMap<String, Class<? extends Item>> TOOL_NAME_CLASS_MAPPING =
            new HashMap<>();
    public static final HashMap<String, ArmorItem.Type> ARMOR_NAME_CLASS_MAPPING = new HashMap<>();

    static {
        // tool
        TOOL_NAME_CLASS_MAPPING.put("hoe", HoeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("pickaxe", PickaxeItem.class);
        TOOL_NAME_CLASS_MAPPING.put("shovel", ShovelItem.class);

        // tool and weapon
        TOOL_NAME_CLASS_MAPPING.put("axe", AxeItem.class);

        // other
        TOOL_NAME_CLASS_MAPPING.put("elytra", ElytraItem.class);
        TOOL_NAME_CLASS_MAPPING.put("fishing_rod", FishingRodItem.class);

        // weapon
        TOOL_NAME_CLASS_MAPPING.put("sword", SwordItem.class);
        TOOL_NAME_CLASS_MAPPING.put("bow", BowItem.class);
        TOOL_NAME_CLASS_MAPPING.put("crossbow", CrossbowItem.class);
        TOOL_NAME_CLASS_MAPPING.put("shield", ShieldItem.class);
        TOOL_NAME_CLASS_MAPPING.put("trident", TridentItem.class);

        // armor
        ARMOR_NAME_CLASS_MAPPING.put("leggings", ArmorItem.Type.LEGGINGS);
        ARMOR_NAME_CLASS_MAPPING.put("boots", ArmorItem.Type.BOOTS);
        ARMOR_NAME_CLASS_MAPPING.put("helmet", ArmorItem.Type.HELMET);
        ARMOR_NAME_CLASS_MAPPING.put("chestplate", ArmorItem.Type.CHESTPLATE);
    }
}
