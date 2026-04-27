package org.hhoa.mc.intensify.recipes;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.hhoa.mc.intensify.api.ComplexRecipe;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.item.IntensifyStone;
import org.hhoa.mc.intensify.util.FurnaceHelper;

public abstract class IntensifyRecipe extends FurnaceRecipe implements ComplexRecipe {
    private IntensifyRecipe(
            ResourceLocation resourceLocation,
            String group,
            Ingredient ingredient,
            ItemStack itemStack,
            float experience,
            int cookingTime) {
        super(resourceLocation, group, ingredient, itemStack, experience, cookingTime);
    }

    @Override
    public ItemStack getCraftingResult(IInventory p_77572_1_) {
        return new ItemStack(Items.FURNACE);
    }

    public IntensifyRecipe(ResourceLocation resourceLocation, float experience, int cookingTime) {
        this(
                resourceLocation,
                "",
                Ingredient.EMPTY,
                new ItemStack(Items.AIR),
                experience,
                cookingTime);
    }

    public abstract boolean matchesInternal(IInventory container, World level);

    // 装备放入时也会进行一次 matches, 会根据matches的结果获取Recipe，然后获取其cookingTime, 默认如果没有matches，则cookingTime为200
    // 那我们如何在放入装备时就匹配成功然后获取相应的cookingTime呢?
    // 方案1：我们把所有Recipe的cookingTotalTime设置为200
    // 方案2: 判断如果有装备，并且燃料为空，并且燃烧时间为0， 则匹配成功（那这都不需要燃料都可以强化了？）
    // 有个bug，启能完后，将启能的武器再次放到需要煅烧的地方，然后将启能石头替换成强化石，导致cookingTotalTime为默认值200
    // 原因是启能完后，将启能的装备再次放到需要煅烧的地方，那么这个时候Recipe的判断失败，则cookingTotalTime为默认的200
    // 然后将启能石头换为强化石只是替换了燃料，不会尝试改变cookingTotalTime, 这样的话第二个方案失败了,只能使用第一个方案了

    // 我们需要判断的是在强化石没有后，怎么判断是否还可以继续强化当前装备
    // 强化石开始燃烧，这时其实替换什么燃料都无所谓了，强化石的燃烧时间迟早会到0

    // 我们可以设置一个lastRecipe
    // 因为强化石的燃烧时间迟早会到0, 到0时就可以根据是否有强化石并且可强化来设置lastRecipe, 可强化则设置，不可强化则不设置
    // lastRecipe设置完后，强化中，如果没有强化石，则可以根据lastRecipe来判断是否可以强化，符合lastRecipe则可以继续强化

    // matches 时判断当前燃烧时间，
    // 强化装备时
    // 如果燃烧时间为0，有强化石，那么返回true，并且设置lastRecipe为当前recipe
    // 如果燃烧时间为0，没有强化石, 那么返回false，并且设置lastRecipe为当前recipe
    // 如果燃烧时间不为0，有强化石， lastRecipe符合, 返回true
    // 如果燃烧时间不为0，有强化石， lastRecipe不符合, 返回false
    // 如果燃烧时间不为0，没有强化石，那么判断lastRecipe参数是否为当前Recipe，是的话就返回true
    // 如果燃烧时间不为0，没有强化石，那么判断lastRecipe参数是否为当前Recipe，不是的话就返回false

    // 怎么判断燃烧时间呢？
    // litTime==0

    @Override
    public boolean matches(IInventory container, World level) {
        ItemStack tool = container.getStackInSlot(0);
        Item toolItem = tool.getItem();
        ToolIntensifyConfig toolItemIntensifyConfig =
                IntensifyConfig.getToolIntensifyConfig(toolItem);
        if (toolItemIntensifyConfig == null) {
            return false;
        }

        AbstractFurnaceTileEntity furnaceBlockEntity = (AbstractFurnaceTileEntity) container;
        int litTime = FurnaceHelper.getLitTime(furnaceBlockEntity);
        if (litTime <= 0) {
            // 开始准备熔炼
            if (!(container instanceof FurnaceTileEntity)
                    || !(container.getStackInSlot(1).getItem() instanceof IntensifyStone)) {
                return false;
            }

            boolean matches = matchesInternal(container, level);
            CompoundNBT persistentData = furnaceBlockEntity.getTileData();
            if (matches) {
                persistentData.putString(
                        IntensifyConstants.LAST_RECIPE_TAG_ID, this.getId().toString());
            } else {
                persistentData.remove(IntensifyConstants.LAST_RECIPE_TAG_ID);
            }

            return matches;
        } else {
            CompoundNBT persistentData = furnaceBlockEntity.getTileData();
            String lastRecipeTagId =
                    persistentData.getString(IntensifyConstants.LAST_RECIPE_TAG_ID);
            return this.getId().toString().equals(lastRecipeTagId);
        }
    }

    public abstract void intensify(
            ItemStack tool, ToolIntensifyConfig toolItemIntensifyConfig, ServerPlayerEntity player);

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(Items.AIR);
    }

    public ItemStack getRecipeOutput(IInventory container) {
        ItemStack item = container.getStackInSlot(0);
        FurnaceTileEntity furnaceBlock = (FurnaceTileEntity) container;
        boolean burningEnd = FurnaceHelper.isBurningEnd(furnaceBlock);
        if (burningEnd) {
            ToolIntensifyConfig toolItemIntensifyConfig =
                    IntensifyConfig.getToolIntensifyConfig(item.getItem());
            String playerName =
                    furnaceBlock.getTileData().getString(IntensifyConstants.FURNACE_OWNER_TAG_ID);
            ServerPlayerEntity player =
                    ServerLifecycleHooks.getCurrentServer()
                            .getPlayerList()
                            .getPlayerByUsername(playerName);
            ItemStack copy = item.copy();
            intensify(copy, toolItemIntensifyConfig, player);
            return copy;
        }
        return item;
    }

    public abstract IntensifyRecipeSerializer<?> getSerializerInternal();

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return getSerializerInternal();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
