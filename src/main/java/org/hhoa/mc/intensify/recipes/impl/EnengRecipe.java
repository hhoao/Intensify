package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
    public boolean matchesInternal(IInventory container, World level) {
        ItemStack tool = container.getStackInSlot(0);
        ItemStack fuel = container.getStackInSlot(1);

        boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(tool);
        return fuel.getItem() == ItemRegistry.ENENG_STONE.get() && !eneng;
    }

    @Override
    public void intensify(
            ItemStack tool,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayerEntity player) {
        EnengIntensifySystem enengIntensifySystem = IntensifyConfig.getEnengIntensifySystem();

        enengIntensifySystem.intensify(player, tool, toolItemIntensifyConfig);
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
