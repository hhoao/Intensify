package org.hhoa.mc.intensify.registry;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.recipes.impl.CommonIntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.EnengRecipe;
import org.hhoa.mc.intensify.recipes.impl.StrengtheningRecipe;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Intensify.MODID);

    public static final RegistryObject<RecipeSerializer<?>> STRENGTHENING_RECIPE =
        RECIPE_SERIALIZERS.register(IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
            () -> StrengtheningRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<?>> ENENG_RECIPE =
        RECIPE_SERIALIZERS.register(IntensifyStoneType.ENENG_STONE.getIdentifier(),
            () -> EnengRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<?>> COMMON_INTENSIFY_RECIPE=
        RECIPE_SERIALIZERS.register(IntensifyStoneType.INTENSIFY_STONE.getIdentifier(),
            () -> CommonIntensifyRecipe.SERIALIZER);

    public static void initialize(IEventBus iEventBus) {
        RECIPE_SERIALIZERS.register(iEventBus);
    }
}
