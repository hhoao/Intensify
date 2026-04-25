package org.hhoa.mc.intensify.util;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceHelper {
    public static int getCookingProgress(AbstractFurnaceBlockEntity furnace) {
        return furnace.cookingProgress;
    }

    public static void setCookingProgress(AbstractFurnaceBlockEntity furnace, int cookingProgress) {
        furnace.cookingProgress = cookingProgress;
    }

    public static int getLitTime(AbstractFurnaceBlockEntity furnace) {
        return furnace.litTime;
    }

    public static void setLitTime(AbstractFurnaceBlockEntity furnace, int litTime) {
        furnace.litTime = litTime;
    }

    public static int getLitDuration(AbstractFurnaceBlockEntity furnace) {
        return furnace.litDuration;
    }

    public static void setLitDuration(AbstractFurnaceBlockEntity furnace, int litDuration) {
        furnace.litDuration = litDuration;
    }

    public static int getCookingTotalTime(AbstractFurnaceBlockEntity furnace) {
        return furnace.cookingTotalTime;
    }

    public static void setCookingTotalTime(
            AbstractFurnaceBlockEntity furnace, int cookingTotalTime) {
        furnace.cookingTotalTime = cookingTotalTime;
    }
}
