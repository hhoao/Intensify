package org.hhoa.mc.intensify.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ItemRegistry;

import java.util.function.Supplier;

public class IntensifyStoneLootModifier extends LootModifier {
    public static final Supplier<Codec<IntensifyStoneLootModifier>> CODEC =
        Suppliers.memoize(
            () -> RecordCodecBuilder.create(
                inst ->
                    codecStart(inst)
                        .and(ExtraCodecs.NON_EMPTY_STRING
                            .optionalFieldOf("intensifyItemStoneType", IntensifyStoneType.STRENGTHENING_STONE.getIdentifier())
                            .forGetter(m -> m.intensifyItemStoneType))
                        .apply(inst, IntensifyStoneLootModifier::new)
            ));
    private final String intensifyItemStoneType;
    private final Item intensifyStone;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public IntensifyStoneLootModifier(LootItemCondition[] conditionsIn, String intensifyItemStoneType) {
        super(conditionsIn);
        this.intensifyItemStoneType = intensifyItemStoneType;
        Item intensifyStone;
        switch (IntensifyStoneType.valueOf(intensifyItemStoneType.toUpperCase())) {
            case STRENGTHENING_STONE: {
                intensifyStone = ItemRegistry.STRENGTHENING_STONE.get();
                break;
            }
            case ENENG_STONE: {
                intensifyStone = ItemRegistry.ENENG_STONE.get();
                break;
            }
            default:
                intensifyStone = ItemRegistry.STRENGTHENING_STONE.get();
        }
        this.intensifyStone = intensifyStone;
    }

    @Override
    public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> original, LootContext context) {
        original.add(new ItemStack(intensifyStone, 1));
        return original;
    }


    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
