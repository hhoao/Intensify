package org.hhoa.mc.intensify.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class StrengtheningStone extends IntensifyStone {
    public StrengtheningStone(Properties properties) {
        super(properties);
    }

    @Override
    public ChatFormatting getNameColor() {
        return ChatFormatting.BLUE;
    }

    @Override
    public List<Component> getDescriptionTexts() {
        ArrayList<Component> components = new ArrayList<>();
        components.add(TranslatableTexts.STRENGTHENING_STONE_DESCRIPTION.component());
        components.add(TranslatableTexts.STRENGTHENING_STONE_DESCRIPTION_TIP.component());
        return components;
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.STRENGTHENING_STONE;
    }
}
