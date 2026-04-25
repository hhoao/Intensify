package org.hhoa.mc.intensify.provider;

import java.util.function.Consumer;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

public interface CustomRecipeBuilder {
    ResourceLocation ROOT_RECIPE_ADVANCEMENT = new ResourceLocation("recipes/root");

    CustomRecipeBuilder unlockedBy(String name, CriterionInstance criterionTriggerInstance);

    void save(Consumer<IFinishedRecipe> finishedRecipeConsumer, ResourceLocation resourceLocation);

    default void save(Consumer<IFinishedRecipe> p_176499_) {
        this.save(p_176499_, getAdvancementId());
    }

    ResourceLocation getAdvancementId();
}
