package org.hhoa.mc.intensify.util;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Player
 *
 * @author xianxing
 * @since 2024/11/2
 */
public class PlayerUtils {
    public static boolean hasItem(Player player, Item item) {
        Inventory inventory = player.getInventory();
        return inventory.contains(new ItemStack(item));
    }

    public static boolean hasEmpty(Player player) {
        Inventory inventory = player.getInventory();
        return inventory.getFreeSlot() >= 0;
    }

    public static void consumeItem(Player player, Item item, int count) {
        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();

        for (int i = 0; i < size; i++) {
            ItemStack is = inventory.getItem(i);
            if (is.getItem() == item) {
                is.shrink(count);
                break;
            }
        }
    }

    public static boolean fireItemToPlayer(ItemStack itemStack, Player player) {
        ItemEntity itemEntity =
                new ItemEntity(
                        player.level(), player.getX(), player.getY(), player.getZ(), itemStack);

        return player.level().addFreshEntity(itemEntity);
    }

    public static void removeSingleItemFromPlayer(Player player, ItemStack itemStack) {
        int count = itemStack.getCount();
        Item item = itemStack.getItem();
        removeSingleItemFromPlayer(player, item, count);
    }

    public static void removeSingleItemFromPlayer(Player player, Item item, int count) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                int count1 = stack.getCount();
                if (count1 > count) {
                    stack.shrink(count);
                    break;
                } else {
                    count -= count1;
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                if (count == 0) {
                    break;
                }
            }
        }
    }

    public static boolean hasItemCount(Player player, ItemStack itemStack) {
        return hasItemCount(player, itemStack.getItem(), itemStack.getCount());
    }

    public static boolean hasItemCount(Player player, Item targetItem, int requiredCount) {
        int totalCount = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == targetItem) {
                totalCount += stack.getCount();

                if (totalCount >= requiredCount) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void addItemToPlayer(ItemStack itemStack, Player player) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }
}
