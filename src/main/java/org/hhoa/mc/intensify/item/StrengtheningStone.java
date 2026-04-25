package org.hhoa.mc.intensify.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class StrengtheningStone extends IntensifyStone {
    public StrengtheningStone(Properties properties) {
        super(properties);
    }

    @Override
    public TextFormatting getNameColor() {
        return TextFormatting.BLUE;
    }

    @Override
    public List<ITextComponent> getDescriptionTexts() {
        ArrayList<ITextComponent> components = new ArrayList<>();
        components.add(TranslatableTexts.STRENGTHENING_STONE_DESCRIPTION.component());
        components.add(TranslatableTexts.STRENGTHENING_STONE_DESCRIPTION_TIP.component());
        return components;
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.STRENGTHENING_STONE;
    }

    @Override
    public Collection<ItemGroup> getCreativeTabs() {
        return super.getCreativeTabs();
    }
}
