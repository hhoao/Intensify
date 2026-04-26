package org.hhoa.mc.intensify.mixin;

import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.gui.screens.recipebook.RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {
    @Accessor("menu")
    RecipeBookMenu intensify$getMenu();
}
