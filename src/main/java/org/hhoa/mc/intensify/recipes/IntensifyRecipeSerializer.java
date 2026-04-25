package org.hhoa.mc.intensify.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class IntensifyRecipeSerializer<T extends IntensifyRecipe> implements RecipeSerializer<T> {
    private final int defaultCookingTime;
    private final IntensifyRecipeSerializer.CookieBaker<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public IntensifyRecipeSerializer(
            IntensifyRecipeSerializer.CookieBaker<T> cookieBaker, int cookingTime) {
        this.defaultCookingTime = cookingTime;
        this.factory = cookieBaker;
        this.codec =
                RecordCodecBuilder.mapCodec(
                        instance ->
                                instance.group(
                                                Codec.FLOAT
                                                        .optionalFieldOf("experience", 0.0F)
                                                        .forGetter(IntensifyRecipe::getExperience),
                                                Codec.INT
                                                        .optionalFieldOf(
                                                                "cookingtime",
                                                                this.defaultCookingTime)
                                                        .forGetter(IntensifyRecipe::getCookingTime))
                                        .apply(instance, this.factory::create));
        this.streamCodec =
                StreamCodec.composite(
                        ByteBufCodecs.FLOAT,
                        IntensifyRecipe::getExperience,
                        ByteBufCodecs.VAR_INT,
                        IntensifyRecipe::getCookingTime,
                        this.factory::create);
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    public interface CookieBaker<T extends IntensifyRecipe> {
        T create(float experience, int cookingTime);
    }
}
