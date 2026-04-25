package org.hhoa.mc.intensify.core;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public interface IntensifySystem {
    void intensify(ServerPlayerEntity player, ItemStack item, ToolIntensifyConfig intensifyConfig);

    default String getAttributeModifierName(Attribute attribute) {
        return Intensify.MODID + ".intensify.attribute." + attribute.getAttributeName();
    }

    default String getTagId(String id) {
        return Intensify.MODID + ".intensify.tag." + id;
    }
}
