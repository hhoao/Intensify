package org.hhoa.mc.intensify;

import static org.hhoa.mc.intensify.Intensify.FIRST_LOGIN_CAPABILITY;
import static org.hhoa.mc.intensify.config.IntensifyConstants.LIMITED_REPLACED_BLOCKS;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.capabilities.FirstLoginCapabilityProvider;
import org.hhoa.mc.intensify.capabilities.IFirstLoginCapability;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.data.ChunkBlockDataStorage;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.provider.IntensifyAdvancementProvider;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.PlayerUtils;

public class IntensifyForgeEventHandler {
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
        TileEntity blockEntity = event.getWorld().getTileEntity(event.getPos());
        if (blockEntity instanceof FurnaceTileEntity) {
            CompoundNBT persistentData = blockEntity.getTileData();
            PlayerEntity player = event.getPlayer();
            persistentData.putString(
                    IntensifyConstants.FURNACE_OWNER_TAG_ID, player.getName().getString());
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getWorld() instanceof ServerWorld)
                || !LIMITED_REPLACED_BLOCKS.contains(event.getPlacedBlock().getBlock())) {
            return;
        }

        ServerWorld world = (ServerWorld) event.getWorld();
        BlockPos pos = event.getPos();
        ChunkPos chunkPos = new ChunkPos(pos);

        ChunkBlockDataStorage chunkDataStorage =
                world.getSavedData()
                        .getOrCreate(
                                () -> new ChunkBlockDataStorage(chunkPos),
                                ChunkBlockDataStorage.getChunkBlockDataName(chunkPos));

        chunkDataStorage.setBlockData(pos, true);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityMultiPlaceEvent event) {
        List<BlockSnapshot> replacedBlockSnapshots = event.getReplacedBlockSnapshots();
        for (BlockSnapshot replacedBlockSnapshot : replacedBlockSnapshots) {
            System.out.println(replacedBlockSnapshot.getTileEntity());
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            LazyOptional<IFirstLoginCapability> capability =
                    player.getCapability(FIRST_LOGIN_CAPABILITY);

            capability.ifPresent(
                    cap -> {
                        if (!cap.hasLoggedIn()) {
                            completeAdvancement(
                                    serverPlayer.getAdvancements(),
                                    serverPlayer.getServer(),
                                    IntensifyAdvancementProvider.INTENSIFY_ADVANCEMENT_ID);
                            cap.setHasLoggedIn(true);
                        }
                    });
            syncRecipeBookDisplayRecipes(serverPlayer);
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayerEntity) {
            event.addCapability(FIRST_LOGIN_CAP, new FirstLoginCapabilityProvider());
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
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
                                                                            .sendFeedback(
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
                                                                            .sendFeedback(
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
                                                                            .sendFeedback(
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
        PlayerEntity player = event.getPlayer();

        if (player instanceof ServerPlayerEntity) {
            LivingEntity entity = event.getEntityLiving();
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
        List<ITextComponent> toolTip = event.getToolTip();
        if (registryName != null) {
            ItemStack itemStack = event.getItemStack();
            ToolIntensifyConfig toolIntensifyConfig =
                    IntensifyConfig.getToolIntensifyConfig(itemStack.getItem());
            if (toolIntensifyConfig != null) {
                modifyToolTip(itemStack, toolTip);
            }
        }
    }

    private static void modifyToolTip(ItemStack itemStack, List<ITextComponent> toolTip) {
        int level = IntensifyConfig.getEnhancementIntensifySystem().getLevel(itemStack);
        boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(itemStack);
        ITextComponent component = toolTip.get(0);
        if (level > 0) {
            List<ITextComponent> siblings = component.getSiblings();
            siblings.add(new StringTextComponent("+" + level));
        } else if (eneng) {
            List<ITextComponent> siblings = component.getSiblings();
            siblings.add(new StringTextComponent("*"));
        }
        if (component instanceof IFormattableTextComponent) {
            IFormattableTextComponent mutableComponent = (IFormattableTextComponent) component;
            Style newStyle = mutableComponent.getStyle();
            if (level >= 25) {
                newStyle = component.getStyle().applyFormatting(TextFormatting.RED);
            } else if (level >= 20) {
                newStyle = component.getStyle().applyFormatting(TextFormatting.LIGHT_PURPLE);
            } else if (level >= 15) {
                newStyle = component.getStyle().applyFormatting(TextFormatting.YELLOW);
            } else if (level >= 10) {
                newStyle = component.getStyle().applyFormatting(TextFormatting.BLUE);
            } else if (level > 0 && eneng) {
                newStyle = component.getStyle().applyFormatting(TextFormatting.GREEN);
            } else if (eneng) {
                newStyle = component.getStyle().applyFormatting(TextFormatting.AQUA);
            }
            mutableComponent.setStyle(newStyle);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        World level = entity.world;

        if (!level.isRemote) {
            if (entity instanceof MobEntity
                    && event.getSource().getImmediateSource() instanceof PlayerEntity) {
                Optional<Item> item =
                        ConfigRegistry.stoneDropoutProbabilityConfig.dropStone(
                                DropTypeEnum.MOB_KILLED, entity.getType());
                if (item.isPresent()) {
                    Item stone = item.get();
                    ItemStack stoneItemStack = new ItemStack(stone);
                    ItemEntity itemEntity =
                            new ItemEntity(
                                    entity.world,
                                    entity.getPosX(),
                                    entity.getPosY(),
                                    entity.getPosZ(),
                                    stoneItemStack);

                    event.getDrops().add(itemEntity);
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        World level = event.getEntityLiving().world;

        if (level.isRemote) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
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
        Advancement advancement = server.getAdvancementManager().getAdvancement(advancementId);
        if (advancement != null) {
            AdvancementProgress progress = advancements.getProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemaningCriteria()) {
                    advancements.grantCriterion(advancement, criterion);
                }
            }
        }
    }

    private static void syncRecipeBookDisplayRecipes(ServerPlayerEntity player) {
        player.unlockRecipes(DISPLAY_RECIPE_KEYS);

        List<net.minecraft.item.crafting.IRecipe<?>> legacyRecipes = new ArrayList<>();
        for (ResourceLocation recipeKey : LEGACY_RECIPE_KEYS) {
            player.getServer().getRecipeManager().getRecipe(recipeKey).ifPresent(legacyRecipes::add);
        }

        if (!legacyRecipes.isEmpty()) {
            player.resetRecipes(legacyRecipes);
        }
    }

    private static ResourceLocation recipeKey(String path) {
        return Intensify.location(path);
    }
}
