package org.hhoa.mc.intensify.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

public class IntensifyConstants {
    public static final String FURNACE_OWNER_TAG_ID = "Owner";
    public static final String ENENGED_TAG_ID = "enenged";
    public static final String LAST_RECIPE_TAG_ID = "LastRecipe";

    public static final Map<String, Predicate<Item>> TOOL_NAME_CLASS_MAPPING =
            new LinkedHashMap<>();
    public static final Map<String, ArmorType> ARMOR_NAME_CLASS_MAPPING = new LinkedHashMap<>();

    static {
        // tool
        TOOL_NAME_CLASS_MAPPING.put("hoe", HoeItem.class::isInstance);
        TOOL_NAME_CLASS_MAPPING.put(
                "pickaxe",
                item ->
                        item.components().has(DataComponents.TOOL)
                                && !HoeItem.class.isInstance(item)
                                && !ShovelItem.class.isInstance(item)
                                && !AxeItem.class.isInstance(item)
                                && !item.components().has(DataComponents.WEAPON));
        TOOL_NAME_CLASS_MAPPING.put("shovel", ShovelItem.class::isInstance);

        // tool and weapon
        TOOL_NAME_CLASS_MAPPING.put("axe", AxeItem.class::isInstance);

        // other
        TOOL_NAME_CLASS_MAPPING.put("elytra", item -> item.components().has(DataComponents.GLIDER));
        TOOL_NAME_CLASS_MAPPING.put("fishing_rod", FishingRodItem.class::isInstance);

        // weapon
        TOOL_NAME_CLASS_MAPPING.put(
                "sword",
                item ->
                        item.components().has(DataComponents.WEAPON)
                                && !AxeItem.class.isInstance(item)
                                && !MaceItem.class.isInstance(item)
                                && !TridentItem.class.isInstance(item)
                                && !BowItem.class.isInstance(item)
                                && !CrossbowItem.class.isInstance(item)
                                && !ShieldItem.class.isInstance(item));
        TOOL_NAME_CLASS_MAPPING.put("mace", MaceItem.class::isInstance);
        TOOL_NAME_CLASS_MAPPING.put("bow", BowItem.class::isInstance);
        TOOL_NAME_CLASS_MAPPING.put("crossbow", CrossbowItem.class::isInstance);
        TOOL_NAME_CLASS_MAPPING.put("shield", ShieldItem.class::isInstance);
        TOOL_NAME_CLASS_MAPPING.put("trident", TridentItem.class::isInstance);

        // armor
        ARMOR_NAME_CLASS_MAPPING.put("leggings", ArmorType.LEGGINGS);
        ARMOR_NAME_CLASS_MAPPING.put("boots", ArmorType.BOOTS);
        ARMOR_NAME_CLASS_MAPPING.put("helmet", ArmorType.HELMET);
        ARMOR_NAME_CLASS_MAPPING.put("chestplate", ArmorType.CHESTPLATE);
    }

    public static ArmorType getArmorType(Item item) {
        Equippable equippable = item.components().get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return null;
        }

        return switch (equippable.slot()) {
            case HEAD -> ArmorType.HELMET;
            case CHEST -> item.components().has(DataComponents.GLIDER) ? ArmorType.BODY : ArmorType.CHESTPLATE;
            case LEGS -> ArmorType.LEGGINGS;
            case FEET -> ArmorType.BOOTS;
            default -> null;
        };
    }
}
