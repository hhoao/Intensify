package org.hhoa.mc.intensify.recipes.display;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.Intensify;

public class FurnaceRecipeBookDisplayRecipe extends SmeltingRecipe {
    private static final String DISPLAY_GROUP = Intensify.MODID + ":furnace_recipe_book_display";
    private static final int DISPLAY_COOKING_TIME = 200;

    private final ItemStack displayInput;
    private final ItemStack displayFuel;
    private final ItemStack displayResult;

    public FurnaceRecipeBookDisplayRecipe(
            ResourceLocation id, ItemStack displayInput, ItemStack displayFuel, ItemStack displayResult) {
        super(
                id,
                DISPLAY_GROUP,
                CookingBookCategory.MISC,
                Ingredient.of(displayInput.getItem()),
                displayResult,
                0.0F,
                DISPLAY_COOKING_TIME);
        this.displayInput = displayInput.copy();
        this.displayFuel = displayFuel.copy();
        this.displayResult = displayResult.copy();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.displayResult.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.displayResult.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FurnaceRecipeBookDisplayRecipeSerializer.INSTANCE;
    }

    @Override
    public boolean isSpecial() {
        return false;
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
