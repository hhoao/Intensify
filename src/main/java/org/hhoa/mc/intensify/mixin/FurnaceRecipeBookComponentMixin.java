package org.hhoa.mc.intensify.mixin;

import java.util.List;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent.class)
public abstract class FurnaceRecipeBookComponentMixin {
    @Inject(method = "fillGhostRecipe", at = @At("HEAD"), cancellable = true)
    private void intensify$fillFuelOnlyGhostRecipe(
            GhostSlots ghostSlots,
            RecipeDisplay recipeDisplay,
            ContextMap contextMap,
            CallbackInfo ci) {
        if (!(recipeDisplay instanceof FurnaceRecipeDisplay furnaceRecipeDisplay)) {
            return;
        }
        if (!intensify$isFuelOnlyDisplay(furnaceRecipeDisplay, contextMap)) {
            return;
        }

        AbstractFurnaceMenu menu =
                (AbstractFurnaceMenu)
                        ((RecipeBookComponentAccessor) this).intensify$getMenu();
        ghostSlots.setResult(menu.getResultSlot(), contextMap, furnaceRecipeDisplay.result());

        Slot fuelSlot = menu.slots.get(AbstractFurnaceMenu.FUEL_SLOT);
        if (fuelSlot.getItem().isEmpty()) {
            ghostSlots.setInput(fuelSlot, contextMap, furnaceRecipeDisplay.fuel());
        }

        ci.cancel();
    }

    private static boolean intensify$isFuelOnlyDisplay(
            FurnaceRecipeDisplay furnaceRecipeDisplay, ContextMap contextMap) {
        List<ItemStack> fuels = furnaceRecipeDisplay.fuel().resolveForStacks(contextMap);
        for (ItemStack fuel : fuels) {
            if (fuel.is(ItemRegistry.ENENG_STONE.get())
                    || fuel.is(ItemRegistry.STRENGTHENING_STONE.get())
                    || fuel.is(ItemRegistry.ETERNAL_STONE.get())) {
                return true;
            }
        }
        return false;
    }
}
