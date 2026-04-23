package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class EnengRecipe extends IntensifyRecipe {
    public EnengRecipe() {
        super("eneng");
    }

    @Override
    protected boolean matchesInput(ItemStack tool, ToolIntensifyConfig toolConfig) {
        return !IntensifyConfig.getEnengIntensifySystem().isEneng(tool);
    }

    @Override
    protected boolean matchesCatalyst(ItemStack catalyst, ItemStack tool) {
        return catalyst.getItem() == ItemRegistry.ENENG_STONE;
    }

    @Override
    protected void intensify(
            ItemStack tool, ToolIntensifyConfig toolConfig, EntityPlayerMP player) {
        EnengIntensifySystem system = IntensifyConfig.getEnengIntensifySystem();
        system.intensify(player, tool, toolConfig);
    }
}
