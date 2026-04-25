package org.hhoa.mc.intensify.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Player
 *
 * @author xianxing
 * @since 2024/11/2
 */
public class PlayerUtils {
    public static boolean fireItemToPlayer(ItemStack itemStack, PlayerEntity player) {
        ItemEntity itemEntity =
                new ItemEntity(
                        player.world,
                        player.getPosX(),
                        player.getPosY(),
                        player.getPosZ(),
                        itemStack);

        return player.world.addEntity(itemEntity);
    }

    public static void removeSingleItemFromPlayer(PlayerEntity player, ItemStack itemStack) {
        int count = itemStack.getCount();
        Item item = itemStack.getItem();
        removeSingleItemFromPlayer(player, item, count);
    }

    public static void removeSingleItemFromPlayer(PlayerEntity player, Item item, int count) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                int count1 = stack.getCount();
                if (count1 > count) {
                    stack.shrink(count);
                    break;
                } else {
                    count -= count1;
                    player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
                if (count == 0) {
                    break;
                }
            }
        }
    }

    public static boolean hasItemCount(PlayerEntity player, ItemStack itemStack) {
        return hasItemCount(player, itemStack.getItem(), itemStack.getCount());
    }

    public static boolean hasItemCount(PlayerEntity player, Item targetItem, int requiredCount) {
        int totalCount = 0;

        for (ItemStack stack : player.container.getInventory()) {
            if (stack.getItem() == targetItem) {
                totalCount += stack.getCount();

                if (totalCount >= requiredCount) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void addItemToPlayer(ItemStack itemStack, PlayerEntity player) {
        if (!player.addItemStackToInventory(itemStack)) {
            player.dropItem(itemStack, false);
        }
    }
}
