package org.hhoa.mc.intensify.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.EnengStone;
import org.hhoa.mc.intensify.item.EternalStone;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.item.ProtectionStone;
import org.hhoa.mc.intensify.item.StrengtheningStone;

@Mod.EventBusSubscriber(modid = Intensify.MODID)
public class ItemRegistry {
    public static final Item STRENGTHENING_STONE = new StrengtheningStone();
    public static final Item ENENG_STONE = new EnengStone();
    public static final Item ETERNAL_STONE = new EternalStone();
    public static final Item PROTECTION_STONE = new ProtectionStone();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        register(registry, STRENGTHENING_STONE, IntensifyStoneType.STRENGTHENING_STONE.getIdentifier());
        register(registry, ENENG_STONE, IntensifyStoneType.ENENG_STONE.getIdentifier());
        register(registry, ETERNAL_STONE, IntensifyStoneType.ETERNAL_STONE.getIdentifier());
        register(registry, PROTECTION_STONE, IntensifyStoneType.PROTECTION_STONE.getIdentifier());
    }

    private static void register(IForgeRegistry<Item> registry, Item item, String id) {
        item.setRegistryName(Intensify.MODID, id);
        item.setUnlocalizedName(Intensify.MODID + "." + id);
        item.setCreativeTab(CreativeTabs.MATERIALS);
        registry.register(item);
    }
}
