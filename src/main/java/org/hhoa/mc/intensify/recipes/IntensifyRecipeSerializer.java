package org.hhoa.mc.intensify.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;

public class IntensifyRecipeSerializer<T extends IntensifyRecipe> implements IRecipeSerializer<T> {
    private final int defaultCookingTime;
    private final IntensifyRecipeSerializer.CookieBaker<T> factory;
    private ResourceLocation registryName;

    public IntensifyRecipeSerializer(
            IntensifyRecipeSerializer.CookieBaker<T> cookieBaker, int cookingTime) {
        this.defaultCookingTime = cookingTime;
        this.factory = cookieBaker;
        this.registryName = new ResourceLocation(Intensify.MODID, "intensify_recipe_serializer");
    }

    @Override
    public T read(ResourceLocation resourceLocation, JsonObject jsonObject) {
        float experience = JSONUtils.getFloat(jsonObject, "experience", 0.0F);
        int cookingTime = JSONUtils.getInt(jsonObject, "cookingtime", this.defaultCookingTime);
        return this.factory.create(resourceLocation, experience, cookingTime);
    }

    @Override
    public T read(ResourceLocation resourceLocation, PacketBuffer byteBuf) {
        float experience = byteBuf.readFloat();
        int cookingTime = byteBuf.readVarInt();
        return this.factory.create(resourceLocation, experience, cookingTime);
    }

    @Override
    public void write(PacketBuffer byteBuf, IntensifyRecipe recipe) {
        byteBuf.writeFloat(recipe.getExperience());
        byteBuf.writeVarInt(recipe.getCookTime());
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

    public interface CookieBaker<T extends IntensifyRecipe> {
        T create(ResourceLocation resourceLocation, float experience, int cookingTime);
    }
}
