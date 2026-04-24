package org.hhoa.mc.intensify.registry;

import java.lang.reflect.Field;
import java.util.HashMap;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.DefaultEnhancementIntensifySystem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RecipeRegistryTest {
    @BeforeClass
    public static void setupMinecraft() throws Exception {
        if (!Bootstrap.isRegistered()) {
            Bootstrap.register();
        }
        configureMinimalTestIntensifyConfig();
    }

    @Test
    public void strengtheningStoneDoesNotStartOnUnenabledEquipment() {
        TileEntityFurnace furnace =
                furnaceWith(new ItemStack(new TestEquipmentItem()), ItemRegistry.STRENGTHENING_STONE);

        ItemStack result = RecipeRegistry.getSmeltingResult(furnace, ItemStack.EMPTY, false);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void strengtheningStoneStartsOnEnabledEquipment() {
        ItemStack equipment = new ItemStack(new TestEquipmentItem());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(IntensifyConstants.ENENGED_TAG_ID, true);
        equipment.setTagCompound(tag);
        TileEntityFurnace furnace = furnaceWith(equipment, ItemRegistry.STRENGTHENING_STONE);

        ItemStack result = RecipeRegistry.getSmeltingResult(furnace, ItemStack.EMPTY, false);

        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void strengtheningStoneFinalizesEnabledEquipment() {
        ItemStack equipment = new ItemStack(new TestEquipmentItem());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(IntensifyConstants.ENENGED_TAG_ID, true);
        equipment.setTagCompound(tag);
        TileEntityFurnace furnace = furnaceWith(equipment, ItemRegistry.STRENGTHENING_STONE);

        ItemStack result = RecipeRegistry.getSmeltingResult(furnace, ItemStack.EMPTY, true);

        Assert.assertEquals(1, result.getTagCompound().getInteger("intensify.intensify.tag.level"));
    }

    @Test
    public void enengStoneStartsOnUnenabledEquipment() {
        TileEntityFurnace furnace =
                furnaceWith(new ItemStack(new TestEquipmentItem()), ItemRegistry.ENENG_STONE);

        ItemStack result = RecipeRegistry.getSmeltingResult(furnace, ItemStack.EMPTY, false);

        Assert.assertFalse(result.isEmpty());
    }

    private static TileEntityFurnace furnaceWith(ItemStack input, net.minecraft.item.Item catalyst) {
        TileEntityFurnace furnace = new TileEntityFurnace();
        furnace.setInventorySlotContents(0, input);
        furnace.setInventorySlotContents(1, new ItemStack(catalyst));
        return furnace;
    }

    private static void configureMinimalTestIntensifyConfig() throws Exception {
        ToolIntensifyConfig config = new ToolIntensifyConfig();
        config.setEnable(true);

        HashMap<Class<? extends Item>, ToolIntensifyConfig> toolConfigs = new HashMap<>();
        toolConfigs.put(TestEquipmentItem.class, config);

        setStaticField("defaultEnengIntensifySystem", new DefaultEnengIntensifySystem());
        setStaticField(
                "defaultEnhancementIntensifySystem",
                new DefaultEnhancementIntensifySystem(1, 0, 1, 4, 10));
        setStaticField("classToolIntensifyConfigHashMap", toolConfigs);
        setStaticField("armorClassConfigMap", new HashMap<EntityEquipmentSlot, ToolIntensifyConfig>());
    }

    private static void setStaticField(String name, Object value) throws Exception {
        Field field = IntensifyConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static class TestEquipmentItem extends Item {}
}
