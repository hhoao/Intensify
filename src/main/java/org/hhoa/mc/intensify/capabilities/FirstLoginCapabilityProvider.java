package org.hhoa.mc.intensify.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.hhoa.mc.intensify.Intensify;

public class FirstLoginCapabilityProvider
        implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
    private final IFirstLoginCapability instance = new FirstLoginCapabilityImpl();
    private final LazyOptional<IFirstLoginCapability> optional = LazyOptional.of(() -> instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == Intensify.FIRST_LOGIN_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(Intensify.locationStr("has_logged_in"), instance.hasLoggedIn());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        instance.setHasLoggedIn(nbt.getBoolean(Intensify.locationStr("has_logged_in")));
    }
}
