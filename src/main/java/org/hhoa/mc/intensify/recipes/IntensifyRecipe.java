package org.hhoa.mc.intensify.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public abstract class IntensifyRecipe extends AbstractCookingRecipe {
    protected IntensifyRecipe(float experience, int cookingTime) {
        super(
                "",
                CookingBookCategory.MISC,
                Ingredient.of(Items.FURNACE),
                new ItemStack(Items.FURNACE),
                experience,
                cookingTime);
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.FURNACE);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public abstract void intensify(
            ItemStack tool,
            HolderLookup.Provider registries,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayer player);

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return input.item().copyWithCount(1);
    }

    public abstract IntensifyRecipeSerializer<?> getSerializerInternal();

    public final boolean matchesStart(ItemStack tool, ItemStack fuel) {
        Item toolItem = tool.getItem();
        ToolIntensifyConfig toolConfig = IntensifyConfig.getToolIntensifyConfig(toolItem);
        return toolConfig != null && matchesStartInternal(tool, fuel);
    }

    public final boolean matchesContinuation(ItemStack tool) {
        Item toolItem = tool.getItem();
        ToolIntensifyConfig toolConfig = IntensifyConfig.getToolIntensifyConfig(toolItem);
        return toolConfig != null && matchesContinuationInternal(tool);
    }

    protected abstract boolean matchesStartInternal(ItemStack tool, ItemStack fuel);

    protected abstract boolean matchesContinuationInternal(ItemStack tool);

    protected abstract String getOperationMarker();

    @Override
    public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() {
        return getSerializerInternal();
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
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }
}
