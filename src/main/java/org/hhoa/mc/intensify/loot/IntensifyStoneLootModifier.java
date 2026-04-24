package org.hhoa.mc.intensify.loot;

import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.util.WorldAnnouncements;

public class IntensifyStoneLootModifier extends LootModifier {
    public static final Serializer SERIALIZER = new Serializer();
    private final String intensifyItemStoneType;
    private final Item intensifyStone;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public IntensifyStoneLootModifier(
            ILootCondition[] conditionsIn, String intensifyItemStoneType) {
        super(conditionsIn);
        this.intensifyItemStoneType = intensifyItemStoneType;
        Item intensifyStone;
        switch (IntensifyStoneType.valueOf(intensifyItemStoneType.toUpperCase())) {
            case STRENGTHENING_STONE:
                {
                    intensifyStone = ItemRegistry.STRENGTHENING_STONE.get();
                    break;
                }
            case ENENG_STONE:
                {
                    intensifyStone = ItemRegistry.ENENG_STONE.get();
                    break;
                }
            case ETERNAL_STONE:
                {
                    intensifyStone = ItemRegistry.ETERNAL_STONE.get();
                    break;
                }
            case PROTECTION_STONE:
                {
                    intensifyStone = ItemRegistry.PROTECTION_STONE.get();
                    break;
                }
            default:
                {
                    throw new RuntimeException(
                            "Not have this intensifyItemStoneType " + intensifyItemStoneType);
                }
        }
        this.intensifyStone = intensifyStone;
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> list, LootContext lootContext) {
        ItemStack drop = new ItemStack(intensifyStone, 1);
        list.add(drop);
        WorldAnnouncements.announceMiningDrop(lootContext, drop);
        return list;
    }

    public static class Serializer
            extends GlobalLootModifierSerializer<IntensifyStoneLootModifier> {
        @Override
        public IntensifyStoneLootModifier read(
                ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            return new IntensifyStoneLootModifier(
                    ailootcondition, object.get("intensifyItemStoneType").getAsString());
        }

        @Override
        public JsonObject write(IntensifyStoneLootModifier instance) {
            JsonObject jsonObject = this.makeConditions(instance.conditions);
            jsonObject.addProperty("intensifyItemStoneType", instance.intensifyItemStoneType);
            return jsonObject;
        }
    }
}
