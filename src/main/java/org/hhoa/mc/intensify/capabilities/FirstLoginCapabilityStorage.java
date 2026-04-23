package org.hhoa.mc.intensify.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.hhoa.mc.intensify.Intensify;

public class FirstLoginCapabilityStorage
        implements Capability.IStorage<IFirstLoginCapability> {
    @Override
    public NBTBase writeNBT(
            Capability<IFirstLoginCapability> capability,
            IFirstLoginCapability instance,
            EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(Intensify.locationStr("has_logged_in"), instance.hasLoggedIn());
        return tag;
    }

    @Override
    public void readNBT(
            Capability<IFirstLoginCapability> capability,
            IFirstLoginCapability instance,
            EnumFacing side,
            NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setHasLoggedIn(tag.getBoolean(Intensify.locationStr("has_logged_in")));
        }
    }
}
