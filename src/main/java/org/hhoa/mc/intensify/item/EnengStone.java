package org.hhoa.mc.intensify.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class EnengStone extends IntensifyStone {
    public EnengStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.BLUE;
    }

    @Override
    public Component getDescriptionText() {
        return Component.literal("该物品用于给装备初次启能，装备赋能后将拥有一些初始属性，且只有启能后才能开始强化装备.");
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ENENG_STONE;
    }
}
