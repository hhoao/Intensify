package org.hhoa.mc.intensify.util;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

    public static void announceStrengthening(
            ServerPlayerEntity player, ItemStack itemStack, int level) {
        if (player == null || !shouldAnnounceStrengthening(level)) {
            return;
        }
        broadcast(
                player,
                new TranslationTextComponent(
                        TranslatableTexts.ANNOUNCEMENT_STRENGTHENING.key(),
                        player.getDisplayName(),
                        itemStack.getDisplayName(),
                        level));
    }

    public static void announceMiningDrop(LootContext lootContext, ItemStack drop) {
        if (!shouldAnnounceMiningDrop(drop) || !lootContext.has(LootParameters.THIS_ENTITY)) {
            return;
        }
        Entity entity = lootContext.get(LootParameters.THIS_ENTITY);
        if (!(entity instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        broadcast(
                player,
                new TranslationTextComponent(
                        TranslatableTexts.ANNOUNCEMENT_ETERNAL_STONE_DROP.key(),
                        player.getDisplayName(),
                        drop.getDisplayName()));
    }

    static boolean containsStrengtheningLevel(List<? extends Integer> levels, int level) {
        return levels.contains(level);
    }

    private static boolean announcementsEnabled() {
        return ConfigRegistry.WORLD_ANNOUNCEMENTS_ENABLED.get();
    }

    private static void broadcast(ServerPlayerEntity source, ITextComponent message) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
            player.sendMessage(message, source.getUniqueID());
        }
    }
}
