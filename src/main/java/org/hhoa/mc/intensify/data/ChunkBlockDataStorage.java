package org.hhoa.mc.intensify.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ChunkBlockDataStorage extends WorldSavedData {
    private Map<Long, Boolean> chunkDataMap = new HashMap<>();

    public static String getChunkBlockDataName(ChunkPos chunkPos) {
        return "placed_" + ChunkPos.asLong(chunkPos.x, chunkPos.z);
    }

    public ChunkBlockDataStorage(String name) {
        super(name);
    }

    public ChunkBlockDataStorage(ChunkPos chunkPos) {
        this(getChunkBlockDataName(chunkPos));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for (Map.Entry<Long, Boolean> positionMap : this.chunkDataMap.entrySet()) {
            compound.setBoolean(positionMap.getKey().toString(), positionMap.getValue());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.chunkDataMap = new HashMap<>();
        Set<String> keys = tag.getKeySet();
        for (String key : keys) {
            this.chunkDataMap.put(Long.valueOf(key), tag.getBoolean(key));
        }
    }

    public boolean getBlockData(BlockPos blockPos) {
        return this.chunkDataMap.getOrDefault(blockPos.toLong(), false);
    }

    public void setBlockData(BlockPos blockPos, boolean replaced) {
        this.chunkDataMap.put(blockPos.toLong(), replaced);
        this.markDirty();
    }

    public static ChunkBlockDataStorage getOrCreate(World world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        String dataName = getChunkBlockDataName(chunkPos);
        MapStorage storage = world.getPerWorldStorage();
        ChunkBlockDataStorage data =
                (ChunkBlockDataStorage) storage.getOrLoadData(ChunkBlockDataStorage.class, dataName);
        if (data == null) {
            data = new ChunkBlockDataStorage(dataName);
            storage.setData(dataName, data);
        }
        return data;
    }
}
