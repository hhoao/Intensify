package org.hhoa.mc.intensify.util;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class WorldAnnouncements {
    public static boolean shouldAnnounceStrengthening(int level) {
        return announcementsEnabled()
                && ConfigRegistry.STRENGTHENING_ANNOUNCEMENT_LEVELS.get().contains(level);
    }

    public static boolean shouldAnnounceMiningDrop(ItemStack drop) {
        return announcementsEnabled()
                && !drop.isEmpty()
                && drop.getItem() == ItemRegistry.ETERNAL_STONE.get();
    }

    public static void announceStrengthening(ServerPlayer player, ItemStack itemStack, int level) {
        if (player == null || !shouldAnnounceStrengthening(level)) {
            return;
        }
        broadcast(
                player,
                Component.translatable(
                        TranslatableTexts.ANNOUNCEMENT_STRENGTHENING.key(),
                        player.getDisplayName(),
                        itemStack.getHoverName(),
                        level));
    }

    public static void announceMiningDrop(LootContext lootContext, ItemStack drop) {
        if (!shouldAnnounceMiningDrop(drop)
                || !lootContext.hasParam(LootContextParams.THIS_ENTITY)) {
            return;
        }
        Entity entity = lootContext.getParam(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) entity;
        broadcast(
                player,
                Component.translatable(
                        TranslatableTexts.ANNOUNCEMENT_ETERNAL_STONE_DROP.key(),
                        player.getDisplayName(),
                        drop.getHoverName()));
    }

    static boolean containsStrengtheningLevel(List<? extends Integer> levels, int level) {
        return levels.contains(level);
    }

    private static boolean announcementsEnabled() {
        return ConfigRegistry.WORLD_ANNOUNCEMENTS_ENABLED.get();
    }

    private static void broadcast(ServerPlayer source, Component message) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }
}
