package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
    public boolean matchesInternal(IInventory container, World level) {
        ItemStack toolItemStack = container.getStackInSlot(0);

        return IntensifyConfig.getEnengIntensifySystem().isEneng(toolItemStack)
                && container.getStackInSlot(1).getItem() == ItemRegistry.STRENGTHENING_STONE.get();
    }

    @Override
    public void intensify(
            ItemStack tool,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayerEntity player) {
        EnhancementIntensifySystem enhancementIntensifySystem =
                IntensifyConfig.getEnhancementIntensifySystem();
        enhancementIntensifySystem.intensify(player, tool, toolItemIntensifyConfig);
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
