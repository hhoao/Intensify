package org.hhoa.mc.intensify.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class EnengStone extends IntensifyStone {
    public EnengStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public List<Component> getDescriptionTexts() {
        return List.of(TranslatableTexts.ENENG_STONE_DESCRIPTION.component());
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ENENG_STONE;
    }
}
