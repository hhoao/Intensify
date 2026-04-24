package org.hhoa.mc.intensify.config;

import net.minecraft.item.Item;
import org.junit.Assert;
import org.junit.Test;

public class IntensifyConfigTest {

    @Test
    public void getToolIntensifyConfigReturnsNullBeforeInitialize() {
        Assert.assertNull(IntensifyConfig.getToolIntensifyConfig(new Item()));
    }
}
