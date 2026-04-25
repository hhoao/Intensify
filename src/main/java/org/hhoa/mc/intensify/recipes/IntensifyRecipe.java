package org.hhoa.mc.intensify.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public abstract class IntensifyRecipe extends SmeltingRecipe {
    protected IntensifyRecipe(float experience, int cookingTime) {
        super("", CookingBookCategory.MISC, Ingredient.EMPTY, ItemStack.EMPTY, experience, cookingTime);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.FURNACE);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
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
    public RecipeSerializer<?> getSerializer() {
        return getSerializerInternal();
    }
}
