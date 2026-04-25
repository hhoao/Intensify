package org.hhoa.mc.intensify.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.recipes.impl.CommonIntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.EnengRecipe;
import org.hhoa.mc.intensify.recipes.impl.StrengtheningRecipe;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Intensify.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> STRENGTHENING_RECIPE =
            RECIPE_SERIALIZERS.register(
                    IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
                    () -> StrengtheningRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> ENENG_RECIPE =
            RECIPE_SERIALIZERS.register(
                    IntensifyStoneType.ENENG_STONE.getIdentifier(), () -> EnengRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMMON_INTENSIFY_RECIPE =
            RECIPE_SERIALIZERS.register(
                    IntensifyStoneType.INTENSIFY_STONE.getIdentifier(),
                    () -> CommonIntensifyRecipe.SERIALIZER);

    public static void initialize(IEventBus iEventBus) {
        RECIPE_SERIALIZERS.register(iEventBus);
    }
}
