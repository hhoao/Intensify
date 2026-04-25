package org.hhoa.mc.intensify.provider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class IntensifyAdvancementProvider extends AdvancementProvider {
    public static final ResourceLocation INTENSIFY_ADVANCEMENT_ID =
            ResourceLocation.fromNamespaceAndPath(Intensify.MODID, "intensify");
    public static final ResourceLocation FIRST_ENENG_ADVANCEMENT_ID =
            ResourceLocation.fromNamespaceAndPath(Intensify.MODID, "first_eneng");
    public static final ResourceLocation FIRST_STRENGTHENING_ADVANCEMENT_ID =
            ResourceLocation.fromNamespaceAndPath(Intensify.MODID, "first_strengthening");

    /**
     * Constructs an advancement provider using the generators to write the advancements to a file.
     *
     * @param output the target directory of the data generator
     * @param registries a future of a lookup for registries and their objects
     * @param existingFileHelper a helper used to find whether a file exists
     * @param subProviders the generators used to create the advancements
     */
    public IntensifyAdvancementProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            ExistingFileHelper existingFileHelper,
            List<AdvancementGenerator> subProviders) {
        super(output, registries, existingFileHelper, subProviders);
    }

    public static class ModAdvancementGenerator implements AdvancementGenerator {

        @Override
        public void generate(
                HolderLookup.Provider registries,
                Consumer<AdvancementHolder> consumer,
                ExistingFileHelper existingFileHelper) {
            AdvancementHolder stoneMiningAdvancement =
                    Advancement.Builder.advancement()
                            .display(
                                    Items.FURNACE,
                                    TranslatableTexts.ADVANCEMENT_INTENSIFY_TITLE.component(),
                                    TranslatableTexts.ADVANCEMENT_INTENSIFY_DESCRIPTION.component(),
                                    ResourceLocation.fromNamespaceAndPath(
                                            "minecraft", "textures/block/furnace.png"),
                                    AdvancementType.TASK,
                                    true, // show toast
                                    true, // announce to chat
                                    false // hidden
                                    )
                            .addCriterion(
                                    "impossible",
                                    CriteriaTriggers.IMPOSSIBLE.createCriterion(
                                            new ImpossibleTrigger.TriggerInstance()))
                            .rewards(
                                    AdvancementRewards.Builder.loot(
                                            IntensifyLootTableProvider.ENENG_STONE_LOOT_TABLE_ID))
                            .save(consumer, INTENSIFY_ADVANCEMENT_ID, existingFileHelper);

            AdvancementHolder enengAdvancement =
                    Advancement.Builder.advancement()
                            .display(
                                    Items.FURNACE,
                                    TranslatableTexts.ADVANCEMENT_FIRST_ENENG_TITLE.component(),
                                    TranslatableTexts.ADVANCEMENT_FIRST_ENENG_DESCRIPTION
                                            .component(),
                                    ResourceLocation.fromNamespaceAndPath(
                                            "minecraft", "textures/block/furnace.png"),
                                    AdvancementType.TASK,
                                    true, // show toast
                                    true, // announce to chat
                                    false // hidden
                                    )
                            .addCriterion(
                                    "impossible",
                                    CriteriaTriggers.IMPOSSIBLE.createCriterion(
                                            new ImpossibleTrigger.TriggerInstance()))
                            .rewards(
                                    AdvancementRewards.Builder.loot(
                                            IntensifyLootTableProvider
                                                    .STRENGTHENING_STONE_LOOT_TABLE_ID))
                            .save(consumer, FIRST_ENENG_ADVANCEMENT_ID, existingFileHelper);

            AdvancementHolder firstStrnengtheningAdvancement =
                    Advancement.Builder.advancement()
                            .display(
                                    Items.FURNACE,
                                    TranslatableTexts.ADVANCEMENT_FIRST_STRENGTHENING_TITLE
                                            .component(),
                                    TranslatableTexts.ADVANCEMENT_FIRST_STRENGTHENING_DESCRIPTION
                                            .component(),
                                    ResourceLocation.fromNamespaceAndPath(
                                            "minecraft", "textures/block/furnace.png"),
                                    AdvancementType.TASK,
                                    true, // show toast
                                    true, // announce to chat
                                    false // hidden
                                    )
                            .addCriterion(
                                    "impossible",
                                    CriteriaTriggers.IMPOSSIBLE.createCriterion(
                                            new ImpossibleTrigger.TriggerInstance()))
                            .rewards(
                                    AdvancementRewards.Builder.loot(
                                            IntensifyLootTableProvider
                                                    .PROTECTION_STONE_LOOT_TABLE_ID))
                            .save(
                                    consumer,
                                    FIRST_STRENGTHENING_ADVANCEMENT_ID,
                                    existingFileHelper);
        }
    }
}
