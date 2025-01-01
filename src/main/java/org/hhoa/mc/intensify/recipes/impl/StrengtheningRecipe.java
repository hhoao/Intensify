package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.Config;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;
import org.hhoa.mc.intensify.registry.ItemRegistry;


public class StrengtheningRecipe extends IntensifyRecipe {
    public static final IntensifyRecipeSerializer<StrengtheningRecipe> SERIALIZER =
        new IntensifyRecipeSerializer<>(StrengtheningRecipe::new, 5);
    private final float probability = 1f;

    public StrengtheningRecipe(ResourceLocation resourceLocation, float experience, int cookingTime) {
        super(resourceLocation, experience, cookingTime);
    }


    @Override
    public boolean matchesInternal(Container container, Level level) {
        ItemStack toolItemStack = container.getItem(0);

        Item toolItem = container.getItem(0).getItem();
        ToolIntensifyConfig toolItemIntensifyConfig = getToolItemIntensifyConfig(toolItem);
        if (toolItemIntensifyConfig == null) {
            return false;
        }

        return Config.getEnengIntensifySystem().isEneng(toolItemStack) &&
            container.getItem(1).getItem() == ItemRegistry.STRENGTHENING_STONE.get();
    }

    @Override
    public void intensify(ItemStack tool,
                               RegistryAccess registryAccess,
                               ToolIntensifyConfig toolItemIntensifyConfig,
                               ServerPlayer player) {
        EnhancementIntensifySystem enhancementIntensifySystem = Config.getEnhancementIntensifySystem();
        enhancementIntensifySystem.intensify(player, tool, toolItemIntensifyConfig);
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
