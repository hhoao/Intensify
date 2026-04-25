package org.hhoa.mc.intensify.registry;

import static org.hhoa.mc.intensify.Intensify.MODID;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.loot.IntensifyStoneLootModifier;

public class LootRegistry {
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final DeferredHolder<
                    MapCodec<? extends IGlobalLootModifier>, MapCodec<IntensifyStoneLootModifier>>
            INTENSIFY_STONE_LOOT =
            GLM.register(
                    IntensifyStoneType.INTENSIFY_STONE.getIdentifier(),
                    () -> IntensifyStoneLootModifier.CODEC);

    public static void initialize(IEventBus iEventBus) {
        GLM.register(iEventBus);
    }
}
