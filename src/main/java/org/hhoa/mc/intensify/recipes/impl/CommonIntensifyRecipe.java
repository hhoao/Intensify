package org.hhoa.mc.intensify.recipes.impl;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class CommonIntensifyRecipe extends IntensifyRecipe {
    public CommonIntensifyRecipe() {
        super("common_intensify");
    }

    @Override
    protected boolean matchesInput(ItemStack tool, ToolIntensifyConfig toolConfig) {
        return !tool.isEmpty();
    }

    @Override
    protected boolean matchesCatalyst(ItemStack catalyst, ItemStack tool) {
        return catalyst.getItem() == ItemRegistry.ETERNAL_STONE;
    }

    @Override
    protected void intensify(
            ItemStack tool, ToolIntensifyConfig toolConfig, EntityPlayerMP player) {
        getOrCreateTag(tool).setBoolean("Unbreakable", true);
        if (player != null) {
            player.sendMessage(TranslatableTexts.ETERNAL_SUCCESS.component());
        }
    }
}
