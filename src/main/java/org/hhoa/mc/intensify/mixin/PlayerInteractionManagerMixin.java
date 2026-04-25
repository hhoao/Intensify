package org.hhoa.mc.intensify.mixin;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.hhoa.mc.intensify.data.ChunkBlockDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {
    @Shadow public ServerWorld world;

    @Redirect(
            method = "tryHarvestBlock(Lnet/minecraft/util/math/BlockPos;)Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/item/ItemStack;)V",
                            ordinal = 0))
    private void apoth_cachePredicate(
            Block block,
            World worldIn,
            PlayerEntity player,
            BlockPos pos,
            BlockState state,
            @Nullable TileEntity te,
            ItemStack stack) {
        block.harvestBlock(worldIn, player, pos, state, te, stack);

        ChunkBlockDataStorage orCreate = ChunkBlockDataStorage.getOrCreate(world, pos);

        if (!orCreate.getBlockData(pos)) return;

        orCreate.setBlockData(pos, false);
    }
}
