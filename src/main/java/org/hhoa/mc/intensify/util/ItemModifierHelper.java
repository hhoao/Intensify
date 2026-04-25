package org.hhoa.mc.intensify.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class ItemModifierHelper {
    public static void initAttributeModifiers(ItemStack itemStack) {
        ItemAttributeModifiers modifiers =
                itemStack.getOrDefault(
                        DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (modifiers.modifiers().isEmpty()) {
            ItemAttributeModifiers defaultModifiers = itemStack.getItem().getDefaultAttributeModifiers();
            if (!defaultModifiers.modifiers().isEmpty()) {
                itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, defaultModifiers);
            }
        }
    }

    public static void setAttributeModifier(
            ItemStack stack,
            Attribute attribute,
            AttributeModifier newModifier,
            EquipmentSlot equipmentSlot) {
        setAttributeModifier(stack, attribute, newModifier, equipmentSlot, newModifier.id());
    }

    public static void setAttributeModifier(
            ItemStack stack,
            Attribute attribute,
            AttributeModifier newModifier,
            EquipmentSlot equipmentSlot,
            ResourceLocation... replaceModifierIds) {
        initAttributeModifiers(stack);
        Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        Set<ResourceLocation> idsToReplace = new LinkedHashSet<>();
        idsToReplace.add(newModifier.id());
        for (ResourceLocation replaceModifierId : replaceModifierIds) {
            idsToReplace.add(replaceModifierId);
        }

        for (ItemAttributeModifiers.Entry entry : getCurrentAttributeModifiers(stack).modifiers()) {
            if (!(entry.attribute().equals(attributeHolder)
                    && idsToReplace.contains(entry.modifier().id()))) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }

        builder.add(attributeHolder, newModifier, EquipmentSlotGroup.bySlot(equipmentSlot));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    public static List<AttributeModifier> getAttributeModifiers(
            ItemStack stack,
            Attribute attribute,
            EquipmentSlot equipmentSlot,
            ResourceLocation modifierId) {
        return getAttributeModifiers(stack, attribute, equipmentSlot, new ResourceLocation[] {modifierId});
    }

    public static List<AttributeModifier> getAttributeModifiers(
            ItemStack stack,
            Attribute attribute,
            EquipmentSlot equipmentSlot,
            ResourceLocation... modifierIds) {
        Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
        List<AttributeModifier> modifiers = new ArrayList<>();
        Set<ResourceLocation> acceptedModifierIds = new LinkedHashSet<>(List.of(modifierIds));

        getCurrentAttributeModifiers(stack)
                .forEach(
                        equipmentSlot,
                        (holder, modifier) -> {
                            if (holder.equals(attributeHolder)
                                    && acceptedModifierIds.contains(modifier.id())) {
                                modifiers.add(modifier);
                            }
                        });
        return modifiers;
    }

    public static boolean getBooleanTag(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getBoolean(key);
    }

    public static int getIntTag(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(key);
    }

    public static String getStringTag(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getString(key);
    }

    public static void putBooleanTag(ItemStack stack, String key, boolean value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(key, value));
    }

    public static void putIntTag(ItemStack stack, String key, int value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(key, value));
    }

    public static void putStringTag(ItemStack stack, String key, String value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(key, value));
    }

    public static void removeTag(ItemStack stack, String key) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(key));
    }

    private static ItemAttributeModifiers getCurrentAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers modifiers =
                stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (modifiers.modifiers().isEmpty()) {
            return stack.getItem().getDefaultAttributeModifiers();
        }
        return modifiers;
    }

    public static void updateCustomData(ItemStack stack, java.util.function.Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public static CompoundTag getCustomData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
}
