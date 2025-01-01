package org.hhoa.mc.intensify.recipes;


import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class IntensifyRecipeSerializer<T extends IntensifyRecipe> implements RecipeSerializer<T> {
    private final int defaultCookingTime;
    private final IntensifyRecipeSerializer.CookieBaker<T> factory;

    public IntensifyRecipeSerializer(IntensifyRecipeSerializer.CookieBaker<T> cookieBaker, int cookingTime) {
        this.defaultCookingTime = cookingTime;
        this.factory = cookieBaker;
    }

    @Override
    public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
        int cookingTime = GsonHelper.getAsInt(jsonObject, "cookingtime", this.defaultCookingTime);
        return this.factory.create(resourceLocation, experience, cookingTime);
    }

    @Override
    public T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
        float experience = byteBuf.readFloat();
        int cookingTime = byteBuf.readVarInt();
        return this.factory.create(resourceLocation, experience, cookingTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf byteBuf, IntensifyRecipe recipe) {
        byteBuf.writeFloat(recipe.getExperience());
        byteBuf.writeVarInt(recipe.getCookingTime());
    }

    public interface CookieBaker<T extends IntensifyRecipe> {
        T create(
            ResourceLocation resourceLocation,
            float experience,
            int cookingTime);
    }
}
