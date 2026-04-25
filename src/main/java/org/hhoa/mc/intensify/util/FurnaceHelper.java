package org.hhoa.mc.intensify.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;

public class FurnaceHelper {
    public static int getCookingProgress(AbstractFurnaceBlockEntity furnace) {
        return furnace.cookingProgress;
    }

    public static int getLitTime(AbstractFurnaceBlockEntity furnace) {
        return furnace.litTime;
    }

    public static int getCookingTotalTime(AbstractFurnaceBlockEntity furnace) {
        return furnace.cookingTotalTime;
    }

    public static boolean isBurningEnd(FurnaceBlockEntity furnaceBlockEntity) {
        int cookingProgress = FurnaceHelper.getCookingProgress(furnaceBlockEntity);
        int cookingTotalTime = FurnaceHelper.getCookingTotalTime(furnaceBlockEntity);
        CompoundTag persistentData = furnaceBlockEntity.getPersistentData();
        int phase = persistentData.getInt("phase");
        boolean burningEnd = false;
        if (cookingProgress == 0) {
            if (phase == 2) {
                phase = 3;
            } else if (phase == 3) {
                burningEnd = true;
                phase = 0;
            } else {
                phase = 1;
            }
        } else if (phase == 1) {
            if (cookingProgress + 1 == cookingTotalTime) {
                phase++;
            }
        }
        persistentData.putInt("phase", phase);
        return burningEnd;
    }
}
