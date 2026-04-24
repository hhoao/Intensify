package org.hhoa.mc.intensify;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public final class WorldAnnouncements {
    private WorldAnnouncements() {}

    public static boolean shouldAnnounceStrengthening(int level) {
        return ConfigRegistry.WORLD_ANNOUNCEMENTS_ENABLED.get()
                && ConfigRegistry.getStrengtheningAnnouncementLevels().contains(level);
    }

    public static boolean shouldAnnounceMiningDrop(ItemStack drop) {
        return ConfigRegistry.WORLD_ANNOUNCEMENTS_ENABLED.get()
                && !drop.isEmpty()
                && drop.getItem() == ItemRegistry.ETERNAL_STONE;
    }

    public static void announceStrengthening(
            EntityPlayerMP player, ItemStack itemStack, int level) {
        if (player == null || !shouldAnnounceStrengthening(level)) {
            return;
        }
        broadcast(
                player,
                TranslatableTexts.ANNOUNCEMENT_STRENGTHENING.component(
                        player.getDisplayName(), itemStack.getTextComponent(), level));
    }

    public static void announceMiningDrop(EntityPlayerMP player, ItemStack drop) {
        if (player == null || !shouldAnnounceMiningDrop(drop)) {
            return;
        }
        broadcast(
                player,
                TranslatableTexts.ANNOUNCEMENT_ETERNAL_STONE_DROP.component(
                        player.getDisplayName(), drop.getTextComponent()));
    }

    private static void broadcast(EntityPlayerMP player, ITextComponent message) {
        if (player.getServer() != null) {
            player.getServer().getPlayerList().sendMessage(message);
        }
    }
}
