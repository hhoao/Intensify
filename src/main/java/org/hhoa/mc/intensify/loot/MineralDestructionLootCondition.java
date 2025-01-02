package org.hhoa.mc.intensify.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.hhoa.mc.intensify.config.Config;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MineralDestructionLootCondition implements LootItemCondition {
    public static final String IDENTIFIER = "mineral_destruction";
    public static final LootItemConditionType LOOT_ITEM_CONDITION_TYPE = new LootItemConditionType(new Serializer());
    private final IntensifyStoneType intensifyStoneType;

    public MineralDestructionLootCondition(IntensifyStoneType intensifyStoneType) {
        this.intensifyStoneType = intensifyStoneType;
    }

    @Override
    public boolean test(LootContext lootContext) {
        StoneDropoutProbabilityConfig configValueMap = Config.getStoneDropoutProbabilityConfig();
        Map<Block, Double> stoneDropoutProbability =
            (Map<Block, Double>) configValueMap.getStoneProbabilities(intensifyStoneType, DropTypeEnum.MINERAL_BLOCK_DESTROYED);
        BlockState blockState = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState == null) {
            return false;
        }

        Double probability = stoneDropoutProbability.get(blockState.getBlock());

        if (probability == null || !lootContext.hasParam(LootContextParams.TOOL)) {
            return false;
        }

        if (lootContext.hasParam(LootContextParams.TOOL)) {
            var tool = lootContext.getParamOrNull(LootContextParams.TOOL);
            return tool == null || EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SILK_TOUCH, tool) <= 0;
        }

        return ThreadLocalRandom.current().nextFloat() < probability;
    }

    @Override
    public LootItemConditionType getType() {
        return LOOT_ITEM_CONDITION_TYPE;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MineralDestructionLootCondition> {
        @Override
        public void serialize(JsonObject jsonObject, MineralDestructionLootCondition lootCondition, JsonSerializationContext jsonSerializationContext) {
//            Map<String, Double> blocks = lootCondition.blocks.entrySet()
//                .stream()
//                .collect(Collectors.toMap(
//                    blockFloatEntry -> ForgeRegistries.BLOCKS.getKey(blockFloatEntry.getKey()).toString(),
//                    Map.Entry::getValue));
            jsonObject.add("intensifyStoneType", jsonSerializationContext.serialize(lootCondition.intensifyStoneType));
        }

        @Override
        public MineralDestructionLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
//            Map<String, Double> blockLocationWithProbabilitySet = context.deserialize(jsonObject.get("blockProbabilities"), Map.class);
//            Map<Block, Double> blockSet = blockLocationWithProbabilitySet.entrySet()
//                .stream()
//                .collect(Collectors.toMap(blockLocationWithProbability ->
//                        ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockLocationWithProbability.getKey())),
//                    Map.Entry::getValue));
//            return new MineralDestructionLootCondition(blockSet);
            IntensifyStoneType intensifyStoneType = context.deserialize(jsonObject.get("intensifyStoneType"), IntensifyStoneType.class);
            return new MineralDestructionLootCondition(intensifyStoneType);
        }
    }
}
