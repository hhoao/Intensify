package org.hhoa.mc.intensify.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.concurrent.ThreadLocalRandom;
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
    public static final LootItemConditionType LOOT_ITEM_CONDITION_TYPE =
            new LootItemConditionType(new Serializer());
    private final IntensifyStoneType intensifyStoneType;

    public MineralDestructionLootCondition(IntensifyStoneType intensifyStoneType) {
        this.intensifyStoneType = intensifyStoneType;
    }

    @Override
    public boolean test(LootContext lootContext) {
        StoneDropoutProbabilityConfig configValueMap = ConfigRegistry.stoneDropoutProbabilityConfig;
        BlockState blockState = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState == null
                || !lootContext.hasParam(LootContextParams.TOOL)
                || EnchantmentHelper.getTagEnchantmentLevel(
                                Enchantments.SILK_TOUCH,
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

    public static class Serializer
            implements net.minecraft.world.level.storage.loot.Serializer<
                    MineralDestructionLootCondition> {
        @Override
        public void serialize(
                JsonObject jsonObject,
                MineralDestructionLootCondition lootCondition,
                JsonSerializationContext jsonSerializationContext) {
            jsonObject.add(
                    "intensifyStoneType",
                    jsonSerializationContext.serialize(lootCondition.intensifyStoneType));
        }

        @Override
        public MineralDestructionLootCondition deserialize(
                JsonObject jsonObject, JsonDeserializationContext context) {
            IntensifyStoneType intensifyStoneType =
                    context.deserialize(
                            jsonObject.get("intensifyStoneType"), IntensifyStoneType.class);
            return new MineralDestructionLootCondition(intensifyStoneType);
        }
    }
}
