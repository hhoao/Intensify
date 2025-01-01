package org.hhoa.mc.intensify.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.hhoa.mc.intensify.config.Config;

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

    public abstract IntensifyStoneType getIdentifier();
}
