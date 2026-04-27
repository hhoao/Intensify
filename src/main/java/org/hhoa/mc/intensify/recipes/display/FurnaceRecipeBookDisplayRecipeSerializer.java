package org.hhoa.mc.intensify.recipes.display;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FurnaceRecipeBookDisplayRecipeSerializer
        implements RecipeSerializer<FurnaceRecipeBookDisplayRecipe> {
    public static final FurnaceRecipeBookDisplayRecipeSerializer INSTANCE =
            new FurnaceRecipeBookDisplayRecipeSerializer();

    private FurnaceRecipeBookDisplayRecipeSerializer() {}

    @Override
    public FurnaceRecipeBookDisplayRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        return new FurnaceRecipeBookDisplayRecipe(
                recipeId,
                stackFromJson(GsonHelper.getAsJsonObject(json, "input")),
                stackFromJson(GsonHelper.getAsJsonObject(json, "fuel")),
                stackFromJson(GsonHelper.getAsJsonObject(json, "result")));
    }

    @Override
    public FurnaceRecipeBookDisplayRecipe fromNetwork(
            ResourceLocation recipeId, FriendlyByteBuf buffer) {
        return new FurnaceRecipeBookDisplayRecipe(
                recipeId, buffer.readItem(), buffer.readItem(), buffer.readItem());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FurnaceRecipeBookDisplayRecipe recipe) {
        buffer.writeItem(recipe.getDisplayInput());
        buffer.writeItem(recipe.getDisplayFuel());
        buffer.writeItem(recipe.getDisplayResult());
    }

    private static ItemStack stackFromJson(JsonObject json) {
        ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(json, "item"));
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item for display recipe: " + itemId);
        }
        int count = GsonHelper.getAsInt(json, "count", 1);
        return new ItemStack(item, count);
    }
}
