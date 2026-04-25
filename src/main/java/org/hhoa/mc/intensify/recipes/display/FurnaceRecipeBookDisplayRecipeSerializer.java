package org.hhoa.mc.intensify.recipes.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FurnaceRecipeBookDisplayRecipeSerializer
        implements RecipeSerializer<FurnaceRecipeBookDisplayRecipe> {
    public static final FurnaceRecipeBookDisplayRecipeSerializer INSTANCE =
            new FurnaceRecipeBookDisplayRecipeSerializer();

    private final MapCodec<FurnaceRecipeBookDisplayRecipe> codec =
            RecordCodecBuilder.mapCodec(
                    instance ->
                            instance.group(
                                            ItemStack.STRICT_CODEC
                                                    .fieldOf("input")
                                                    .forGetter(
                                                            FurnaceRecipeBookDisplayRecipe
                                                                    ::getDisplayInput),
                                            ItemStack.STRICT_CODEC
                                                    .fieldOf("fuel")
                                                    .forGetter(
                                                            FurnaceRecipeBookDisplayRecipe
                                                                    ::getDisplayFuel),
                                            ItemStack.STRICT_CODEC
                                                    .fieldOf("result")
                                                    .forGetter(
                                                            FurnaceRecipeBookDisplayRecipe
                                                                    ::getDisplayResult))
                                    .apply(instance, FurnaceRecipeBookDisplayRecipe::new));

    private final StreamCodec<RegistryFriendlyByteBuf, FurnaceRecipeBookDisplayRecipe> streamCodec =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC,
                    FurnaceRecipeBookDisplayRecipe::getDisplayInput,
                    ItemStack.STREAM_CODEC,
                    FurnaceRecipeBookDisplayRecipe::getDisplayFuel,
                    ItemStack.STREAM_CODEC,
                    FurnaceRecipeBookDisplayRecipe::getDisplayResult,
                    FurnaceRecipeBookDisplayRecipe::new);

    private FurnaceRecipeBookDisplayRecipeSerializer() {}

    @Override
    public MapCodec<FurnaceRecipeBookDisplayRecipe> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FurnaceRecipeBookDisplayRecipe> streamCodec() {
        return this.streamCodec;
    }
}
