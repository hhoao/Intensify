package org.hhoa.mc.intensify.enums;

public enum DropTypeEnum {
    FISHING("fishing"),
    MOB_KILLED("mob_killed"),
    MINERAL_BLOCK_DESTROYED("mineral_block_destroyed");

    private final String identify;

    DropTypeEnum(String identify) {
        this.identify = identify;
    }

    public String getIdentify() {
        return identify;
    }
}
