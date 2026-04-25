package org.hhoa.mc.intensify.provider;

import net.minecraft.data.DataGenerator;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.loot.IntensifyStoneLootModifier;
import org.hhoa.mc.intensify.loot.MineralDestructionLootCondition;

public class IntensifyLootModifierProvider extends GlobalLootModifierProvider {
    public IntensifyLootModifierProvider(DataGenerator output, String modid) {
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
        MineralDestructionLootCondition blockMiningLootCondition =
                getMineralDestructionLootCondition(strengtheningStone);
        IntensifyStoneLootModifier strengtheningStoneLootModifier =
                new IntensifyStoneLootModifier(
                        new ILootCondition[] {blockMiningLootCondition},
                        strengtheningStone.getIdentifier());

        add(
                strengtheningStone.getIdentifier(),
                IntensifyStoneLootModifier.SERIALIZER,
                strengtheningStoneLootModifier);
    }

    private static MineralDestructionLootCondition getMineralDestructionLootCondition(
            IntensifyStoneType stoneType) {
        return new MineralDestructionLootCondition(stoneType);
    }
}
