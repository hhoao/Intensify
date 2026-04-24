package org.hhoa.mc.intensify;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorldAnnouncementsTest {
    @BeforeClass
    public static void setupMinecraft() {
        if (!Bootstrap.isRegistered()) {
            Bootstrap.register();
        }
    }

    @Test
    public void announcesConfiguredHighStrengtheningLevels() {
        Assert.assertFalse(WorldAnnouncements.shouldAnnounceStrengthening(9));
        Assert.assertTrue(WorldAnnouncements.shouldAnnounceStrengthening(10));
        Assert.assertFalse(WorldAnnouncements.shouldAnnounceStrengthening(11));
        Assert.assertTrue(WorldAnnouncements.shouldAnnounceStrengthening(15));
        Assert.assertTrue(WorldAnnouncements.shouldAnnounceStrengthening(20));
        Assert.assertTrue(WorldAnnouncements.shouldAnnounceStrengthening(25));
    }

    @Test
    public void announcesOnlyEternalStoneMiningDrops() {
        Assert.assertFalse(
                WorldAnnouncements.shouldAnnounceMiningDrop(
                        new ItemStack(ItemRegistry.STRENGTHENING_STONE)));
        Assert.assertFalse(
                WorldAnnouncements.shouldAnnounceMiningDrop(
                        new ItemStack(ItemRegistry.PROTECTION_STONE)));
        Assert.assertFalse(
                WorldAnnouncements.shouldAnnounceMiningDrop(new ItemStack(ItemRegistry.ENENG_STONE)));
        Assert.assertTrue(
                WorldAnnouncements.shouldAnnounceMiningDrop(
                        new ItemStack(ItemRegistry.ETERNAL_STONE)));
    }
}
