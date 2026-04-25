package org.hhoa.mc.intensify.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;
import org.hhoa.mc.intensify.data.ChunkBlockDataStorage;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ConfigRegistry;

public class MineralDestructionLootCondition implements ILootCondition {
    public static final String IDENTIFIER = "mineral_destruction";
    public static final LootConditionType LOOT_ITEM_CONDITION_TYPE =
            new LootConditionType(new Serializer());
    private final IntensifyStoneType intensifyStoneType;

    public MineralDestructionLootCondition(IntensifyStoneType intensifyStoneType) {
        this.intensifyStoneType = intensifyStoneType;
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockState blockState = lootContext.get(LootParameters.BLOCK_STATE);
        if (blockState == null
                || !lootContext.has(LootParameters.TOOL)
                || EnchantmentHelper.getEnchantmentLevel(
                                Enchantments.SILK_TOUCH, lootContext.get(LootParameters.TOOL))
                        > 0) {
            return false;
        }
        StoneDropoutProbabilityConfig configValueMap = ConfigRegistry.stoneDropoutProbabilityConfig;

        Double stoneDropOutProbability =
                configValueMap.getStoneDropOutProbability(
                        intensifyStoneType,
                        DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                        blockState.getBlock());

        boolean randomResult = ThreadLocalRandom.current().nextDouble() < stoneDropOutProbability;
        if (!randomResult) {
            return false;
        }

        ServerWorld world = lootContext.getWorld();
        ServerChunkProvider chunkProvider = world.getChunkProvider();
        DimensionSavedDataManager savedData = chunkProvider.getSavedData();
        Vector3d origin = lootContext.get(LootParameters.field_237457_g_);
        BlockPos blockPos = new BlockPos(origin);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        ChunkBlockDataStorage orCreate =
                savedData.getOrCreate(
                        () -> new ChunkBlockDataStorage(chunkPos),
                        ChunkBlockDataStorage.getChunkBlockDataName(chunkPos));
        boolean placed = orCreate.getBlockData(new BlockPos(origin));
        return !placed;
    }

    @Override
    public LootConditionType func_230419_b_() {
        return LOOT_ITEM_CONDITION_TYPE;
    }

    public static class Serializer implements ILootSerializer<MineralDestructionLootCondition> {
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
