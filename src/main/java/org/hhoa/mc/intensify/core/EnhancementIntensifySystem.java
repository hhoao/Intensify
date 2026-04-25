package org.hhoa.mc.intensify.core;

import net.minecraft.world.item.ItemStack;

public abstract class EnhancementIntensifySystem implements IntensifySystem {

    public abstract int getLevel(ItemStack itemStack);
}
