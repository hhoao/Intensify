package org.hhoa.mc.intensify.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public abstract class IntensifyStone extends Item {
    public IntensifyStone(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
        return IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            TooltipContext tooltipContext,
            List<Component> components,
            TooltipFlag tooltipFlag) {
        Component component = components.get(0);
        if (component instanceof MutableComponent && getNameColor() != null) {
            MutableComponent mutableComponent = (MutableComponent) component;
            Style newStyle = component.getStyle().withColor(getNameColor());
            mutableComponent.setStyle(newStyle);
        }
        List<Component> descriptions = getDescriptionTexts();
        if (descriptions != null) {
            for (Component description : descriptions) {
                if (description instanceof MutableComponent) {
                    Style style = description.getStyle().withColor(ChatFormatting.DARK_PURPLE);
                    ((MutableComponent) description).setStyle(style);
                    components.add(description);
                }
            }
        }
        components.add(TranslatableTexts.INTENSIFY_ITEM_TIP.component());
        super.appendHoverText(itemStack, tooltipContext, components, tooltipFlag);
    }

    public abstract ChatFormatting getNameColor();

    public abstract List<Component> getDescriptionTexts();

    public abstract IntensifyStoneType getIdentifier();
}
