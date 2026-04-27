package org.hhoa.mc.intensify.recipes.display;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class FurnaceRecipeBookDisplayRecipeSerializer
        implements IRecipeSerializer<FurnaceRecipeBookDisplayRecipe> {
    public static final FurnaceRecipeBookDisplayRecipeSerializer INSTANCE =
            new FurnaceRecipeBookDisplayRecipeSerializer();

    private ResourceLocation registryName;

    private FurnaceRecipeBookDisplayRecipeSerializer() {
        this.registryName = new ResourceLocation("intensify", "furnace_recipe_book_display");
    }

    @Override
    public FurnaceRecipeBookDisplayRecipe read(ResourceLocation recipeId, JsonObject json) {
        return new FurnaceRecipeBookDisplayRecipe(
                recipeId,
                stackFromJson(JSONUtils.getJsonObject(json, "input")),
                stackFromJson(JSONUtils.getJsonObject(json, "fuel")),
                stackFromJson(JSONUtils.getJsonObject(json, "result")));
    }

    @Override
    public FurnaceRecipeBookDisplayRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        return new FurnaceRecipeBookDisplayRecipe(
                recipeId, buffer.readItemStack(), buffer.readItemStack(), buffer.readItemStack());
    }

    @Override
    public void write(PacketBuffer buffer, FurnaceRecipeBookDisplayRecipe recipe) {
        buffer.writeItemStack(recipe.getDisplayInput());
        buffer.writeItemStack(recipe.getDisplayFuel());
        buffer.writeItemStack(recipe.getDisplayResult());
    }

    @Override
    public IRecipeSerializer<?> setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public Class<IRecipeSerializer<?>> getRegistryType() {
        return null;
    }

    private static ItemStack stackFromJson(JsonObject json) {
        ResourceLocation itemId = new ResourceLocation(JSONUtils.getString(json, "item"));
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item for display recipe: " + itemId);
        }
        int count = JSONUtils.getInt(json, "count", 1);
        return new ItemStack(item, count);
    }
}
