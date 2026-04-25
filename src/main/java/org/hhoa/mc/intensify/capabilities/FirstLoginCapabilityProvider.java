package org.hhoa.mc.intensify.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.hhoa.mc.intensify.Intensify;

public class FirstLoginCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
    private final IFirstLoginCapability instance = new FirstLoginCapabilityImpl();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == Intensify.FIRST_LOGIN_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == Intensify.FIRST_LOGIN_CAPABILITY
                ? Intensify.FIRST_LOGIN_CAPABILITY.cast(this.instance)
                : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound)
                Intensify.FIRST_LOGIN_CAPABILITY
                        .getStorage()
                        .writeNBT(Intensify.FIRST_LOGIN_CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        Intensify.FIRST_LOGIN_CAPABILITY
                .getStorage()
                .readNBT(Intensify.FIRST_LOGIN_CAPABILITY, this.instance, null, nbt);
    }
}
