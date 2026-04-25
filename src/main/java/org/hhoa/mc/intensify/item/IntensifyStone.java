package org.hhoa.mc.intensify.item;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public abstract class IntensifyStone extends Item {
    public IntensifyStone() {
        super();
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME;
    }

    @Override
    public void addInformation(
            ItemStack itemStack, World level, List<String> components, ITooltipFlag tooltipFlag) {
        if (!components.isEmpty() && getNameColor() != null) {
            components.set(0, getNameColor() + components.get(0));
        }

        List<ITextComponent> descriptions = getDescriptionTexts();
        if (descriptions != null) {
            for (ITextComponent description : descriptions) {
                ITextComponent styledDescription =
                        description.createCopy()
                                .setStyle(
                                        description
                                                .getStyle()
                                                .createShallowCopy()
                                                .setColor(TextFormatting.DARK_PURPLE));
                components.add(styledDescription.getFormattedText());
            }
        }
        components.add(TranslatableTexts.INTENSIFY_ITEM_TIP.component().getFormattedText());
        super.addInformation(itemStack, level, components, tooltipFlag);
    }

    public abstract TextFormatting getNameColor();

    public abstract List<ITextComponent> getDescriptionTexts();

    public abstract IntensifyStoneType getIdentifier();
}
