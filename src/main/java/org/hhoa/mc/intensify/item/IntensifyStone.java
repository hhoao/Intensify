package org.hhoa.mc.intensify.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
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
    public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType, FuelValues fuelValues) {
        return IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag) {
        List<Component> descriptions = getDescriptionTexts();
        if (descriptions != null) {
            for (Component description : descriptions) {
                if (description instanceof MutableComponent) {
                    Style style = description.getStyle().withColor(ChatFormatting.DARK_PURPLE);
                    ((MutableComponent) description).setStyle(style);
                    consumer.accept(description);
                }
            }
        }
        consumer.accept(TranslatableTexts.INTENSIFY_ITEM_TIP.component());
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        Component name = super.getName(itemStack);
        if (getNameColor() == null || !(name instanceof MutableComponent mutableName)) {
            return name;
        }
        mutableName.setStyle(name.getStyle().withColor(getNameColor()));
        return mutableName;
    }

    public abstract ChatFormatting getNameColor();

    public abstract List<Component> getDescriptionTexts();

    public abstract IntensifyStoneType getIdentifier();
}
