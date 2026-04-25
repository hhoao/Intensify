package org.hhoa.mc.intensify.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import org.hhoa.mc.intensify.api.ComplexRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceTileEntityMixin {
    @Redirect(
            method = "canSmelt(Lnet/minecraft/item/crafting/IRecipe;)Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/item/crafting/IRecipe;getRecipeOutput()Lnet/minecraft/item/ItemStack;",
                            ordinal = 0),
            require = 1)
    public ItemStack intensify_canSmelt(IRecipe<?> recipe) {
        if (recipe instanceof ComplexRecipe) {
            return ((ComplexRecipe) recipe)
                    .getRecipeOutput((AbstractFurnaceTileEntity) (Object) this);
        }
        return recipe.getRecipeOutput();
    }

    @Redirect(
            method = "smelt(Lnet/minecraft/item/crafting/IRecipe;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/item/crafting/IRecipe;getRecipeOutput()Lnet/minecraft/item/ItemStack;",
                            ordinal = 0),
            require = 1)
    public ItemStack intensify_smelt(IRecipe<?> recipe) {
        if (recipe instanceof ComplexRecipe) {
            return ((ComplexRecipe) recipe)
                    .getRecipeOutput((AbstractFurnaceTileEntity) (Object) this);
        }
        return recipe.getRecipeOutput();
    }
}
