package org.hhoa.mc.intensify.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;

import java.lang.reflect.Field;

public class FurnaceHelper {
    private static final Field COOKING_PROGRESS_FIELD;
    private static final Field COOKING_TOTAL_TIME_FIELD;

    static {
        try {
            COOKING_PROGRESS_FIELD = AbstractFurnaceBlockEntity.class.getDeclaredField("cookingProgress");
            COOKING_PROGRESS_FIELD.setAccessible(true);

            COOKING_TOTAL_TIME_FIELD = AbstractFurnaceBlockEntity.class.getDeclaredField("cookingTotalTime");
            COOKING_TOTAL_TIME_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access furnace fields", e);
        }
    }

    public static int getCookingProgress(AbstractFurnaceBlockEntity furnace) {
        try {
            return COOKING_PROGRESS_FIELD.getInt(furnace);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read cooking progress", e);
        }
    }

    public static int getCookingTotalTime(AbstractFurnaceBlockEntity furnace) {
        try {
            return COOKING_TOTAL_TIME_FIELD.getInt(furnace);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read cooking total time", e);
        }
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
        } else if (phase == 1){
            if (cookingProgress + 1 == cookingTotalTime) {
                phase++;
            }
        }
        persistentData.putInt("phase", phase);
        return burningEnd;
    }
}
