package org.hhoa.mc.intensify.recipes.display;

import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.Intensify;

// Display-only furnace recipe for recipe book content; never matches live furnace input.
public class FurnaceRecipeBookDisplayRecipe extends AbstractCookingRecipe {
    private static final String DISPLAY_GROUP = Intensify.MODID + ":furnace_recipe_book_display";
    private static final int DISPLAY_COOKING_TIME = 200;

    private final ItemStack displayInput;
    private final ItemStack displayFuel;
    private final ItemStack displayResult;

    public FurnaceRecipeBookDisplayRecipe(
            ItemStack displayInput, ItemStack displayFuel, ItemStack displayResult) {
        super(
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
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return this.displayResult.copy();
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.displayResult.copy();
    }

    @Override
    public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() {
        return FurnaceRecipeBookDisplayRecipeSerializer.INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<? extends AbstractCookingRecipe> getType() {
        return (RecipeType<? extends AbstractCookingRecipe>) RecipeType.SMELTING;
    }

    @Override
    protected Item furnaceIcon() {
        return Items.FURNACE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new FurnaceRecipeDisplay(
                        new SlotDisplay.ItemStackSlotDisplay(this.displayInput),
                        new SlotDisplay.ItemStackSlotDisplay(this.displayFuel),
                        new SlotDisplay.ItemStackSlotDisplay(this.displayResult),
                        new SlotDisplay.ItemSlotDisplay(this.furnaceIcon()),
                        DISPLAY_COOKING_TIME,
                        0.0F));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
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
}
