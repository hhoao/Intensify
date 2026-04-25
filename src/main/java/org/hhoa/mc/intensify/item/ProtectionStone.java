package org.hhoa.mc.intensify.item;

import java.util.Collections;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class ProtectionStone extends IntensifyStone {
    public ProtectionStone() {
        super();
    }

    @Override
    public TextFormatting getNameColor() {
        return TextFormatting.GREEN;
    }

    @Override
    public List<ITextComponent> getDescriptionTexts() {
        return Collections.singletonList(
                TranslatableTexts.PROTECTION_STONE_DESCRIPTION.component());
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.PROTECTION_STONE;
    }
}
