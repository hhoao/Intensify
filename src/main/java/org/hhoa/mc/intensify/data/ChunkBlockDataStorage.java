package org.hhoa.mc.intensify.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class ChunkBlockDataStorage extends WorldSavedData {
    private Map<Long, Boolean> chunkDataMap = new HashMap<>();

    public static String getChunkBlockDataName(ChunkPos chunkPos) {
        return "placed_" + chunkPos.asLong();
    }

    public ChunkBlockDataStorage(ChunkPos chunkPos) {
        super(getChunkBlockDataName(chunkPos));
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundTag) {
        for (Map.Entry<Long, Boolean> positionMap : chunkDataMap.entrySet()) {
            Long key = positionMap.getKey();
            Boolean b = positionMap.getValue();
            compoundTag.putBoolean(key.toString(), b);
        }
        return compoundTag;
    }

    @Override
    public void read(CompoundNBT tag) {
        chunkDataMap = new HashMap<>();
        Set<String> keys = tag.keySet();

        for (String x : keys) {
            chunkDataMap.put(Long.valueOf(x), tag.getBoolean(x));
        }
    }

    public boolean getBlockData(BlockPos blockPos) {
        return chunkDataMap.getOrDefault(blockPos.toLong(), false);
    }

    public void setBlockData(BlockPos blockPos, boolean replaced) {
        chunkDataMap.put(blockPos.toLong(), replaced);
        setDirty(true);
    }

    public static ChunkBlockDataStorage getOrCreate(ServerWorld serverWorld, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        return serverWorld
                .getSavedData()
                .getOrCreate(
                        () -> new ChunkBlockDataStorage(chunkPos),
                        ChunkBlockDataStorage.getChunkBlockDataName(chunkPos));
    }
}
