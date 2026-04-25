package org.hhoa.mc.intensify.provider;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyLootTableProvider extends LootTableProvider {
    public static final ResourceLocation ENENG_STONE_LOOT_TABLE_ID =
            new ResourceLocation(Intensify.MODID, IntensifyStoneType.ENENG_STONE.getIdentifier());
    ;
    public static final ResourceLocation STRENGTHENING_STONE_LOOT_TABLE_ID =
            new ResourceLocation(
                    Intensify.MODID, IntensifyStoneType.STRENGTHENING_STONE.getIdentifier());
    ;
    public static final ResourceLocation PROTECTION_STONE_LOOT_TABLE_ID =
            new ResourceLocation(
                    Intensify.MODID, IntensifyStoneType.PROTECTION_STONE.getIdentifier());
    ;
    public static final ResourceLocation ETERNAL_STONE_LOOT_TABLE_ID =
            new ResourceLocation(Intensify.MODID, IntensifyStoneType.ETERNAL_STONE.getIdentifier());

    public IntensifyLootTableProvider(PackOutput output) {
        super(
                output,
                Set.of(),
                List.of(new SubProviderEntry(ItemLootTables::new, LootContextParamSets.GIFT)));
    }

    public static class ItemLootTables implements LootTableSubProvider {
        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            LootTable.Builder strengtheningTable =
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1))
                                            .add(
                                                    LootItem.lootTableItem(
                                                            ItemRegistry.STRENGTHENING_STONE
                                                                    .get())));
            consumer.accept(STRENGTHENING_STONE_LOOT_TABLE_ID, strengtheningTable);

            LootTable.Builder enengTable =
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1))
                                            .add(
                                                    LootItem.lootTableItem(
                                                            ItemRegistry.ENENG_STONE.get())));
            consumer.accept(ENENG_STONE_LOOT_TABLE_ID, enengTable);

            LootTable.Builder protectionStoneLootTable =
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1))
                                            .add(
                                                    LootItem.lootTableItem(
                                                            ItemRegistry.PROTECTION_STONE.get())));
            consumer.accept(PROTECTION_STONE_LOOT_TABLE_ID, protectionStoneLootTable);

            LootTable.Builder eternalStoneLootTable =
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1))
                                            .add(
                                                    LootItem.lootTableItem(
                                                            ItemRegistry.ETERNAL_STONE.get())));
            consumer.accept(ETERNAL_STONE_LOOT_TABLE_ID, eternalStoneLootTable);
        }
    }
}
