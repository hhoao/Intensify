package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.hhoa.mc.intensify.config.Config;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.recipes.IntensifyRecipeSerializer;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;


public class CommonIntensifyRecipe extends IntensifyRecipe {
    public static final IntensifyRecipeSerializer<CommonIntensifyRecipe> SERIALIZER =
        new IntensifyRecipeSerializer<>(CommonIntensifyRecipe::new, Config.BURN_TIME);

    public CommonIntensifyRecipe(
        ResourceLocation resourceLocation,
        float experience,
        int cookingTime
    ) {
        super(resourceLocation, experience, cookingTime);
    }

    @Override
    public boolean matchesInternal(Container container, Level level) {
        ItemStack fuel = container.getItem(1);

        return fuel.getItem() == ItemRegistry.ETERNAL_STONE.get();
    }

    @Override
    public void intensify(ItemStack tool,
                          RegistryAccess registryAccess,
                          ToolIntensifyConfig toolItemIntensifyConfig,
                          ServerPlayer player) {
        CompoundTag tag = tool.getOrCreateTag();
        tag.putBoolean("Unbreakable", true);
    }


    @Override
    public IntensifyRecipeSerializer<?> getSerializerInternal() {
        return SERIALIZER;
    }
}
