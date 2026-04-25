package org.hhoa.mc.intensify.provider;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;

public class IntensifyStoneRecipeBuilder implements CustomRecipeBuilder {
    private final ResourceLocation id;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement;
    private final IntensifyRecipeSerializer<?> serializer;

    private IntensifyStoneRecipeBuilder(
            ResourceLocation id,
            float experience,
            int cookingTime,
            IntensifyRecipeSerializer<?> serializer,
            Advancement.Builder advancementBuilder) {
        this.id = id;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
        this.advancement = advancementBuilder;
    }

    public static IntensifyStoneRecipeBuilder builder(
            ResourceLocation resourceLocation,
            float experience,
            int cookingTime,
            IntensifyRecipeSerializer<?> serializer,
            Advancement.Builder advancementBuilder) {
        return new IntensifyStoneRecipeBuilder(
                resourceLocation, experience, cookingTime, serializer, advancementBuilder);
    }

    @Override
    public IntensifyStoneRecipeBuilder unlockedBy(
            String name, CriterionInstance criterionTriggerInstance) {
        this.advancement.withCriterion(name, criterionTriggerInstance);
        return this;
    }

    @Override
    public void save(Consumer<IFinishedRecipe> recipeConsumer, ResourceLocation resourceLocation) {
        this.advancement
                .withParentId(ROOT_RECIPE_ADVANCEMENT)
                .withRequirementsStrategy(IRequirementsStrategy.OR);
        recipeConsumer.accept(
                new IntensifyStoneRecipeBuilder.Result(
                        this.id,
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

    static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final IntensifyRecipeSerializer<?> serializer;

        public Result(
                ResourceLocation id,
                float experience,
                int cookingTime,
                Advancement.Builder advancement,
                ResourceLocation advancementId,
                IntensifyRecipeSerializer<?> serializer) {
            this.id = id;
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            jsonObject.addProperty("experience", this.experience);
            jsonObject.addProperty("cookingtime", this.cookingTime);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public JsonObject getAdvancementJson() {
            return this.advancement.serialize();
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementID() {
            return this.advancementId;
        }
    }
}
