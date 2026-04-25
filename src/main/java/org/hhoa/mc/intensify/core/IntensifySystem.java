package org.hhoa.mc.intensify.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public interface IntensifySystem {
    void intensify(ServerPlayer player, ItemStack item, ToolIntensifyConfig intensifyConfig);

    default ResourceLocation getAttributeModifierId(Attribute attribute) {
        return getAttributeModifierId(attribute, null);
    }

    default ResourceLocation getAttributeModifierId(Attribute attribute, EquipmentSlot equipmentSlot) {
        ResourceLocation attributeKey = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        String normalizedAttributeId =
                attributeKey != null ? attributeKey.toString() : attribute.getDescriptionId();
        String slotName = usesSlotScopedModifierIds(equipmentSlot) ? equipmentSlot.getName() : null;
        return ResourceLocation.fromNamespaceAndPath(
                Intensify.MODID, AttributeModifierIds.buildPath(normalizedAttributeId, slotName));
    }

    default ResourceLocation[] getCompatibleAttributeModifierIds(
            Attribute attribute, EquipmentSlot equipmentSlot) {
        ResourceLocation slotScopedId = getAttributeModifierId(attribute, equipmentSlot);
        ResourceLocation legacyId = getAttributeModifierId(attribute);
        if (slotScopedId.equals(legacyId)) {
            return new ResourceLocation[] {slotScopedId};
        }
        return new ResourceLocation[] {slotScopedId, legacyId};
    }

    default String getTagId(String id) {
        return Intensify.MODID + ".intensify.tag." + id;
    }

    default EquipmentSlot getEquipmentSlotForItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof Equipable equipable) {
            return equipable.getEquipmentSlot();
        }
        return EquipmentSlot.MAINHAND;
    }

    private static boolean usesSlotScopedModifierIds(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.HEAD
                || equipmentSlot == EquipmentSlot.CHEST
                || equipmentSlot == EquipmentSlot.LEGS
                || equipmentSlot == EquipmentSlot.FEET;
    }
}
