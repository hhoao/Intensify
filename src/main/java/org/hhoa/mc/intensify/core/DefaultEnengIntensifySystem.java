package org.hhoa.mc.intensify.core;

import static org.hhoa.mc.intensify.config.IntensifyConstants.ENENGED_TAG_ID;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;

public class DefaultEnengIntensifySystem extends EnengIntensifySystem {
    @Override
    public boolean isEneng(ItemStack itemStack) {
        CompoundNBT orCreateTag = itemStack.getOrCreateTag();
        return orCreateTag.getBoolean(ENENGED_TAG_ID);
    }

    @Override
    public void intensify(
            ServerPlayerEntity player, ItemStack itemStack, ToolIntensifyConfig intensifyConfig) {

        EquipmentSlotType equipmentSlotForItem = MobEntity.getSlotForItemStack(itemStack);
        List<ToolIntensifyConfig.AttributeConfig> attributes = intensifyConfig.getAttributes();
        for (ToolIntensifyConfig.AttributeConfig attribute : attributes) {
            Attribute attributeType = attribute.getType();
            ToolIntensifyConfig.EnengConfig eneng = attribute.getEneng();
            if (attribute.getEneng() != null && eneng.isEnable()) {
                attributeEneng(itemStack, eneng, attributeType, equipmentSlotForItem);
            }
        }
        CompoundNBT orCreateTag = itemStack.getOrCreateTag();
        orCreateTag.putBoolean(ENENGED_TAG_ID, true);
        if (player != null) {
            player.sendMessage(TranslatableTexts.ENENG_SUCCESS.component(), player.getUniqueID());
        }
    }

    private void attributeEneng(
            ItemStack itemStack,
            ToolIntensifyConfig.EnengConfig eneng,
            Attribute attributeType,
            EquipmentSlotType equipmentSlotForItem) {
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
