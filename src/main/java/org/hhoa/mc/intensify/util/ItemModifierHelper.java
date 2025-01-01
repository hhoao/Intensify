package org.hhoa.mc.intensify.util;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ItemModifierHelper {
    public static void initAttributeModifiers(ItemStack itemStack, EquipmentSlot equipmentSlot) {
        if (!itemStack.hasTag() || !itemStack.getTag().contains("AttributeModifiers", 9)) {
            Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(equipmentSlot);
            for (Map.Entry<Attribute, Collection<AttributeModifier>> attributeCollectionEntry : attributeModifiers.asMap().entrySet()) {
                for (AttributeModifier attributeModifier : attributeCollectionEntry.getValue()) {
                    itemStack.addAttributeModifier(attributeCollectionEntry.getKey(), attributeModifier, equipmentSlot);
                }
            }
        }
    }

    public static void replaceAttributeModifier(ItemStack stack, UUID modifierId, CompoundTag newModifierData) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("AttributeModifiers", Tag.TAG_LIST)) {
                ListTag modifiers = tag.getList("AttributeModifiers", Tag.TAG_COMPOUND);

                for (int i = 0; i < modifiers.size(); i++) {
                    CompoundTag modifierTag = modifiers.getCompound(i);

                    if (UUID.fromString(modifierTag.getString("UUID")).equals(modifierId)) {
                        modifiers.set(i, newModifierData); // 替换指定的 AttributeModifier
                        break;
                    }
                }
            }
        }
    }

    public static void removeAttributeModifier(ItemStack stack, UUID modifierId) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("AttributeModifiers", Tag.TAG_LIST)) {
                ListTag modifiers = tag.getList("AttributeModifiers", Tag.TAG_COMPOUND);

                // 遍历寻找需要移除的 AttributeModifier
                modifiers.removeIf(modifier -> {
                    CompoundTag modifierTag = (CompoundTag) modifier;
                    return modifierTag.getUUID("UUID").equals(modifierId);
                });

                // 如果列表为空，移除整个标签
                if (modifiers.isEmpty()) {
                    tag.remove("AttributeModifiers");
                }
            }
        }
    }
}
