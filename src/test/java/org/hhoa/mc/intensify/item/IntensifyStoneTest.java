package org.hhoa.mc.intensify.item;

import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.junit.Assert;
import org.junit.Test;

public class IntensifyStoneTest {
    @Test
    public void strengtheningStoneProvidesForgeFurnaceBurnTime() {
        StrengtheningStone stone = new StrengtheningStone();

        int burnTime = stone.getItemBurnTime(null);

        Assert.assertEquals(IntensifyConfig.DEFAULT_INTENSIFY_STONE_BURN_TIME.intValue(), burnTime);
    }
}
