package org.hhoa.mc.intensify.registry;

import java.util.Arrays;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.junit.Assert;
import org.junit.Test;

public class ClientModelRegistryTest {
    @Test
    public void stoneModelIdsIncludeEveryRegisteredStone() {
        Assert.assertEquals(
                Arrays.asList(
                        IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
                        IntensifyStoneType.ENENG_STONE.getIdentifier(),
                        IntensifyStoneType.PROTECTION_STONE.getIdentifier(),
                        IntensifyStoneType.ETERNAL_STONE.getIdentifier()),
                ClientModelRegistry.getStoneModelIds());
    }
}
