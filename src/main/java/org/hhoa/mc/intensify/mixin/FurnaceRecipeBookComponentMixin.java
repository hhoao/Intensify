package org.hhoa.mc.intensify.mixin;

import java.util.List;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent.class)
public abstract class FurnaceRecipeBookComponentMixin {
    @Inject(method = "setupGhostRecipe", at = @At("HEAD"), cancellable = true)
    private void intensify$fillFuelOnlyGhostRecipe(
            Recipe<?> recipe, List<Slot> slots, CallbackInfo ci) {
        if (!(recipe instanceof FurnaceRecipeBookDisplayRecipe displayRecipe)) {
            return;
        }
        if (!displayRecipe.isFuelOnlyPlacement()) {
            return;
        }

        GhostRecipe ghostRecipe = ((RecipeBookComponentAccessor) this).intensify$getGhostRecipe();
        ghostRecipe.setRecipe(recipe);
        ghostRecipe.addIngredient(Ingredient.of(displayRecipe.getDisplayResult()), slots.get(2).x, slots.get(2).y);

        Slot fuelSlot = slots.get(1);
        if (fuelSlot.getItem().isEmpty()) {
            ghostRecipe.addIngredient(Ingredient.of(displayRecipe.getDisplayFuel()), fuelSlot.x, fuelSlot.y);
        }

        ci.cancel();
    }
}
