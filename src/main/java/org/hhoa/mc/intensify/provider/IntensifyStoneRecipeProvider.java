package org.hhoa.mc.intensify.provider;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.recipes.impl.CommonIntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.EnengRecipe;
import org.hhoa.mc.intensify.recipes.impl.StrengtheningRecipe;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyStoneRecipeProvider extends RecipeProvider {
    public static final String HAS_STONE = Intensify.locationStr("has_stone");
    public static final String HAS_TOOL = Intensify.locationStr("has_tool");

    public IntensifyStoneRecipeProvider(DataGenerator p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ConditionalRecipe.builder()
                .addCondition(TrueCondition.INSTANCE)
                .addRecipe(
                        IntensifyStoneRecipeBuilder.builder(
                                                new ResourceLocation(
                                                        Intensify.MODID,
                                                        IntensifyStoneType.STRENGTHENING_STONE
                                                                .getIdentifier()),
                                                IntensifyConfig.DEFAULT_INTENSIFY_STONE_EXPERIENCE,
                                                IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME,
                                                StrengtheningRecipe.SERIALIZER,
                                                Advancement.Builder.builder()
                                                        .withDisplay(
                                                                new ItemStack(Items.COAL),
                                                                TranslatableTexts
                                                                        .STRENGTHENING_ADVANCEMENT_TITLE
                                                                        .component(),
                                                                TranslatableTexts
                                                                        .STRENGTHENING_ADVANCEMENT_DESCRIPTION
                                                                        .component(),
                                                                null,
                                                                FrameType.TASK,
                                                                true,
                                                                true,
                                                                false))
                                        .unlockedBy(
                                                HAS_STONE,
                                                hasItem(ItemRegistry.STRENGTHENING_STONE.get()))
                                ::save)
                .generateAdvancement()
                .build(
                        consumer,
                        new ResourceLocation(
                                Intensify.MODID,
                                IntensifyStoneType.STRENGTHENING_STONE.getIdentifier()));

        ConditionalRecipe.builder()
                .addCondition(TrueCondition.INSTANCE)
                .addRecipe(
                        IntensifyStoneRecipeBuilder.builder(
                                                new ResourceLocation(
                                                        Intensify.MODID,
                                                        IntensifyStoneType.ENENG_STONE
                                                                .getIdentifier()),
                                                IntensifyConfig.DEFAULT_INTENSIFY_STONE_EXPERIENCE,
                                                IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME,
                                                EnengRecipe.SERIALIZER,
                                                Advancement.Builder.builder()
                                                        .withDisplay(
                                                                new ItemStack(Items.LAPIS_LAZULI),
                                                                TranslatableTexts
                                                                        .ENENG_ADVANCEMENT_TITLE
                                                                        .component(),
                                                                TranslatableTexts
                                                                        .ENENG_ADVANCEMENT_DESCRIPTION
                                                                        .component(),
                                                                null,
                                                                FrameType.TASK,
                                                                true,
                                                                true,
                                                                false))
                                        .unlockedBy(
                                                HAS_STONE, hasItem(ItemRegistry.ENENG_STONE.get()))
                                ::save)
                .generateAdvancement()
                .build(
                        consumer,
                        new ResourceLocation(
                                Intensify.MODID, IntensifyStoneType.ENENG_STONE.getIdentifier()));

        ConditionalRecipe.builder()
                .addCondition(TrueCondition.INSTANCE)
                .addRecipe(
                        IntensifyStoneRecipeBuilder.builder(
                                                new ResourceLocation(
                                                        Intensify.MODID,
                                                        IntensifyStoneType.INTENSIFY_STONE
                                                                .getIdentifier()),
                                                IntensifyConfig.DEFAULT_INTENSIFY_STONE_EXPERIENCE,
                                                IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME,
                                                CommonIntensifyRecipe.SERIALIZER,
                                                Advancement.Builder.builder()
                                                        .withDisplay(
                                                                new ItemStack(Items.DIAMOND),
                                                                TranslatableTexts
                                                                        .ETERNAL_ADVANCEMENT_TITLE
                                                                        .component(),
                                                                TranslatableTexts
                                                                        .ETERNAL_ADVANCEMENT_DESCRIPTION
                                                                        .component(),
                                                                null,
                                                                FrameType.TASK,
                                                                true,
                                                                true,
                                                                false))
                                        .unlockedBy(
                                                HAS_STONE,
                                                hasItem(ItemRegistry.ETERNAL_STONE.get()))
                                ::save)
                .generateAdvancement()
                .build(
                        consumer,
                        new ResourceLocation(
                                Intensify.MODID,
                                IntensifyStoneType.INTENSIFY_STONE.getIdentifier()));
    }
}
