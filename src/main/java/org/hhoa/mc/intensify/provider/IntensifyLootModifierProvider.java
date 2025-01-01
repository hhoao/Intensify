package org.hhoa.mc.intensify.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import org.hhoa.mc.intensify.config.Config;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.loot.IntensifyStoneLootModifier;
import org.hhoa.mc.intensify.loot.MineralDestructionLootCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IntensifyLootModifierProvider extends GlobalLootModifierProvider {
    public IntensifyLootModifierProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    protected void start() {
        addLootModifier(IntensifyStoneType.STRENGTHENING_STONE);
        addLootModifier(IntensifyStoneType.ENENG_STONE);
        addLootModifier(IntensifyStoneType.PROTECTION_STONE);
        addLootModifier(IntensifyStoneType.ETERNAL_STONE);
    }

    private void addLootModifier(IntensifyStoneType strengtheningStone) {
        MineralDestructionLootCondition blockMiningLootCondition = getMineralDestructionLootCondition(strengtheningStone);
        IntensifyStoneLootModifier strengtheningStoneLootModifier = new IntensifyStoneLootModifier(
            new LootItemCondition[]{
                blockMiningLootCondition
            }, strengtheningStone.getIdentifier());

        add(strengtheningStone.getIdentifier(), strengtheningStoneLootModifier);
    }

    private static @NotNull MineralDestructionLootCondition getMineralDestructionLootCondition(IntensifyStoneType stoneType) {
        return new MineralDestructionLootCondition(stoneType);
    }
}
