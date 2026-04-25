package org.hhoa.mc.intensify.provider;

import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

public class CustomTriggerInstance extends InventoryChangeTrigger.TriggerInstance {
    private final PlayerAdvancements advancements;
    private final CriterionProgress criterionProgress;
    private final AtomicReference<CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>>
            listenerAtomicReference;

    public CustomTriggerInstance(
            MinMaxBounds.Ints p_286313_,
            MinMaxBounds.Ints p_286767_,
            MinMaxBounds.Ints p_286601_,
            ItemPredicate[] p_286380_,
            PlayerAdvancements advancements,
            CriterionProgress criterionProgress,
            AtomicReference<CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>>
                    listenerAtomicReference) {
        super(ContextAwarePredicate.ANY, p_286313_, p_286767_, p_286601_, p_286380_);
        this.advancements = advancements;
        this.criterionProgress = criterionProgress;
        this.listenerAtomicReference = listenerAtomicReference;
    }

    public CustomTriggerInstance(
            PlayerAdvancements advancements,
            CriterionProgress criterionProgress,
            AtomicReference<CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>>
                    listenerAtomicReference) {
        this(null, null, null, null, advancements, criterionProgress, listenerAtomicReference);
    }

    @Override
    public boolean matches(
            Inventory inventory, ItemStack itemStack, int p_43189_, int p_43190_, int p_43191_) {
        if (advancements != null
                && listenerAtomicReference.get() != null
                && criterionProgress.isDone()) {
            CriteriaTriggers.INVENTORY_CHANGED.removePlayerListener(
                    advancements, listenerAtomicReference.get());
            listenerAtomicReference.set(null);
        } else {
            Item item = itemStack.getItem();
            if (item instanceof ArmorItem) {
                ToolIntensifyConfig toolIntensifyConfig =
                        IntensifyConfig.getArmorClassConfigMap().get(((ArmorItem) item).getType());
                return toolIntensifyConfig != null;
            } else if (item instanceof Vanishable) {
                ToolIntensifyConfig toolIntensifyConfig =
                        IntensifyConfig.getToolWeaponClassConfigMap().get(item.getClass());
                return toolIntensifyConfig != null;
            }
        }
        return false;
    }
}
