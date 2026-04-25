package org.hhoa.mc.intensify.util;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class ItemModifierHelper {
    public static void initAttributeModifiers(
            ItemStack itemStack, EquipmentSlotType equipmentSlot) {
        if (!itemStack.hasTag() || !itemStack.getTag().contains("AttributeModifiers", 9)) {
            Multimap<Attribute, AttributeModifier> attributeModifiers =
                    itemStack.getAttributeModifiers(equipmentSlot);
            for (Map.Entry<Attribute, Collection<AttributeModifier>> attributeCollectionEntry :
                    attributeModifiers.asMap().entrySet()) {
                for (AttributeModifier attributeModifier : attributeCollectionEntry.getValue()) {
                    itemStack.addAttributeModifier(
                            attributeCollectionEntry.getKey(), attributeModifier, equipmentSlot);
                }
            }
        }
    }

    public static void replaceAttributeModifier(
            ItemStack stack, UUID modifierId, CompoundNBT newModifierData) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.contains("AttributeModifiers", Constants.NBT.TAG_LIST)) {
                ListNBT modifiers = tag.getList("AttributeModifiers", Constants.NBT.TAG_COMPOUND);

                for (int i = 0; i < modifiers.size(); i++) {
                    CompoundNBT modifierTag = modifiers.getCompound(i);

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
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.contains("AttributeModifiers", Constants.NBT.TAG_LIST)) {
                ListNBT modifiers = tag.getList("AttributeModifiers", Constants.NBT.TAG_COMPOUND);

                // 遍历寻找需要移除的 AttributeModifier
                modifiers.removeIf(
                        modifier -> {
                            CompoundNBT modifierTag = (CompoundNBT) modifier;
                            return modifierTag.getUniqueId("UUID").equals(modifierId);
                        });

                // 如果列表为空，移除整个标签
                if (modifiers.isEmpty()) {
                    tag.remove("AttributeModifiers");
                }
            }
        }
    }
}
