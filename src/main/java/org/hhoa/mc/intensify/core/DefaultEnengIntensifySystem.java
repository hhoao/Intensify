package org.hhoa.mc.intensify.core;

import static org.hhoa.mc.intensify.config.IntensifyConstants.ENENGED_TAG_ID;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;

public class DefaultEnengIntensifySystem extends EnengIntensifySystem {
    @Override
    public boolean isEneng(ItemStack itemStack) {
        return getOrCreateTag(itemStack).getBoolean(ENENGED_TAG_ID);
    }

    @Override
    public void intensify(
            EntityPlayerMP player, ItemStack itemStack, ToolIntensifyConfig intensifyConfig) {

        EntityEquipmentSlot equipmentSlotForItem = EntityLiving.getSlotForItemStack(itemStack);
        List<ToolIntensifyConfig.AttributeConfig> attributes = intensifyConfig.getAttributes();
        for (ToolIntensifyConfig.AttributeConfig attribute : attributes) {
            IAttribute attributeType = attribute.getType();
            ToolIntensifyConfig.EnengConfig eneng = attribute.getEneng();
            if (attribute.getEneng() != null && eneng.isEnable()) {
                attributeEneng(itemStack, eneng, attributeType, equipmentSlotForItem);
            }
        }
        NBTTagCompound tag = getOrCreateTag(itemStack);
        tag.setBoolean(ENENGED_TAG_ID, true);
        if (player != null) {
            player.sendMessage(TranslatableTexts.ENENG_SUCCESS.component());
        }
    }

    private void attributeEneng(
            ItemStack itemStack,
            ToolIntensifyConfig.EnengConfig eneng,
            IAttribute attributeType,
            EntityEquipmentSlot equipmentSlotForItem) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        List<Double> value = eneng.getValue();
        double v = randomizeAndMultiply(value, current);

        ItemModifierHelper.initAttributeModifiers(itemStack, equipmentSlotForItem);
        itemStack.addAttributeModifier(
                attributeType.getName(),
                new AttributeModifier(
                        getAttributeModifierName(attributeType),
                        v,
                        0),
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

    private static NBTTagCompound getOrCreateTag(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        return itemStack.getTagCompound();
    }
}
