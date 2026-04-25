package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class CommonIntensifyRecipe extends IntensifyRecipe {
    public static final IntensifyRecipeSerializer<CommonIntensifyRecipe> SERIALIZER =
            new IntensifyRecipeSerializer<>(
                    CommonIntensifyRecipe::new, IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME);

    public CommonIntensifyRecipe(float experience, int cookingTime) {
        super(experience, cookingTime);
    }

    @Override
    public void intensify(
            ItemStack tool,
            HolderLookup.Provider registries,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayer player) {
        tool.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        if (player != null) {
            player.sendSystemMessage(TranslatableTexts.ETERNAL_SUCCESS.component());
        }
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }

    @Override
    protected boolean matchesStartInternal(ItemStack tool, ItemStack fuel) {
        return fuel.getItem() == ItemRegistry.ETERNAL_STONE.get();
    }

    @Override
    protected boolean matchesContinuationInternal(ItemStack tool) {
        return true;
    }

    @Override
    protected String getOperationMarker() {
        return "ETERNAL";
    }
}
