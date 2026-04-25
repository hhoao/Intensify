package org.hhoa.mc.intensify.item;

import java.util.Collections;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class EternalStone extends IntensifyStone {
    public EternalStone(Properties properties) {
        super(properties);
    }

    @Override
    public TextFormatting getNameColor() {
        return TextFormatting.YELLOW;
    }

    @Override
    public List<ITextComponent> getDescriptionTexts() {
        return Collections.singletonList(TranslatableTexts.ETERNAL_STONE_DESCRIPTION.component());
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ETERNAL_STONE;
    }
}
