package org.hhoa.mc.intensify.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ConfigRegistry;

public class MineralDestructionLootCondition implements LootItemCondition {
    public static final String IDENTIFIER = "mineral_destruction";
    public static final MapCodec<MineralDestructionLootCondition> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance ->
                            instance.group(
                                            Codec.STRING
                                                    .fieldOf("intensifyStoneType")
                                                    .forGetter(
                                                            condition ->
                                                                    condition.intensifyStoneType
                                                                            .getIdentifier()))
                                    .apply(
                                            instance,
                                            identifier ->
                                                    new MineralDestructionLootCondition(
                                                            IntensifyStoneType.fromIdentifier(
                                                                    identifier))));
    public static final LootItemConditionType LOOT_ITEM_CONDITION_TYPE =
            new LootItemConditionType(CODEC);
    private final IntensifyStoneType intensifyStoneType;

    public MineralDestructionLootCondition(IntensifyStoneType intensifyStoneType) {
        this.intensifyStoneType = intensifyStoneType;
    }

    @Override
    public boolean test(LootContext lootContext) {
        StoneDropoutProbabilityConfig configValueMap = ConfigRegistry.stoneDropoutProbabilityConfig;
        BlockState blockState = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        var silkTouch =
                lootContext.getLevel()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH);
        if (blockState == null
                || !lootContext.hasParam(LootContextParams.TOOL)
                || EnchantmentHelper.getItemEnchantmentLevel(
                                silkTouch,
                                lootContext.getParam(LootContextParams.TOOL))
                        > 0) {
            return false;
        }

        Double stoneDropOutProbability =
                configValueMap.getStoneDropOutProbability(
                        intensifyStoneType,
                        DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                        blockState.getBlock());

        return ThreadLocalRandom.current().nextDouble() < stoneDropOutProbability;
    }

    @Override
    public LootItemConditionType getType() {
        return LOOT_ITEM_CONDITION_TYPE;
    }
}
