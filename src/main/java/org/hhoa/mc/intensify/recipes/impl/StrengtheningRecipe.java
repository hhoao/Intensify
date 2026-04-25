package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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

    public StrengtheningRecipe(float experience, int cookingTime) {
        super(experience, cookingTime);
    }

    @Override
    public void intensify(
            ItemStack tool,
            HolderLookup.Provider registries,
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

    @Override
    protected boolean matchesStartInternal(ItemStack tool, ItemStack fuel) {
        return fuel.getItem() == ItemRegistry.STRENGTHENING_STONE.get()
                && IntensifyConfig.getEnengIntensifySystem().isEneng(tool);
    }

    @Override
    protected boolean matchesContinuationInternal(ItemStack tool) {
        return IntensifyConfig.getEnengIntensifySystem().isEneng(tool);
    }

    @Override
    protected String getOperationMarker() {
        return "STRENGTHENING";
    }
}
