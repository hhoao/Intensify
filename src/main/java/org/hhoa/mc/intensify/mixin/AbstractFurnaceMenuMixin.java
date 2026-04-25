package org.hhoa.mc.intensify.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin {
    @Inject(method = "handlePlacement", at = @At("HEAD"), cancellable = true)
    private void intensify$handleFuelOnlyPlacement(
            boolean useMaxItems,
            boolean isCreative,
            RecipeHolder<?> recipe,
            ServerLevel level,
            Inventory playerInventory,
            CallbackInfoReturnable<RecipeBookMenu.PostPlaceAction> cir) {
        if (!(recipe.value() instanceof FurnaceRecipeBookDisplayRecipe displayRecipe)) {
            return;
        }
        if (!displayRecipe.isFuelOnlyPlacement()) {
            return;
        }

        AbstractFurnaceMenu menu = (AbstractFurnaceMenu) (Object) this;
        Slot fuelSlot = menu.getSlot(AbstractFurnaceMenu.FUEL_SLOT);
        ItemStack placeableFuel = displayRecipe.getPlaceableFuel().copyWithCount(1);
        if (placeableFuel.isEmpty() || !fuelSlot.mayPlace(placeableFuel)) {
            cir.setReturnValue(RecipeBookMenu.PostPlaceAction.NOTHING);
            return;
        }

        ItemStack existingFuel = fuelSlot.getItem();
        if (!existingFuel.isEmpty()
                && (!ItemStack.isSameItemSameComponents(existingFuel, placeableFuel)
                        || existingFuel.getCount() >= fuelSlot.getMaxStackSize(placeableFuel))) {
            cir.setReturnValue(RecipeBookMenu.PostPlaceAction.NOTHING);
            return;
        }

        ItemStack insertedFuel = placeableFuel;
        if (!isCreative) {
            int inventorySlot = playerInventory.findSlotMatchingItem(placeableFuel);
            if (inventorySlot < 0) {
                cir.setReturnValue(RecipeBookMenu.PostPlaceAction.NOTHING);
                return;
            }

            insertedFuel = playerInventory.removeItem(inventorySlot, 1);
            if (insertedFuel.isEmpty()) {
                cir.setReturnValue(RecipeBookMenu.PostPlaceAction.NOTHING);
                return;
            }
            playerInventory.setChanged();
        }

        if (existingFuel.isEmpty()) {
            fuelSlot.setByPlayer(insertedFuel);
        } else {
            existingFuel.grow(insertedFuel.getCount());
            fuelSlot.setByPlayer(existingFuel);
        }

        cir.setReturnValue(RecipeBookMenu.PostPlaceAction.NOTHING);
    }
}
