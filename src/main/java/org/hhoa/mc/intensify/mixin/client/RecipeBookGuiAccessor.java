package org.hhoa.mc.intensify.mixin.client;

import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookGui.class)
public interface RecipeBookGuiAccessor {
    @Accessor("ghostRecipe")
    GhostRecipe intensify$getGhostRecipe();
}
