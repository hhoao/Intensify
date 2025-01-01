package org.hhoa.mc.intensify.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.loot.MineralDestructionLootCondition;

public class LootConditionsRegistry {
    private static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
        DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Intensify.MODID);

    public static final RegistryObject<LootItemConditionType> BLOCK_MINING_CONDITION =
        LOOT_CONDITIONS.register(MineralDestructionLootCondition.IDENTIFIER,
            () -> MineralDestructionLootCondition.LOOT_ITEM_CONDITION_TYPE);

    public static void initialize(IEventBus iEventBus) {
        LOOT_CONDITIONS.register(iEventBus);
    }
}
