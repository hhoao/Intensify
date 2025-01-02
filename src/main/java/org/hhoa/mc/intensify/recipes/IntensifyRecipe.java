package org.hhoa.mc.intensify.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.item.IntensifyStone;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.util.FurnaceHelper;

import java.util.HashMap;
import java.util.Map;


public abstract class IntensifyRecipe extends SmeltingRecipe {
    private IntensifyRecipe(
        ResourceLocation resourceLocation,
        String group,
        CookingBookCategory category,
        Ingredient ingredient,
        ItemStack itemStack,
        float experience,
        int cookingTime) {
        super(resourceLocation, group, category, ingredient, itemStack, experience, cookingTime);
    }

    public IntensifyRecipe(
        ResourceLocation resourceLocation,
        float experience,
        int cookingTime
    ) {
        this(resourceLocation,
            null,
            null,
            null,
            null,
            experience,
            cookingTime);
    }

    public abstract boolean matchesInternal(Container container,
                                    Level level);


    @Override
    public boolean matches(Container container, net.minecraft.world.level.Level level) {
        if (!(container instanceof FurnaceBlockEntity) ||
            !(container.getItem(1).getItem() instanceof IntensifyStone)) {
            return false;
        }

        return matchesInternal(container, level);
    }

    public abstract void intensify(ItemStack tool,
                                        RegistryAccess registryAccess,
                                        ToolIntensifyConfig toolItemIntensifyConfig,
                                        ServerPlayer player);

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack item = container.getItem(0);
        FurnaceBlockEntity furnaceBlock = (FurnaceBlockEntity) container;
        boolean burningEnd = FurnaceHelper.isBurningEnd(furnaceBlock);
        if (burningEnd) {
            ToolIntensifyConfig toolItemIntensifyConfig = ToolIntensifyConfig.getToolIntensifyConfig(item.getItem());
            String playerName = furnaceBlock.getPersistentData().getString(IntensifyConstants.FURNACE_OWNER_TAG_ID);
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(playerName);
            ItemStack copy = item.copy();
            intensify(copy, registryAccess, toolItemIntensifyConfig, player);
            return copy;
        }
        return item;
    }

    public abstract IntensifyRecipeSerializer<?> getSerializerInternal();


    @Override
    public RecipeSerializer<?> getSerializer() {
        return getSerializerInternal();
    }
}
