package org.hhoa.mc.intensify.util;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ItemModifierHelper {
    public static void initAttributeModifiers(ItemStack itemStack, EntityEquipmentSlot equipmentSlot) {
        if (!itemStack.hasTagCompound()
                || !itemStack.getTagCompound().hasKey("AttributeModifiers", Constants.NBT.TAG_LIST)) {
            Multimap<String, AttributeModifier> attributeModifiers =
                    itemStack.getAttributeModifiers(equipmentSlot);
            for (Map.Entry<String, Collection<AttributeModifier>> attributeCollectionEntry :
                    attributeModifiers.asMap().entrySet()) {
                for (AttributeModifier attributeModifier : attributeCollectionEntry.getValue()) {
                    itemStack.addAttributeModifier(
                            attributeCollectionEntry.getKey(), attributeModifier, equipmentSlot);
                }
            }
        }
    }

    public static void replaceAttributeModifier(
            ItemStack stack, UUID modifierId, NBTTagCompound newModifierData) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("AttributeModifiers", Constants.NBT.TAG_LIST)) {
                NBTTagList modifiers =
                        tag.getTagList("AttributeModifiers", Constants.NBT.TAG_COMPOUND);

                for (int i = 0; i < modifiers.tagCount(); i++) {
                    NBTTagCompound modifierTag = modifiers.getCompoundTagAt(i);

                    if (modifierTag.getUniqueId("UUID").equals(modifierId)) {
                        modifiers.set(i, newModifierData);
                        break;
                    }
                }
            }
        }
    }

    public static void removeAttributeModifier(ItemStack stack, UUID modifierId) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("AttributeModifiers", Constants.NBT.TAG_LIST)) {
                NBTTagList modifiers =
                        tag.getTagList("AttributeModifiers", Constants.NBT.TAG_COMPOUND);

                for (int i = 0; i < modifiers.tagCount(); i++) {
                    NBTTagCompound modifierTag = modifiers.getCompoundTagAt(i);
                    if (modifierTag.getUniqueId("UUID").equals(modifierId)) {
                        modifiers.removeTag(i);
                        break;
                    }
                }

                if (modifiers.tagCount() == 0) {
                    tag.removeTag("AttributeModifiers");
                }
            }
        }
    }
}
