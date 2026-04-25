package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class StrengtheningRecipe extends IntensifyRecipe {
    public static final IntensifyRecipeSerializer<StrengtheningRecipe> SERIALIZER =
            new IntensifyRecipeSerializer<>(
                    StrengtheningRecipe::new, IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME);

    public StrengtheningRecipe(
            ResourceLocation resourceLocation, float experience, int cookingTime) {
        super(resourceLocation, experience, cookingTime);
    }

    @Override
    public boolean matchesInternal(Container container, Level level) {
        ItemStack toolItemStack = container.getItem(0);

        return IntensifyConfig.getEnengIntensifySystem().isEneng(toolItemStack)
                && container.getItem(1).getItem() == ItemRegistry.STRENGTHENING_STONE.get();
    }

    @Override
    public void intensify(
            ItemStack tool,
            RegistryAccess registryAccess,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayer player) {
        EnhancementIntensifySystem enhancementIntensifySystem =
                IntensifyConfig.getEnhancementIntensifySystem();
        enhancementIntensifySystem.intensify(player, tool, toolItemIntensifyConfig);
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
