package org.hhoa.mc.intensify.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.EnengStone;
import org.hhoa.mc.intensify.item.EternalStone;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.item.ProtectionStone;
import org.hhoa.mc.intensify.item.StrengtheningStone;

public class ItemRegistry {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Intensify.MODID);

    public static final DeferredHolder<Item, Item> STRENGTHENING_STONE =
            ITEMS.register(
                    IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
                    key ->
                            new StrengtheningStone(
                                    new Item.Properties()
                                            .setId(ResourceKey.create(Registries.ITEM, key))));

    public static final DeferredHolder<Item, Item> ENENG_STONE =
            ITEMS.register(
                    IntensifyStoneType.ENENG_STONE.getIdentifier(),
                    key ->
                            new EnengStone(
                                    new Item.Properties()
                                            .setId(ResourceKey.create(Registries.ITEM, key))));

    public static final DeferredHolder<Item, Item> ETERNAL_STONE =
            ITEMS.register(
                    IntensifyStoneType.ETERNAL_STONE.getIdentifier(),
                    key ->
                            new EternalStone(
                                    new Item.Properties()
                                            .setId(ResourceKey.create(Registries.ITEM, key))));

    public static final DeferredHolder<Item, Item> PROTECTION_STONE =
            ITEMS.register(
                    IntensifyStoneType.PROTECTION_STONE.getIdentifier(),
                    key ->
                            new ProtectionStone(
                                    new Item.Properties()
                                            .setId(ResourceKey.create(Registries.ITEM, key))));

    public static void initialize(IEventBus iEventBus) {
        ITEMS.register(iEventBus);
    }
}
