package org.hhoa.mc.intensify.util;

import net.minecraft.tileentity.TileEntityFurnace;

public class FurnaceHelper {
    public static int getLitTime(TileEntityFurnace furnace) {
        return furnace.getField(0);
    }

    public static int getCookTime(TileEntityFurnace furnace) {
        return furnace.getField(2);
    }

    public static int getCookTimeTotal(TileEntityFurnace furnace) {
        return furnace.getField(3);
    }

    public static boolean isFinishing(TileEntityFurnace furnace) {
        return furnace.isBurning() && getCookTime(furnace) >= Math.max(0, getCookTimeTotal(furnace) - 1);
    }
}
