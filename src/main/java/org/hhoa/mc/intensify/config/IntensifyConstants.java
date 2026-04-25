package org.hhoa.mc.intensify.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;

public class IntensifyConstants {
    public static final String FURNACE_OWNER_TAG_ID = "Owner";
    public static final String ENENGED_TAG_ID = "enenged";
    public static final String LAST_RECIPE_TAG_ID = "LastRecipe";

    public static final HashMap<String, Class<? extends Item>> TOOL_NAME_CLASS_MAPPING =
            new HashMap<>();
    public static final HashMap<String, EquipmentSlotType> ARMOR_NAME_CLASS_MAPPING =
            new HashMap<>();

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
        ARMOR_NAME_CLASS_MAPPING.put("leggings", EquipmentSlotType.LEGS);
        ARMOR_NAME_CLASS_MAPPING.put("boots", EquipmentSlotType.FEET);
        ARMOR_NAME_CLASS_MAPPING.put("helmet", EquipmentSlotType.HEAD);
        ARMOR_NAME_CLASS_MAPPING.put("chestplate", EquipmentSlotType.CHEST);
    }

    public static final Set<Block> LIMITED_REPLACED_BLOCKS = new HashSet<>();

    static {
        LIMITED_REPLACED_BLOCKS.add(Blocks.COAL_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.IRON_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.GOLD_ORE);
        LIMITED_REPLACED_BLOCKS.add(Blocks.NETHER_GOLD_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.REDSTONE_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.LAPIS_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.DIAMOND_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.EMERALD_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);

        LIMITED_REPLACED_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
    }
}
