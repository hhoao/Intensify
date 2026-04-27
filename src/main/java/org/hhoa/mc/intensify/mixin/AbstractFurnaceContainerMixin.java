package org.hhoa.mc.intensify.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceContainer.class)
public abstract class AbstractFurnaceContainerMixin {
    @Inject(method = "func_217056_a", at = @At("HEAD"), cancellable = true)
    private void intensify$handleFuelOnlyPlacement(
            boolean placeAll, IRecipe<?> recipe, ServerPlayerEntity player, CallbackInfo ci) {
        if (!(recipe instanceof FurnaceRecipeBookDisplayRecipe)) {
            return;
        }

        FurnaceRecipeBookDisplayRecipe displayRecipe = (FurnaceRecipeBookDisplayRecipe) recipe;
        if (!displayRecipe.isFuelOnlyPlacement()) {
            return;
        }

        AbstractFurnaceContainer menu = (AbstractFurnaceContainer) (Object) this;
        Slot fuelSlot = menu.getSlot(1);
        ItemStack placeableFuel = displayRecipe.getPlaceableFuel();
        placeableFuel.setCount(1);
        if (placeableFuel.isEmpty() || !fuelSlot.isItemValid(placeableFuel)) {
            sendGhostRecipe(player, recipe);
            ci.cancel();
            return;
        }

        ItemStack existingFuel = fuelSlot.getStack();
        if (!existingFuel.isEmpty()
                && (!ItemStack.areItemsEqual(existingFuel, placeableFuel)
                        || !ItemStack.areItemStackTagsEqual(existingFuel, placeableFuel)
                        || existingFuel.getCount() >= fuelSlot.getItemStackLimit(placeableFuel))) {
            sendGhostRecipe(player, recipe);
            ci.cancel();
            return;
        }

        PlayerInventory inventory = player.inventory;
        ItemStack insertedFuel = placeableFuel;
        if (!player.isCreative()) {
            int inventorySlot = inventory.findSlotMatchingUnusedItem(placeableFuel);
            if (inventorySlot < 0) {
                sendGhostRecipe(player, recipe);
                ci.cancel();
                return;
            }

            insertedFuel = inventory.decrStackSize(inventorySlot, 1);
            if (insertedFuel.isEmpty()) {
                sendGhostRecipe(player, recipe);
                ci.cancel();
                return;
            }
            inventory.markDirty();
        }

        if (existingFuel.isEmpty()) {
            fuelSlot.putStack(insertedFuel);
        } else {
            existingFuel.grow(insertedFuel.getCount());
            fuelSlot.putStack(existingFuel);
        }

        ci.cancel();
    }

    private static void sendGhostRecipe(ServerPlayerEntity player, IRecipe<?> recipe) {
        player.connection.sendPacket(new SPlaceGhostRecipePacket(player.openContainer.windowId, recipe));
    }
}
