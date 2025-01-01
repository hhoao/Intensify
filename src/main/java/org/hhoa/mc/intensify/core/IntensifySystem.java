package org.hhoa.mc.intensify.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public interface IntensifySystem {
    void intensify(ServerPlayer player, ItemStack item,
                   ToolIntensifyConfig intensifyConfig);

    default String getAttributeModifierName(Attribute attribute) {
        return Intensify.MODID + ".intensify.attribute." + attribute.getDescriptionId();
    }

    default String getTagId(String id) {
        return Intensify.MODID + ".intensify.tag." + id;
    }
}
