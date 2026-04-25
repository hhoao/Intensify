package org.hhoa.mc.intensify.capabilities;

public class FirstLoginCapabilityImpl implements IFirstLoginCapability {
    private boolean hasLoggedIn = false;

    @Override
    public boolean hasLoggedIn() {
        return hasLoggedIn;
    }

    @Override
    public void setHasLoggedIn(boolean value) {
        this.hasLoggedIn = value;
    }
}
