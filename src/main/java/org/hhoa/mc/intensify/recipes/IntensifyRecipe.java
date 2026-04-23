package org.hhoa.mc.intensify.recipes;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.item.IntensifyStone;

public abstract class IntensifyRecipe {
    private final ResourceLocation id;

    protected IntensifyRecipe(String path) {
        this.id = Intensify.location(path);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public boolean matchesStart(TileEntityFurnace furnace) {
        ItemStack tool = furnace.getStackInSlot(0);
        ItemStack catalyst = furnace.getStackInSlot(1);
        ToolIntensifyConfig config = IntensifyConfig.getToolIntensifyConfig(tool.getItem());
        if (config == null || catalyst.isEmpty() || !(catalyst.getItem() instanceof IntensifyStone)) {
            return false;
        }
        return this.matchesInput(tool, config) && this.matchesCatalyst(catalyst, tool);
    }

    public boolean matchesActive(TileEntityFurnace furnace) {
        ItemStack tool = furnace.getStackInSlot(0);
        ToolIntensifyConfig config = IntensifyConfig.getToolIntensifyConfig(tool.getItem());
        return config != null && this.matchesInput(tool, config);
    }

    public ItemStack getPreviewResult(TileEntityFurnace furnace) {
        if (!this.matchesActive(furnace)) {
            return ItemStack.EMPTY;
        }
        return furnace.getStackInSlot(0).copy();
    }

    public ItemStack assemble(TileEntityFurnace furnace) {
        if (!this.matchesActive(furnace)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = furnace.getStackInSlot(0).copy();
        ToolIntensifyConfig config = IntensifyConfig.getToolIntensifyConfig(result.getItem());
        this.intensify(result, config, this.resolvePlayer(furnace));
        return result;
    }

    protected abstract boolean matchesInput(ItemStack tool, ToolIntensifyConfig toolConfig);

    protected abstract boolean matchesCatalyst(ItemStack catalyst, ItemStack tool);

    protected abstract void intensify(
            ItemStack tool, ToolIntensifyConfig toolConfig, EntityPlayerMP player);

    protected static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private EntityPlayerMP resolvePlayer(TileEntityFurnace furnace) {
        NBTTagCompound data = furnace.getTileData();
        String playerName = data.getString(IntensifyConstants.FURNACE_OWNER_TAG_ID);
        if (playerName.isEmpty()
                || furnace.getWorld() == null
                || furnace.getWorld().getMinecraftServer() == null) {
            return null;
        }
        return furnace.getWorld().getMinecraftServer().getPlayerList().getPlayerByUsername(playerName);
    }
}
