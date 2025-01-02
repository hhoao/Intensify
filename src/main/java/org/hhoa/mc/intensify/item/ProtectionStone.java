package org.hhoa.mc.intensify.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ProtectionStone extends IntensifyStone{
    public ProtectionStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.GREEN;
    }

    @Override
    public Component getDescriptionText() {
        return Component.literal("当装备强化失败时，背包中的此物品可保证装备失败后不降低等级");
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.PROTECTION_STONE;
    }
}
