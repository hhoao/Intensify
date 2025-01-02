package org.hhoa.mc.intensify.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class EternalStone extends IntensifyStone{
    public EternalStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public Component getDescriptionText() {
        return Component.literal("使用它强化后的装备，将变得不可破坏");
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ETERNAL_STONE;
    }
}
