package org.hhoa.mc.intensify.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.Config;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return Config.BURN_TIME;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        Component component = components.get(0);
        if (component instanceof MutableComponent && getNameColor() != null) {
            MutableComponent mutableComponent = (MutableComponent) component;
            Style newStyle = component.getStyle().withColor(getNameColor());
            mutableComponent.setStyle(newStyle);
        }
        Component description = getDescriptionText();
        if (description != null) {
            components.add(description);
        }
        components.add(Component.literal("(请勿使用强化石强化非装备物品!!)"));
        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }

    public abstract ChatFormatting getNameColor();

    public abstract Component getDescriptionText();

    public abstract IntensifyStoneType getIdentifier();
}
