package org.hhoa.mc.intensify;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.capabilities.FirstLoginCapabilityProvider;
import org.hhoa.mc.intensify.capabilities.IFirstLoginCapability;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.provider.CustomTriggerInstance;
import org.hhoa.mc.intensify.provider.IntensifyAdvancementProvider;
import org.hhoa.mc.intensify.provider.IntensifyStoneRecipeProvider;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.PlayerUtils;

public class IntensifyForgeEventHandler {
    public static final Capability<IFirstLoginCapability> FIRST_LOGIN_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation FIRST_LOGIN_CAP =
            Intensify.location("first_login_capability");
    private static final ResourceLocation[] DISPLAY_RECIPE_KEYS =
            new ResourceLocation[] {
                recipeKey("recipe_book_display/eneng_stone"),
                recipeKey("recipe_book_display/strengthening_stone"),
                recipeKey("recipe_book_display/eternal_stone")
            };
    private static final ResourceLocation[] LEGACY_RECIPE_KEYS =
            new ResourceLocation[] {
                recipeKey("eneng_stone"),
                recipeKey("strengthening_stone"),
                recipeKey("intensify_stone")
            };

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (blockEntity instanceof FurnaceBlockEntity) {
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
            LazyOptional<IFirstLoginCapability> capability =
                    player.getCapability(FIRST_LOGIN_CAPABILITY);

            capability.ifPresent(
                    cap -> {
                        if (!cap.hasLoggedIn()) {
                            completeAdvancement(
                                    player.getAdvancements(),
                                    player.getServer(),
                                    IntensifyAdvancementProvider.INTENSIFY_ADVANCEMENT_ID);
                            cap.setHasLoggedIn(true);
                        }
                    });
            syncRecipeBookDisplayRecipes(player);
        }
    }

    private static void addAdvancementListener(ServerPlayer player, IntensifyStoneType type) {
        Advancement advancement =
                player.getServer()
                        .getAdvancements()
                        .getAdvancement(Intensify.location("recipes/" + type.getIdentifier()));
        if (advancement != null) {
            CriterionProgress criterion =
                    player.getAdvancements()
                            .getOrStartProgress(advancement)
                            .getCriterion(IntensifyStoneRecipeProvider.HAS_STONE);
            if (criterion != null && !criterion.isDone()) {
                AtomicReference<CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>>
                        listenerAtomicReference = new AtomicReference<>();
                CustomTriggerInstance customTriggerInstance =
                        new CustomTriggerInstance(
                                player.getAdvancements(), criterion, listenerAtomicReference);
                CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>
                        customTriggerInstanceListener =
                                new CriterionTrigger.Listener<>(
                                        customTriggerInstance,
                                        advancement,
                                        IntensifyStoneRecipeProvider.HAS_TOOL);
                listenerAtomicReference.set(customTriggerInstanceListener);
                CriteriaTriggers.INVENTORY_CHANGED.addPlayerListener(
                        player.getAdvancements(), customTriggerInstanceListener);
            }
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(FIRST_LOGIN_CAP, new FirstLoginCapabilityProvider());
        }
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
                ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
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
            if (entity instanceof Mob && event.getSource().getEntity() instanceof Player) {
                Optional<Item> item =
                        ConfigRegistry.stoneDropoutProbabilityConfig.dropStone(
                                DropTypeEnum.MOB_KILLED, entity.getType());
                if (item.isPresent()) {
                    Item stone = item.get();
                    ItemStack stoneItemStack = new ItemStack(stone);
                    ItemEntity itemEntity =
                            new ItemEntity(
                                    entity.level(),
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    stoneItemStack);

                    event.getDrops().add(itemEntity);
                }
            }
        }
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
        Advancement advancement = server.getAdvancements().getAdvancement(advancementId);
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

        List<net.minecraft.world.item.crafting.Recipe<?>> legacyRecipes = new ArrayList<>();
        for (ResourceLocation recipeKey : LEGACY_RECIPE_KEYS) {
            player.getServer().getRecipeManager().byKey(recipeKey).ifPresent(legacyRecipes::add);
        }

        if (!legacyRecipes.isEmpty()) {
            player.resetRecipes(legacyRecipes);
        }
    }

    private static ResourceLocation recipeKey(String path) {
        return Intensify.location(path);
    }
}
