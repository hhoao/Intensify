package org.hhoa.mc.intensify.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class EternalStone extends IntensifyStone {
    public EternalStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public List<Component> getDescriptionTexts() {
        return List.of(TranslatableTexts.ETERNAL_STONE_DESCRIPTION.component());
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ETERNAL_STONE;
    }
}
