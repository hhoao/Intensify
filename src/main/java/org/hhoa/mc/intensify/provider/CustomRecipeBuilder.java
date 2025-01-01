package org.hhoa.mc.intensify.provider;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface CustomRecipeBuilder {
   ResourceLocation ROOT_RECIPE_ADVANCEMENT = new ResourceLocation("recipes/root");

   CustomRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTriggerInstance);

   void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation resourceLocation);

   default void save(Consumer<FinishedRecipe> p_176499_) {
      this.save(p_176499_, getAdvancementId());
   }

   ResourceLocation getAdvancementId();
}
