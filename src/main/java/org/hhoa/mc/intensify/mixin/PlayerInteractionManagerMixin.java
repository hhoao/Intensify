package org.hhoa.mc.intensify.mixin;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.hhoa.mc.intensify.data.ChunkBlockDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {
    @Redirect(
            method = "tryHarvestBlock(Lnet/minecraft/util/math/BlockPos;)Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/item/ItemStack;)V",
                            ordinal = 0))
    private void apoth_cachePredicate(
            Block block,
            World worldIn,
            EntityPlayer player,
            BlockPos pos,
            IBlockState state,
            @Nullable TileEntity te,
            ItemStack stack) {
        block.harvestBlock(worldIn, player, pos, state, te, stack);

        ChunkBlockDataStorage storage = ChunkBlockDataStorage.getOrCreate(worldIn, pos);
        if (storage.getBlockData(pos)) {
            storage.setBlockData(pos, false);
        }
    }
}
