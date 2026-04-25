package org.hhoa.mc.intensify.provider;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyLootTableProvider extends LootTableProvider {
    public static final ResourceLocation ENENG_STONE_LOOT_TABLE_ID =
            new ResourceLocation(Intensify.MODID, IntensifyStoneType.ENENG_STONE.getIdentifier());;
    public static final ResourceLocation STRENGTHENING_STONE_LOOT_TABLE_ID =
            new ResourceLocation(
                    Intensify.MODID, IntensifyStoneType.STRENGTHENING_STONE.getIdentifier());;
    public static final ResourceLocation PROTECTION_STONE_LOOT_TABLE_ID =
            new ResourceLocation(
                    Intensify.MODID, IntensifyStoneType.PROTECTION_STONE.getIdentifier());;
    public static final ResourceLocation ETERNAL_STONE_LOOT_TABLE_ID =
            new ResourceLocation(Intensify.MODID, IntensifyStoneType.ETERNAL_STONE.getIdentifier());

    public IntensifyLootTableProvider(DataGenerator output) {
        super(output);
    }

    @Override
    protected List<
                    Pair<
                            Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>,
                            LootParameterSet>>
            getTables() {
        ArrayList<
                        Pair<
                                Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>,
                                LootParameterSet>>
                objects = new ArrayList<>();
        objects.add(
                Pair.of(
                        () ->
                                consumer -> {
                                    LootTable.Builder strengtheningTable =
                                            LootTable.builder()
                                                    .addLootPool(
                                                            LootPool.builder()
                                                                    .bonusRolls(1, 1)
                                                                    .rolls(new ConstantRange(1))
                                                                    .addEntry(
                                                                            ItemLootEntry.builder(
                                                                                    ItemRegistry
                                                                                                    .STRENGTHENING_STONE
                                                                                            ::get)));
                                    consumer.accept(
                                            STRENGTHENING_STONE_LOOT_TABLE_ID, strengtheningTable);

                                    LootTable.Builder enengTable =
                                            LootTable.builder()
                                                    .addLootPool(
                                                            LootPool.builder()
                                                                    .bonusRolls(1, 1)
                                                                    .rolls(new ConstantRange(1))
                                                                    .addEntry(
                                                                            ItemLootEntry.builder(
                                                                                    ItemRegistry
                                                                                                    .ENENG_STONE
                                                                                            ::get)));
                                    consumer.accept(ENENG_STONE_LOOT_TABLE_ID, enengTable);

                                    LootTable.Builder protectionStoneLootTable =
                                            LootTable.builder()
                                                    .addLootPool(
                                                            LootPool.builder()
                                                                    .bonusRolls(1, 1)
                                                                    .rolls(new ConstantRange(1))
                                                                    .addEntry(
                                                                            ItemLootEntry.builder(
                                                                                    ItemRegistry
                                                                                                    .PROTECTION_STONE
                                                                                            ::get)));
                                    consumer.accept(
                                            PROTECTION_STONE_LOOT_TABLE_ID,
                                            protectionStoneLootTable);

                                    LootTable.Builder eternalStoneLootTable =
                                            LootTable.builder()
                                                    .addLootPool(
                                                            LootPool.builder()
                                                                    .bonusRolls(1, 1)
                                                                    .rolls(new ConstantRange(1))
                                                                    .addEntry(
                                                                            ItemLootEntry.builder(
                                                                                    ItemRegistry
                                                                                                    .ETERNAL_STONE
                                                                                            ::get)));
                                    consumer.accept(
                                            ETERNAL_STONE_LOOT_TABLE_ID, eternalStoneLootTable);
                                },
                        LootParameterSets.GIFT));
        return objects;
    }
}
