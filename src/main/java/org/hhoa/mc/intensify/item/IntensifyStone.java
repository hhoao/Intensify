package org.hhoa.mc.intensify.item;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public abstract class IntensifyStone extends Item {
    public IntensifyStone(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME;
    }

    @Override
    public void addInformation(
            ItemStack itemStack,
            World level,
            List<ITextComponent> components,
            ITooltipFlag tooltipFlag) {
        ITextComponent component = components.get(0);
        if (component instanceof IFormattableTextComponent && getNameColor() != null) {
            IFormattableTextComponent mutableComponent = (IFormattableTextComponent) component;
            Style newStyle = component.getStyle().applyFormatting(getNameColor());
            mutableComponent.setStyle(newStyle);
        }
        List<ITextComponent> descriptions = getDescriptionTexts();
        if (descriptions != null) {
            for (ITextComponent description : descriptions) {
                if (description instanceof IFormattableTextComponent) {
                    Style style =
                            description.getStyle().applyFormatting(TextFormatting.DARK_PURPLE);
                    ((IFormattableTextComponent) description).setStyle(style);
                    components.add(description);
                }
            }
        }
        components.add(TranslatableTexts.INTENSIFY_ITEM_TIP.component());
        super.addInformation(itemStack, level, components, tooltipFlag);
    }

    public abstract TextFormatting getNameColor();

    public abstract List<ITextComponent> getDescriptionTexts();

    public abstract IntensifyStoneType getIdentifier();
}
