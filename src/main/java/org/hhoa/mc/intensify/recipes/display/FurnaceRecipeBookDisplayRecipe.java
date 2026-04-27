package org.hhoa.mc.intensify.recipes.display;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.hhoa.mc.intensify.Intensify;

public class FurnaceRecipeBookDisplayRecipe extends FurnaceRecipe {
    private static final String DISPLAY_GROUP = Intensify.MODID + ":furnace_recipe_book_display";
    private static final int DISPLAY_COOKING_TIME = 200;

    private final ItemStack displayInput;
    private final ItemStack displayFuel;
    private final ItemStack displayResult;

    public FurnaceRecipeBookDisplayRecipe(
            ResourceLocation id,
            ItemStack displayInput,
            ItemStack displayFuel,
            ItemStack displayResult) {
        super(
                id,
                DISPLAY_GROUP,
                Ingredient.fromStacks(displayInput),
                displayResult,
                0.0F,
                DISPLAY_COOKING_TIME);
        this.displayInput = displayInput.copy();
        this.displayFuel = displayFuel.copy();
        this.displayResult = displayResult.copy();
    }

    @Override
    public boolean matches(IInventory inventory, World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        return this.displayResult.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.displayResult.copy();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return FurnaceRecipeBookDisplayRecipeSerializer.INSTANCE;
    }

    public ItemStack getDisplayInput() {
        return this.displayInput.copy();
    }

    public ItemStack getDisplayFuel() {
        return this.displayFuel.copy();
    }

    public ItemStack getDisplayResult() {
        return this.displayResult.copy();
    }

    public boolean isFuelOnlyPlacement() {
        return true;
    }

    public ItemStack getPlaceableFuel() {
        return this.displayFuel.copy();
    }
}
