package org.hhoa.mc.intensify.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;

public class FurnaceHelper {
    public static int getCookingProgress(AbstractFurnaceTileEntity furnace) {
        return furnace.cookTime;
    }

    public static int getLitTime(AbstractFurnaceTileEntity furnace) {
        return furnace.burnTime;
    }

    public static int getCookingTotalTime(AbstractFurnaceTileEntity furnace) {
        return furnace.cookTimeTotal;
    }

    public static boolean isBurningEnd(FurnaceTileEntity furnaceBlockEntity) {
        int cookingProgress = FurnaceHelper.getCookingProgress(furnaceBlockEntity);
        int cookingTotalTime = FurnaceHelper.getCookingTotalTime(furnaceBlockEntity);
        CompoundNBT persistentData = furnaceBlockEntity.getTileData();
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
