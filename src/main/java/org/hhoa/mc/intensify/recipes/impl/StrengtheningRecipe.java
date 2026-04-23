package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class StrengtheningRecipe extends IntensifyRecipe {
    public StrengtheningRecipe() {
        super("strengthening");
    }

    @Override
    protected boolean matchesInput(ItemStack tool, ToolIntensifyConfig toolConfig) {
        return IntensifyConfig.getEnengIntensifySystem().isEneng(tool);
    }

    @Override
    protected boolean matchesCatalyst(ItemStack catalyst, ItemStack tool) {
        return catalyst.getItem() == ItemRegistry.STRENGTHENING_STONE;
    }

    @Override
    protected void intensify(
            ItemStack tool, ToolIntensifyConfig toolConfig, EntityPlayerMP player) {
        EnhancementIntensifySystem system = IntensifyConfig.getEnhancementIntensifySystem();
        system.intensify(player, tool, toolConfig);
    }
}
