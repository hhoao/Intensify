package org.hhoa.mc.intensify.provider;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.loot.IntensifyStoneLootModifier;
import org.hhoa.mc.intensify.loot.MineralDestructionLootCondition;
import org.jetbrains.annotations.NotNull;

public class IntensifyLootModifierProvider extends GlobalLootModifierProvider {
    public IntensifyLootModifierProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
    protected void start() {
        addLootModifier(IntensifyStoneType.STRENGTHENING_STONE);
        addLootModifier(IntensifyStoneType.ENENG_STONE);
        addLootModifier(IntensifyStoneType.PROTECTION_STONE);
        addLootModifier(IntensifyStoneType.ETERNAL_STONE);
    }

    private void addLootModifier(IntensifyStoneType strengtheningStone) {
        MineralDestructionLootCondition blockMiningLootCondition =
                getMineralDestructionLootCondition(strengtheningStone);
        IntensifyStoneLootModifier strengtheningStoneLootModifier =
                new IntensifyStoneLootModifier(
                        new LootItemCondition[] {blockMiningLootCondition},
                        strengtheningStone.getIdentifier());

        add(strengtheningStone.getIdentifier(), strengtheningStoneLootModifier);
    }

    private static @NotNull MineralDestructionLootCondition getMineralDestructionLootCondition(
            IntensifyStoneType stoneType) {
        return new MineralDestructionLootCondition(stoneType);
    }
}
