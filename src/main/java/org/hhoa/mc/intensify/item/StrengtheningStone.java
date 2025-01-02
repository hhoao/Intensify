package org.hhoa.mc.intensify.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class StrengtheningStone extends IntensifyStone {
    public StrengtheningStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.BLUE;
    }

    @Override
    public Component getDescriptionText() {
        return Component.literal("用于在熔炉中强化装备, 初次强化装备需要先进行启能，强化后的装备将更加强大");
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.STRENGTHENING_STONE;
    }
}
