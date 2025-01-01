package org.hhoa.mc.intensify.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.EnengStone;
import org.hhoa.mc.intensify.item.EternalStone;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.item.ProtectionStone;
import org.hhoa.mc.intensify.item.StrengtheningStone;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Intensify.MODID);

    public static final RegistryObject<Item> STRENGTHENING_STONE =
        ITEMS.register(IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(), () -> new StrengtheningStone(new Item.Properties()));

    public static final RegistryObject<Item> ENENG_STONE =
        ITEMS.register(IntensifyStoneType.ENENG_STONE.getIdentifier(), () -> new EnengStone(new Item.Properties()));

    public static final RegistryObject<Item> ETERNAL_STONE =
        ITEMS.register(IntensifyStoneType.ETERNAL_STONE.getIdentifier(), () -> new EternalStone(new Item.Properties()));

    public static final RegistryObject<Item> PROTECTION_STONE =
        ITEMS.register(IntensifyStoneType.PROTECTION_STONE.getIdentifier(), () -> new ProtectionStone(new Item.Properties()));

    public static void initialize(IEventBus iEventBus) {
        ITEMS.register(iEventBus);
    }
}
