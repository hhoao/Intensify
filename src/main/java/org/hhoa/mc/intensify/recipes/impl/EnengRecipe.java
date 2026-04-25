package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class EnengRecipe extends IntensifyRecipe {
    public static final IntensifyRecipeSerializer<EnengRecipe> SERIALIZER =
            new IntensifyRecipeSerializer<>(
                    EnengRecipe::new, IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME);

    public EnengRecipe(ResourceLocation resourceLocation, float experience, int cookingTime) {
        super(resourceLocation, experience, cookingTime);
    }

    @Override
    public boolean matchesInternal(Container container, Level level) {
        ItemStack tool = container.getItem(0);
        ItemStack fuel = container.getItem(1);

        boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(tool);
        return fuel.getItem() == ItemRegistry.ENENG_STONE.get() && !eneng;
    }

    @Override
    public void intensify(
            ItemStack tool,
            RegistryAccess registryAccess,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayer player) {
        EnengIntensifySystem enengIntensifySystem = IntensifyConfig.getEnengIntensifySystem();

        enengIntensifySystem.intensify(player, tool, toolItemIntensifyConfig);
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
