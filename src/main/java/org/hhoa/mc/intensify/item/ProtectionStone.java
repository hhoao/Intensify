package org.hhoa.mc.intensify.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class ProtectionStone extends IntensifyStone {
    public ProtectionStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.GREEN;
    }

    @Override
    public List<Component> getDescriptionTexts() {
        return List.of(TranslatableTexts.PROTECTION_STONE_DESCRIPTION.component());
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.PROTECTION_STONE;
    }
}
