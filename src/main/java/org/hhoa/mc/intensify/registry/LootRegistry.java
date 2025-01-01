package org.hhoa.mc.intensify.registry;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.loot.IntensifyStoneLootModifier;

import static org.hhoa.mc.intensify.Intensify.MODID;

public class LootRegistry {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM =
        DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<Codec<IntensifyStoneLootModifier>> INTENSIFY_STONE_LOOT =
        GLM.register(IntensifyStoneType.INTENSIFY_STONE.getIdentifier(), IntensifyStoneLootModifier.CODEC);

    public static void initialize(IEventBus iEventBus) {
        GLM.register(iEventBus);
    }
}
