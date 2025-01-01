package org.hhoa.mc.intensify.item;

public class EternalStone extends IntensifyStone{
    public EternalStone(Properties properties) {
        super(properties);
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.ETERNAL_STONE;
    }
}
