package org.hhoa.mc.intensify.registry;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipeSerializer;
import org.hhoa.mc.intensify.recipes.impl.CommonIntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.EnengRecipe;
import org.hhoa.mc.intensify.recipes.impl.StrengtheningRecipe;

public class RecipeRegistry {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Intensify.MODID);

    public static final RegistryObject<IRecipeSerializer<?>> STRENGTHENING_RECIPE =
            RECIPE_SERIALIZERS.register(
                    IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
                    () -> StrengtheningRecipe.SERIALIZER);

    public static final RegistryObject<IRecipeSerializer<?>> ENENG_RECIPE =
            RECIPE_SERIALIZERS.register(
                    IntensifyStoneType.ENENG_STONE.getIdentifier(), () -> EnengRecipe.SERIALIZER);

    public static final RegistryObject<IRecipeSerializer<?>> COMMON_INTENSIFY_RECIPE =
            RECIPE_SERIALIZERS.register(
                    IntensifyStoneType.INTENSIFY_STONE.getIdentifier(),
                    () -> CommonIntensifyRecipe.SERIALIZER);

    public static final RegistryObject<IRecipeSerializer<FurnaceRecipeBookDisplayRecipe>>
            FURNACE_RECIPE_BOOK_DISPLAY_RECIPE =
                    RECIPE_SERIALIZERS.register(
                            "furnace_recipe_book_display",
                            () -> FurnaceRecipeBookDisplayRecipeSerializer.INSTANCE);

    public static void initialize(IEventBus iEventBus) {
        RECIPE_SERIALIZERS.register(iEventBus);
    }
}
