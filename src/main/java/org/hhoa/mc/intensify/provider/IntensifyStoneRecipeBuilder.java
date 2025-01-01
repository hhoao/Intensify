package org.hhoa.mc.intensify.provider;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IntensifyStoneRecipeBuilder implements CustomRecipeBuilder {
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement;
    private final IntensifyRecipeSerializer<?> serializer;

    private IntensifyStoneRecipeBuilder(float experience,
                                        int cookingTime,
                                        IntensifyRecipeSerializer<?> serializer,
                                        Advancement.Builder advancementBuilder) {
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
        this.advancement = advancementBuilder;
    }


    public static IntensifyStoneRecipeBuilder builder(float experience,
                                                      int cookingTime,
                                                      IntensifyRecipeSerializer<?> serializer,
                                                      Advancement.Builder advancementBuilder) {
        return new IntensifyStoneRecipeBuilder(experience, cookingTime, serializer, advancementBuilder);
    }

    @Override
    public IntensifyStoneRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(name, criterionTriggerInstance);
        return this;
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeConsumer, ResourceLocation resourceLocation) {
        this.advancement
            .parent(ROOT_RECIPE_ADVANCEMENT)
            .requirements(RequirementsStrategy.OR);
        recipeConsumer.accept(
            new IntensifyStoneRecipeBuilder.Result(
                this.experience,
                this.cookingTime,
                this.advancement,
                resourceLocation,
                this.serializer));
    }

    @Override
    public ResourceLocation getAdvancementId() {
        return new ResourceLocation(Intensify.MODID, "recipes/");
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final IntensifyRecipeSerializer<?> serializer;

        public Result(float experience,
                      int cookingTime,
                      Advancement.Builder advancement,
                      ResourceLocation advancementId,
                      IntensifyRecipeSerializer<?> serializer) {
            this.id = new ResourceLocation("fsdfsdf");
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            jsonObject.addProperty("experience", this.experience);
            jsonObject.addProperty("cookingtime", this.cookingTime);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        @javax.annotation.Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
