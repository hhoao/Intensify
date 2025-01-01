package org.hhoa.mc.intensify.provider;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.Config;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.recipes.impl.CommonIntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.EnengRecipe;
import org.hhoa.mc.intensify.recipes.impl.StrengtheningRecipe;
import org.hhoa.mc.intensify.registry.ItemRegistry;

import java.util.function.Consumer;

public class IntensifyStoneRecipeProvider extends RecipeProvider {
    public IntensifyStoneRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ConditionalRecipe.builder()
            .addCondition(TrueCondition.INSTANCE)
            .addRecipe(
                IntensifyStoneRecipeBuilder.builder(100f,
                        Config.BURN_TIME,
                        StrengtheningRecipe.SERIALIZER,
                        Advancement.Builder.recipeAdvancement()
                            .display(new ItemStack(Items.DIAMOND),
                                Component.translatable(TranslatableTexts.STRENGTHENING_TITLE),
                                Component.translatable(TranslatableTexts.STRENGTHENING_DESCRIPTION),
                                null, FrameType.TASK, true, true, false))
                    .unlockedBy("has_stone", has(ItemRegistry.STRENGTHENING_STONE.get()))
                    ::save)
            .generateAdvancement()
            .build(consumer, new ResourceLocation(Intensify.MODID, IntensifyStoneType.STRENGTHENING_STONE.getIdentifier()));

        ConditionalRecipe.builder()
            .addCondition(TrueCondition.INSTANCE)
            .addRecipe(
                IntensifyStoneRecipeBuilder.builder(100f,
                        Config.BURN_TIME,
                        EnengRecipe.SERIALIZER,
                        Advancement.Builder.recipeAdvancement()
                            .display(new ItemStack(Items.DIAMOND),
                                Component.translatable(TranslatableTexts.ENENG_TITLE),
                                Component.translatable(TranslatableTexts.ENENG_DESCRIPTION),
                                null, FrameType.TASK, true, true, false))
                    .unlockedBy("has_stone", has(ItemRegistry.ENENG_STONE.get()))
                    ::save)
            .generateAdvancement()
            .build(consumer, new ResourceLocation(Intensify.MODID, IntensifyStoneType.ENENG_STONE.getIdentifier()));

        ConditionalRecipe.builder()
            .addCondition(TrueCondition.INSTANCE)
            .addRecipe(
                IntensifyStoneRecipeBuilder.builder(100f,
                        Config.BURN_TIME,
                        CommonIntensifyRecipe.SERIALIZER,
                        Advancement.Builder.recipeAdvancement()
                            .display(new ItemStack(Items.DIAMOND),
                                Component.translatable(TranslatableTexts.ENENG_TITLE),
                                Component.translatable(TranslatableTexts.ENENG_DESCRIPTION),
                                null, FrameType.TASK, true, true, false))
                    .unlockedBy("has_stone", has(ItemRegistry.ETERNAL_STONE.get()))
                    ::save)
            .generateAdvancement()
            .build(consumer, new ResourceLocation(Intensify.MODID, IntensifyStoneType.INTENSIFY_STONE.getIdentifier()));
    }
}
