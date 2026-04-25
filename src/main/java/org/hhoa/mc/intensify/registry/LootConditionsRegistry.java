package org.hhoa.mc.intensify.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.loot.MineralDestructionLootCondition;

public class LootConditionsRegistry {

    public static void initialize(IEventBus iEventBus) {
        Registry.register(
                Registry.LOOT_CONDITION_TYPE,
                new ResourceLocation(Intensify.MODID, MineralDestructionLootCondition.IDENTIFIER),
                MineralDestructionLootCondition.LOOT_ITEM_CONDITION_TYPE);
    }
}
