package org.hhoa.mc.intensify.mixin;

import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookMenu.class)
public abstract class AbstractFurnaceMenuMixin {
    @Inject(method = "handlePlacement", at = @At("HEAD"), cancellable = true)
    private void intensify$handleFuelOnlyPlacement(
            boolean placeAll, Recipe<?> recipe, ServerPlayer player, CallbackInfo ci) {
        if (!(recipe instanceof FurnaceRecipeBookDisplayRecipe displayRecipe)) {
            return;
        }
        if (!displayRecipe.isFuelOnlyPlacement()) {
            return;
        }

        if (!((Object) this instanceof AbstractFurnaceMenu menu)) {
            return;
        }
        Slot fuelSlot = menu.getSlot(AbstractFurnaceMenu.FUEL_SLOT);
        ItemStack placeableFuel = displayRecipe.getPlaceableFuel().copy();
        if (placeableFuel.isEmpty() || !fuelSlot.mayPlace(placeableFuel)) {
            player.connection.send(new ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe));
            ci.cancel();
            return;
        }

        ItemStack existingFuel = fuelSlot.getItem();
        if (!existingFuel.isEmpty()
                && (!ItemStack.isSameItemSameTags(existingFuel, placeableFuel)
                        || existingFuel.getCount() >= fuelSlot.getMaxStackSize())) {
            player.connection.send(new ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe));
            ci.cancel();
            return;
        }

        Inventory inventory = player.getInventory();
        ItemStack insertedFuel = placeableFuel;
        if (!player.isCreative()) {
            int inventorySlot = inventory.findSlotMatchingUnusedItem(placeableFuel);
            if (inventorySlot < 0) {
                player.connection.send(new ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe));
                ci.cancel();
                return;
            }

            insertedFuel = inventory.removeItem(inventorySlot, 1);
            if (insertedFuel.isEmpty()) {
                player.connection.send(new ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe));
                ci.cancel();
                return;
            }
            inventory.setChanged();
        }

        if (existingFuel.isEmpty()) {
            fuelSlot.set(insertedFuel);
        } else {
            existingFuel.grow(insertedFuel.getCount());
            fuelSlot.set(existingFuel);
        }

        ci.cancel();
    }
}
