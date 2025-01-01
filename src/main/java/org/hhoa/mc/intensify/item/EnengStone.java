package org.hhoa.mc.intensify.item;

public class EnengStone extends IntensifyStone {
    public EnengStone(Properties properties) {
        super(properties);
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ENENG_STONE;
    }
}
