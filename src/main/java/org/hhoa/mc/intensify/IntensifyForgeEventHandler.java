package org.hhoa.mc.intensify;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.provider.IntensifyAdvancementProvider;
import org.hhoa.mc.intensify.registry.AttachmentRegistry;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;
import org.hhoa.mc.intensify.util.PlayerUtils;
import org.slf4j.Logger;

public class IntensifyForgeEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<IntensifyStoneType> MINING_DROP_STONE_TYPES =
            List.of(
                    IntensifyStoneType.STRENGTHENING_STONE,
                    IntensifyStoneType.ENENG_STONE,
                    IntensifyStoneType.ETERNAL_STONE,
                    IntensifyStoneType.PROTECTION_STONE);
    private static final List<ResourceKey<Recipe<?>>> DISPLAY_RECIPE_KEYS =
            List.of(
                    recipeKey("recipe_book_display/eneng_stone"),
                    recipeKey("recipe_book_display/strengthening_stone"),
                    recipeKey("recipe_book_display/eternal_stone"));
    private static final List<ResourceKey<Recipe<?>>> LEGACY_RECIPE_KEYS =
            List.of(
                    recipeKey("eneng_stone"),
                    recipeKey("strengthening_stone"),
                    recipeKey("intensify_stone"));

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (blockEntity instanceof FurnaceBlockEntity && !event.getLevel().isClientSide) {
            CompoundTag persistentData = blockEntity.getPersistentData();
            Player player = event.getEntity();
            persistentData.putString(
                    IntensifyConstants.FURNACE_OWNER_TAG_ID, player.getName().getString());
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Level level = event.getEntity().level();

        if (!level.isClientSide) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            if (!player.getData(AttachmentRegistry.FIRST_LOGIN)) {
                completeAdvancement(
                        player.getAdvancements(),
                        player.getServer(),
                        IntensifyAdvancementProvider.INTENSIFY_ADVANCEMENT_ID);
                player.setData(AttachmentRegistry.FIRST_LOGIN, true);
            }
            syncRecipeBookDisplayRecipes(player);
        }
    }

    @SubscribeEvent
    public void onBlockDrops(BlockDropsEvent event) {
        boolean silkTouch = hasSilkTouch(event.getLevel(), event.getTool());
        List<ItemStack> drops =
                createMiningStoneDrops(
                        event.getState(),
                        event.getBreaker() instanceof Player,
                        silkTouch);
        if (!drops.isEmpty() || shouldLogMiningAttempt(event, silkTouch)) {
            LOGGER.info(
                    "Mining drop event: block={}, breaker={}, tool={}, silkTouch={}, totalRate={}, strengtheningProb={}, enengProb={}, eternalProb={}, protectionProb={}, drops={}",
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK
                            .getKey(event.getState().getBlock()),
                    event.getBreaker() == null ? "null" : event.getBreaker().getType(),
                    event.getTool(),
                    silkTouch,
                    ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().get(),
                    ConfigRegistry.stoneDropoutProbabilityConfig.getStoneDropOutProbability(
                            IntensifyStoneType.STRENGTHENING_STONE,
                            DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                            event.getState().getBlock()),
                    ConfigRegistry.stoneDropoutProbabilityConfig.getStoneDropOutProbability(
                            IntensifyStoneType.ENENG_STONE,
                            DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                            event.getState().getBlock()),
                    ConfigRegistry.stoneDropoutProbabilityConfig.getStoneDropOutProbability(
                            IntensifyStoneType.ETERNAL_STONE,
                            DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                            event.getState().getBlock()),
                    ConfigRegistry.stoneDropoutProbabilityConfig.getStoneDropOutProbability(
                            IntensifyStoneType.PROTECTION_STONE,
                            DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                            event.getState().getBlock()),
                    drops);
        }
        for (ItemStack drop : drops) {
            event.getDrops()
                    .add(
                            new ItemEntity(
                                    event.getLevel(),
                                    event.getPos().getX() + 0.5D,
                                    event.getPos().getY() + 0.5D,
                                    event.getPos().getZ() + 0.5D,
                                    drop));
        }
    }

    public static List<ItemStack> createMiningStoneDrops(
            BlockState state, boolean playerHarvested, boolean silkTouching) {
        if (!playerHarvested || silkTouching) {
            return List.of();
        }

        return MINING_DROP_STONE_TYPES.stream()
                .map(
                        stoneType ->
                                ConfigRegistry.stoneDropoutProbabilityConfig.dropStone(
                                        stoneType,
                                        DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                                        state.getBlock()))
                .flatMap(Optional::stream)
                .map(ItemStack::new)
                .toList();
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        EquipmentSlot slot = event.getSlot();
        if (slot != EquipmentSlot.HEAD
                && slot != EquipmentSlot.CHEST
                && slot != EquipmentSlot.LEGS
                && slot != EquipmentSlot.FEET) {
            return;
        }

        migrateLegacyArmorModifiers(event.getTo(), slot);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("intensify")
                        .then(
                                Commands.literal("stone_dropout_rate")
                                        .then(
                                                Commands.argument(
                                                                "rate",
                                                                DoubleArgumentType.doubleArg(
                                                                        0,
                                                                        Double.MAX_VALUE)) // 设定倍率范围
                                                        .executes(
                                                                context -> {
                                                                    double rate =
                                                                            DoubleArgumentType
                                                                                    .getDouble(
                                                                                            context,
                                                                                            "rate");
                                                                    ConfigRegistry
                                                                            .stoneDropoutProbabilityConfig
                                                                            .getTotalRate()
                                                                            .set(rate);
                                                                    LOGGER.info(
                                                                            "Updated stone drop total_rate to {}",
                                                                            rate);
                                                                    context.getSource()
                                                                            .sendSuccess(
                                                                                    () ->
                                                                                            TranslatableTexts
                                                                                                    .SET_STONE_DROP_RATE_TIP
                                                                                                    .component(
                                                                                                            rate),
                                                                                    true);
                                                                    return 1;
                                                                })))
                        .then(
                                Commands.literal("upgrade_multiplier")
                                        .then(
                                                Commands.argument(
                                                                "rate",
                                                                DoubleArgumentType.doubleArg(
                                                                        0,
                                                                        Double.MAX_VALUE)) // 设定倍率范围
                                                        .executes(
                                                                context -> {
                                                                    double rate =
                                                                            DoubleArgumentType
                                                                                    .getDouble(
                                                                                            context,
                                                                                            "rate");
                                                                    ConfigRegistry
                                                                            .UPGRADE_MULTIPLIER
                                                                            .set(rate);
                                                                    context.getSource()
                                                                            .sendSuccess(
                                                                                    () ->
                                                                                            TranslatableTexts
                                                                                                    .SET_UPGRADE_MULTIPLIER_TIP
                                                                                                    .component(
                                                                                                            rate),
                                                                                    true);
                                                                    return 1;
                                                                })))
                        .then(
                                Commands.literal("attribute_multiplier")
                                        .then(
                                                Commands.argument(
                                                                "rate",
                                                                DoubleArgumentType.doubleArg(
                                                                        0, Double.MAX_VALUE))
                                                        .executes(
                                                                context -> {
                                                                    double rate =
                                                                            DoubleArgumentType
                                                                                    .getDouble(
                                                                                            context,
                                                                                            "rate");
                                                                    ConfigRegistry
                                                                            .ATTRIBUTE_MULTIPLIER
                                                                            .set(rate);
                                                                    context.getSource()
                                                                            .sendSuccess(
                                                                                    () ->
                                                                                            TranslatableTexts
                                                                                                    .SET_ATTRIBUTE_MULTIPLIER_TIP
                                                                                                    .component(
                                                                                                            rate),
                                                                                    true);
                                                                    return 1;
                                                                }))));
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        Level world = player.level();

        if (!world.isClientSide) {
            LivingEntity entity = event.getEntity();
            Optional<Item> item =
                    ConfigRegistry.stoneDropoutProbabilityConfig.dropStone(
                            DropTypeEnum.FISHING, entity.getType());
            if (item.isPresent()) {
                Item stone = item.get();
                ItemStack stoneItemStack = new ItemStack(stone);
                PlayerUtils.fireItemToPlayer(stoneItemStack, player);
            }
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation registryName =
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(
                        event.getItemStack().getItem());
        List<Component> toolTip = event.getToolTip();
        if (registryName != null) {
            ItemStack itemStack = event.getItemStack();
            ToolIntensifyConfig toolIntensifyConfig =
                    IntensifyConfig.getToolIntensifyConfig(itemStack.getItem());
            if (toolIntensifyConfig != null) {
                modifyToolTip(itemStack, toolTip);
            }
        }
    }

    private static void modifyToolTip(ItemStack itemStack, List<Component> toolTip) {
        int level = IntensifyConfig.getEnhancementIntensifySystem().getLevel(itemStack);
        boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(itemStack);
        Component component = toolTip.get(0);
        if (level > 0) {
            List<Component> siblings = component.getSiblings();
            siblings.add(Component.literal("+" + level));
        } else if (eneng) {
            List<Component> siblings = component.getSiblings();
            siblings.add(Component.literal("*"));
        }
        if (component instanceof MutableComponent) {
            MutableComponent mutableComponent = (MutableComponent) component;
            Style newStyle = mutableComponent.getStyle();
            if (level >= 25) {
                newStyle = component.getStyle().withColor(ChatFormatting.RED);
            } else if (level >= 20) {
                newStyle = component.getStyle().withColor(ChatFormatting.LIGHT_PURPLE);
            } else if (level >= 15) {
                newStyle = component.getStyle().withColor(ChatFormatting.YELLOW);
            } else if (level >= 10) {
                newStyle = component.getStyle().withColor(ChatFormatting.BLUE);
            } else if (level > 0 && eneng) {
                newStyle = component.getStyle().withColor(ChatFormatting.GREEN);
            } else if (eneng) {
                newStyle = component.getStyle().withColor(ChatFormatting.AQUA);
            }
            mutableComponent.setStyle(newStyle);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!level.isClientSide) {
            createMobKillStoneDrop(
                            entity.getType(),
                            true,
                            event.getSource().getDirectEntity() instanceof Player)
                    .ifPresent(
                            stoneItemStack ->
                                    event.getDrops()
                                            .add(
                                                    new ItemEntity(
                                                            entity.level(),
                                                            entity.getX(),
                                                            entity.getY(),
                                                            entity.getZ(),
                                                            stoneItemStack)));
        }
    }

    public static Optional<ItemStack> createMobKillStoneDrop(
            Object lookupKey, boolean livingMob, boolean directlyKilledByPlayer) {
        if (!livingMob || !directlyKilledByPlayer) {
            return Optional.empty();
        }
        return ConfigRegistry.stoneDropoutProbabilityConfig
                .dropStone(DropTypeEnum.MOB_KILLED, lookupKey)
                .map(ItemStack::new);
    }

    private static boolean hasSilkTouch(ServerLevel level, ItemStack tool) {
        if (tool.isEmpty()) {
            return false;
        }
        var silkTouch =
                level.registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH);
        return EnchantmentHelper.getItemEnchantmentLevel(silkTouch, tool) > 0;
    }

    private static boolean shouldLogMiningAttempt(BlockDropsEvent event, boolean silkTouch) {
        if (ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().get() > 1.0D) {
            return true;
        }
        if (silkTouch) {
            return true;
        }
        return ConfigRegistry.stoneDropoutProbabilityConfig.getStoneDropOutProbability(
                        IntensifyStoneType.STRENGTHENING_STONE,
                        DropTypeEnum.MINERAL_BLOCK_DESTROYED,
                        event.getState().getBlock())
                > 0.0D;
    }

    @SubscribeEvent
    public void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        Level level = event.getEntity().level();

        if (level.isClientSide()) return;
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ItemStack smelting = event.getSmelting();
        if (player.getServer() != null) {
            boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(smelting);
            if (eneng && player.getServer() != null) {
                completeAdvancement(
                        player.getAdvancements(),
                        player.getServer(),
                        IntensifyAdvancementProvider.FIRST_ENENG_ADVANCEMENT_ID);
            }
            int itemLevel = IntensifyConfig.getEnhancementIntensifySystem().getLevel(smelting);
            if (itemLevel > 0) {
                completeAdvancement(
                        player.getAdvancements(),
                        player.getServer(),
                        IntensifyAdvancementProvider.FIRST_STRENGTHENING_ADVANCEMENT_ID);
            }
        }
    }

    private static void completeAdvancement(
            PlayerAdvancements advancements,
            MinecraftServer server,
            ResourceLocation advancementId) {
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);
        if (advancement != null) {
            AdvancementProgress progress = advancements.getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    advancements.award(advancement, criterion);
                }
            }
        }
    }

    private static void syncRecipeBookDisplayRecipes(ServerPlayer player) {
        player.awardRecipesByKey(DISPLAY_RECIPE_KEYS);

        List<RecipeHolder<?>> legacyRecipes = new ArrayList<>();
        for (ResourceKey<Recipe<?>> recipeKey : LEGACY_RECIPE_KEYS) {
            player.getServer()
                    .getRecipeManager()
                    .byKey(recipeKey)
                    .ifPresent(legacyRecipes::add);
        }

        if (!legacyRecipes.isEmpty()) {
            player.resetRecipes(legacyRecipes);
        }
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(
                Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(Intensify.MODID, path));
    }

    private static void migrateLegacyArmorModifiers(ItemStack itemStack, EquipmentSlot slot) {
        if (itemStack.isEmpty()) {
            return;
        }

        ToolIntensifyConfig intensifyConfig = IntensifyConfig.getToolIntensifyConfig(itemStack.getItem());
        if (intensifyConfig == null) {
            return;
        }

        for (ToolIntensifyConfig.AttributeConfig attributeConfig : intensifyConfig.getAttributes()) {
            ResourceLocation slotScopedId =
                    IntensifyConfig.getEnhancementIntensifySystem()
                            .getAttributeModifierId(attributeConfig.getType(), slot);
            ResourceLocation legacyId =
                    IntensifyConfig.getEnhancementIntensifySystem()
                            .getAttributeModifierId(attributeConfig.getType());

            if (slotScopedId.equals(legacyId)) {
                continue;
            }

            ItemModifierHelper.getAttributeModifiers(
                            itemStack, attributeConfig.getType(), slot, legacyId)
                    .forEach(
                            modifier ->
                                    ItemModifierHelper.setAttributeModifier(
                                            itemStack,
                                            attributeConfig.getType(),
                                            new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                                    slotScopedId,
                                                    modifier.amount(),
                                                    modifier.operation()),
                                            slot,
                                            legacyId,
                                            slotScopedId));
        }
    }

}
