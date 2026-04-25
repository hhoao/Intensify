package org.hhoa.mc.intensify.provider;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.command.FunctionObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.TranslatableTexts;

public class IntensifyAdvancementProvider extends AdvancementProvider {
    public static final ResourceLocation INTENSIFY_ADVANCEMENT_ID =
            new ResourceLocation(Intensify.MODID, "intensify");
    public static final ResourceLocation FIRST_ENENG_ADVANCEMENT_ID =
            new ResourceLocation(Intensify.MODID, "first_eneng");
    public static final ResourceLocation FIRST_STRENGTHENING_ADVANCEMENT_ID =
            new ResourceLocation(Intensify.MODID, "first_strengthening");

    public IntensifyAdvancementProvider(DataGenerator output) {
        super(output);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer) {
        Advancement stoneMiningAdvancement =
                Advancement.Builder.builder()
                        .withDisplay(
                                Items.FURNACE,
                                TranslatableTexts.ADVANCEMENT_INTENSIFY_TITLE.component(),
                                TranslatableTexts.ADVANCEMENT_INTENSIFY_DESCRIPTION.component(),
                                new ResourceLocation("minecraft", "textures/block/furnace.png"),
                                FrameType.TASK,
                                true, // show toast
                                true, // announce to chat
                                false // hidden
                                )
                        .withCriterion("impossible", new ImpossibleTrigger.Instance())
                        .withRewards(
                                new AdvancementRewards(
                                        100,
                                        new ResourceLocation[] {
                                            IntensifyLootTableProvider.ENENG_STONE_LOOT_TABLE_ID
                                        },
                                        new ResourceLocation[0],
                                        FunctionObject.CacheableFunction.EMPTY))
                        .register(consumer, INTENSIFY_ADVANCEMENT_ID.toString());

        Advancement enengAdvancement =
                Advancement.Builder.builder()
                        .withDisplay(
                                Items.FURNACE,
                                TranslatableTexts.ADVANCEMENT_FIRST_ENENG_TITLE.component(),
                                TranslatableTexts.ADVANCEMENT_FIRST_ENENG_DESCRIPTION.component(),
                                new ResourceLocation("minecraft", "textures/block/furnace.png"),
                                FrameType.TASK,
                                true, // show toast
                                true, // announce to chat
                                false // hidden
                                )
                        .withCriterion("impossible", new ImpossibleTrigger.Instance())
                        .withRewards(
                                new AdvancementRewards(
                                        100,
                                        new ResourceLocation[] {
                                            IntensifyLootTableProvider
                                                    .STRENGTHENING_STONE_LOOT_TABLE_ID
                                        },
                                        new ResourceLocation[0],
                                        FunctionObject.CacheableFunction.EMPTY))
                        .register(consumer, FIRST_ENENG_ADVANCEMENT_ID.toString());

        Advancement firstStrnengtheningAdvancement =
                Advancement.Builder.builder()
                        .withDisplay(
                                Items.FURNACE,
                                TranslatableTexts.ADVANCEMENT_FIRST_STRENGTHENING_TITLE.component(),
                                TranslatableTexts.ADVANCEMENT_FIRST_STRENGTHENING_DESCRIPTION
                                        .component(),
                                new ResourceLocation("minecraft", "textures/block/furnace.png"),
                                FrameType.TASK,
                                true, // show toast
                                true, // announce to chat
                                false // hidden
                                )
                        .withCriterion("impossible", new ImpossibleTrigger.Instance())
                        .withRewards(
                                new AdvancementRewards(
                                        100,
                                        new ResourceLocation[] {
                                            IntensifyLootTableProvider
                                                    .PROTECTION_STONE_LOOT_TABLE_ID
                                        },
                                        new ResourceLocation[0],
                                        FunctionObject.CacheableFunction.EMPTY))
                        .register(consumer, FIRST_STRENGTHENING_ADVANCEMENT_ID.toString());
        AdvancementRewards build =
                AdvancementRewards.Builder.recipe(
                                IntensifyLootTableProvider.PROTECTION_STONE_LOOT_TABLE_ID)
                        .build();
    }
}
