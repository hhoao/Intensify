package org.hhoa.mc.intensify.core;

import static org.hhoa.mc.intensify.config.IntensifyConstants.ENENGED_TAG_ID;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;

public class DefaultEnengIntensifySystem extends EnengIntensifySystem {
    @Override
    public boolean isEneng(ItemStack itemStack) {
        CompoundTag orCreateTag = itemStack.getOrCreateTag();
        return orCreateTag.getBoolean(ENENGED_TAG_ID);
    }

    @Override
    public void intensify(
            ServerPlayer player, ItemStack itemStack, ToolIntensifyConfig intensifyConfig) {
        EquipmentSlot equipmentSlotForItem = LivingEntity.getEquipmentSlotForItem(itemStack);
        List<ToolIntensifyConfig.AttributeConfig> attributes = intensifyConfig.getAttributes();
        for (ToolIntensifyConfig.AttributeConfig attribute : attributes) {
            Attribute attributeType = attribute.getType();
            ToolIntensifyConfig.EnengConfig eneng = attribute.getEneng();
            if (attribute.getEneng() != null && eneng.isEnable()) {
                attributeEneng(itemStack, eneng, attributeType, equipmentSlotForItem);
            }
        }
        CompoundTag orCreateTag = itemStack.getOrCreateTag();
        orCreateTag.putBoolean(ENENGED_TAG_ID, true);
        if (player != null) {
            player.sendSystemMessage(TranslatableTexts.ENENG_SUCCESS.component());
        }
    }

    private void attributeEneng(
            ItemStack itemStack,
            ToolIntensifyConfig.EnengConfig eneng,
            Attribute attributeType,
            EquipmentSlot equipmentSlotForItem) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        List<Double> value = eneng.getValue();
        double v = randomizeAndMultiply(value, current);

        ItemModifierHelper.initAttributeModifiers(itemStack, equipmentSlotForItem);
        itemStack.addAttributeModifier(
                attributeType,
                new AttributeModifier(
                        getAttributeModifierName(attributeType),
                        v,
                        AttributeModifier.Operation.ADDITION),
                equipmentSlotForItem);
    }

    private static double randomizeAndMultiply(List<Double> value, ThreadLocalRandom current) {
        double v;
        if (value.size() == 2) {
            v =
                    current.nextDouble(value.get(0), value.get(1))
                            * ConfigRegistry.ATTRIBUTE_MULTIPLIER.get();
        } else {
            v = value.get(0) * ConfigRegistry.ATTRIBUTE_MULTIPLIER.get();
            double lower = v * 0.1;
            double upper = v * (1 + 0.1);
            v = current.nextDouble(lower, upper);
        }
        return v;
    }
}
