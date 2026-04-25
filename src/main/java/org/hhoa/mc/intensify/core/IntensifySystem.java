package org.hhoa.mc.intensify.core;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public interface IntensifySystem {
    void intensify(EntityPlayerMP player, ItemStack item, ToolIntensifyConfig intensifyConfig);

    default String getAttributeModifierName(IAttribute attribute) {
        return Intensify.MODID + ".intensify.attribute." + attribute.getName();
    }

    default String getTagId(String id) {
        return Intensify.MODID + ".intensify.tag." + id;
    }
}
