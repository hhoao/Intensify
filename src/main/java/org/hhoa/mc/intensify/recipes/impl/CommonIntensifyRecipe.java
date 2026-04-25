package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
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

    public CommonIntensifyRecipe(
            net.minecraft.util.ResourceLocation resourceLocation,
            float experience,
            int cookingTime) {
        super(resourceLocation, experience, cookingTime);
    }

    @Override
    public boolean matchesInternal(IInventory container, World level) {
        ItemStack fuel = container.getStackInSlot(1);

        return fuel.getItem() == ItemRegistry.ETERNAL_STONE.get();
    }

    @Override
    public void intensify(
            ItemStack tool,
            ToolIntensifyConfig toolItemIntensifyConfig,
            ServerPlayerEntity player) {
        CompoundNBT tag = tool.getOrCreateTag();
        tag.putBoolean("Unbreakable", true);
        player.sendMessage(TranslatableTexts.ETERNAL_SUCCESS.component(), player.getUniqueID());
    }

    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
