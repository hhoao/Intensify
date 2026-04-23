package org.hhoa.mc.intensify.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import org.hhoa.mc.intensify.registry.RecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityFurnace.class)
public abstract class AbstractFurnaceTileEntityMixin {
    @Redirect(
            method = "canSmelt()Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/item/crafting/FurnaceRecipes;getSmeltingResult(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"),
            require = 1)
    private ItemStack intensify_canSmelt(FurnaceRecipes recipes, ItemStack input) {
        TileEntityFurnace furnace = (TileEntityFurnace) (Object) this;
        return RecipeRegistry.getSmeltingResult(furnace, recipes.getSmeltingResult(input), false);
    }

    @Redirect(
            method = "smeltItem()V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/item/crafting/FurnaceRecipes;getSmeltingResult(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"),
            require = 1)
    private ItemStack intensify_smelt(FurnaceRecipes recipes, ItemStack input) {
        TileEntityFurnace furnace = (TileEntityFurnace) (Object) this;
        return RecipeRegistry.getSmeltingResult(furnace, recipes.getSmeltingResult(input), true);
    }
}
