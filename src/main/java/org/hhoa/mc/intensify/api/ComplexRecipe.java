package org.hhoa.mc.intensify.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ComplexRecipe {
    ItemStack getRecipeOutput(IInventory container);
}
