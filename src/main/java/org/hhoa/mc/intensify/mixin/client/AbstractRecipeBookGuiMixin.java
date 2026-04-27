package org.hhoa.mc.intensify.mixin.client;

import java.util.List;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractRecipeBookGui.class)
public abstract class AbstractRecipeBookGuiMixin {
    @Inject(method = "setupGhostRecipe", at = @At("HEAD"), cancellable = true)
    private void intensify$fillFuelOnlyGhostRecipe(
            IRecipe<?> recipe, List<Slot> slots, CallbackInfo ci) {
        if (!(recipe instanceof FurnaceRecipeBookDisplayRecipe)) {
            return;
        }

        FurnaceRecipeBookDisplayRecipe displayRecipe = (FurnaceRecipeBookDisplayRecipe) recipe;
        if (!displayRecipe.isFuelOnlyPlacement()) {
            return;
        }

        GhostRecipe ghostRecipe = ((RecipeBookGuiAccessor) this).intensify$getGhostRecipe();
        ghostRecipe.setRecipe(recipe);
        ghostRecipe.addIngredient(
                Ingredient.fromStacks(displayRecipe.getDisplayResult()),
                slots.get(2).xPos,
                slots.get(2).yPos);

        Slot fuelSlot = slots.get(1);
        if (!fuelSlot.getHasStack()) {
            ghostRecipe.addIngredient(
                    Ingredient.fromStacks(displayRecipe.getDisplayFuel()),
                    fuelSlot.xPos,
                    fuelSlot.yPos);
        }

        ci.cancel();
    }
}
