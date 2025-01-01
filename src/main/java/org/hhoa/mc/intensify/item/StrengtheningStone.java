package org.hhoa.mc.intensify.item;

public class StrengtheningStone extends IntensifyStone {
    public StrengtheningStone(Properties properties) {
        super(properties);
    }

    @Override
    public IntensifyStoneType getIdentifier() {
        return IntensifyStoneType.STRENGTHENING_STONE;
    }
}
