package org.hhoa.mc.intensify.core;

import net.minecraft.item.ItemStack;

public abstract class EnhancementIntensifySystem implements IntensifySystem {

    public abstract int getLevel(ItemStack itemStack);
}
