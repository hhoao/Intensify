package org.hhoa.mc.intensify.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;

public class IntensifyConstants {
    public static final String FURNACE_OWNER_TAG_ID = "Owner";
    public static final String ENENGED_TAG_ID = "enenged";
    public static final String LAST_RECIPE_TAG_ID = "LastRecipe";

    public static final HashMap<String, Class<? extends Item>> TOOL_NAME_CLASS_MAPPING =
            new HashMap<>();
    public static final HashMap<String, EntityEquipmentSlot> ARMOR_NAME_CLASS_MAPPING =
            new HashMap<>();
    public static final List<String> TOOL_CONFIG_RESOURCE_NAMES =
            Collections.unmodifiableList(
                    Arrays.asList(
                            "axe",
                            "boots",
                            "bow",
                            "chestplate",
                            "crossbow",
                            "elytra",
                            "fishing_rod",
                            "helmet",
                            "hoe",
                            "leggings",
                            "pickaxe",
                            "shield",
                            "shovel",
                            "sword",
                            "trident"));

    static {
        // tool
        TOOL_NAME_CLASS_MAPPING.put("hoe", ItemHoe.class);
        TOOL_NAME_CLASS_MAPPING.put("pickaxe", ItemPickaxe.class);
        TOOL_NAME_CLASS_MAPPING.put("shovel", ItemSpade.class);

        // tool and weapon
        TOOL_NAME_CLASS_MAPPING.put("axe", ItemAxe.class);

        // other
        TOOL_NAME_CLASS_MAPPING.put("elytra", ItemElytra.class);
        TOOL_NAME_CLASS_MAPPING.put("fishing_rod", ItemFishingRod.class);

        // weapon
        TOOL_NAME_CLASS_MAPPING.put("sword", ItemSword.class);
        TOOL_NAME_CLASS_MAPPING.put("bow", ItemBow.class);
        TOOL_NAME_CLASS_MAPPING.put("shield", ItemShield.class);

        // armor
        ARMOR_NAME_CLASS_MAPPING.put("leggings", EntityEquipmentSlot.LEGS);
        ARMOR_NAME_CLASS_MAPPING.put("boots", EntityEquipmentSlot.FEET);
        ARMOR_NAME_CLASS_MAPPING.put("helmet", EntityEquipmentSlot.HEAD);
        ARMOR_NAME_CLASS_MAPPING.put("chestplate", EntityEquipmentSlot.CHEST);
    }

    public static final Set<Block> LIMITED_REPLACED_BLOCKS = new HashSet<>();

    static {
        LIMITED_REPLACED_BLOCKS.add(Blocks.COAL_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.IRON_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.GOLD_ORE);
        LIMITED_REPLACED_BLOCKS.add(Blocks.REDSTONE_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.LAPIS_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.DIAMOND_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.EMERALD_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.QUARTZ_ORE);
    }
}
