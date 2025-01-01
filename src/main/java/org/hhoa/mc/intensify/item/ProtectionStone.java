package org.hhoa.mc.intensify.item;

public class ProtectionStone extends IntensifyStone{
    public ProtectionStone(Properties properties) {
        super(properties);
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.PROTECTION_STONE;
    }
}
