package org.hhoa.mc.intensify.registry;

import static org.hhoa.mc.intensify.Intensify.MODID;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.loot.IntensifyStoneLootModifier;

public class LootRegistry {
    private static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM =
            DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<IntensifyStoneLootModifier.Serializer> INTENSIFY_STONE_LOOT =
            GLM.register(
                    IntensifyStoneType.INTENSIFY_STONE.getIdentifier(),
                    () -> IntensifyStoneLootModifier.SERIALIZER);

    public static void initialize(IEventBus iEventBus) {
        GLM.register(iEventBus);
    }
}
