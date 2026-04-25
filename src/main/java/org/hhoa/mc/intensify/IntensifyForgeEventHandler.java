package org.hhoa.mc.intensify;

import static org.hhoa.mc.intensify.Intensify.FIRST_LOGIN_CAPABILITY;
import static org.hhoa.mc.intensify.config.IntensifyConstants.LIMITED_REPLACED_BLOCKS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.hhoa.mc.intensify.capabilities.FirstLoginCapabilityProvider;
import org.hhoa.mc.intensify.capabilities.IFirstLoginCapability;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.data.ChunkBlockDataStorage;
import org.hhoa.mc.intensify.enums.DropTypeEnum;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.registry.AdvancementRegistry;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.util.PlayerUtils;

public class IntensifyForgeEventHandler {
    public static final ResourceLocation FIRST_LOGIN_CAP =
            Intensify.location("first_login_capability");
    private static final List<IntensifyStoneType> MINING_DROP_STONE_TYPES =
            Arrays.asList(
                    IntensifyStoneType.STRENGTHENING_STONE,
                    IntensifyStoneType.ENENG_STONE,
                    IntensifyStoneType.ETERNAL_STONE,
                    IntensifyStoneType.PROTECTION_STONE);

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        TileEntity blockEntity = event.getWorld().getTileEntity(event.getPos());
        if (blockEntity instanceof TileEntityFurnace) {
            NBTTagCompound persistentData = blockEntity.getTileData();
            EntityPlayer player = event.getEntityPlayer();
            persistentData.setString(
                    IntensifyConstants.FURNACE_OWNER_TAG_ID, player.getName());
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote
                || !LIMITED_REPLACED_BLOCKS.contains(event.getPlacedBlock().getBlock())) {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        ChunkBlockDataStorage.getOrCreate(world, pos).setBlockData(pos, true);
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        BlockPos pos = event.getPos();
        ChunkBlockDataStorage storage = ChunkBlockDataStorage.getOrCreate(world, pos);
        boolean playerPlaced = storage.getBlockData(pos);

        List<ItemStack> stoneDrops =
                createMiningStoneDrops(
                        event.getState(),
                        event.getHarvester() instanceof EntityPlayerMP,
                        event.isSilkTouching(),
                        playerPlaced);
        if (playerPlaced) {
            storage.setBlockData(pos, false);
        }
        event.getDrops().addAll(stoneDrops);
        if (event.getHarvester() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getHarvester();
            for (ItemStack stoneDrop : stoneDrops) {
                WorldAnnouncements.announceMiningDrop(player, stoneDrop);
            }
        }
    }

    static List<ItemStack> createMiningStoneDrops(
            IBlockState state, boolean playerHarvested, boolean silkTouching, boolean playerPlaced) {
        List<ItemStack> drops = new ArrayList<>();
        if (!playerHarvested || silkTouching || playerPlaced) {
            return drops;
        }
        for (IntensifyStoneType stoneType : MINING_DROP_STONE_TYPES) {
            Optional<Item> item =
                    ConfigRegistry.stoneDropoutProbabilityConfig.dropStone(
                            stoneType, DropTypeEnum.MINERAL_BLOCK_DESTROYED, state.getBlock());
            item.map(ItemStack::new).ifPresent(drops::add);
        }
        return drops;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
            IFirstLoginCapability capability = player.getCapability(FIRST_LOGIN_CAPABILITY, null);
            if (capability != null && !capability.hasLoggedIn()) {
                completeAdvancement(
                        serverPlayer.getAdvancements(),
                        serverPlayer.getServer(),
                        AdvancementRegistry.INTENSIFY_ADVANCEMENT_ID);
                capability.setHasLoggedIn(true);
            }
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(FIRST_LOGIN_CAP, new FirstLoginCapabilityProvider());
        }
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player instanceof EntityPlayerMP && !event.getDrops().isEmpty()) {
            Optional<Item> item =
                    ConfigRegistry.stoneDropoutProbabilityConfig.dropStone(
                            DropTypeEnum.FISHING, event.getDrops().get(0));
            if (item.isPresent()) {
                event.getDrops().add(new ItemStack(item.get()));
            }
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation registryName =
                ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        List<String> toolTip = event.getToolTip();
        if (registryName != null) {
            ItemStack itemStack = event.getItemStack();
            ToolIntensifyConfig toolIntensifyConfig =
                    IntensifyConfig.getToolIntensifyConfig(itemStack.getItem());
            if (toolIntensifyConfig != null) {
                modifyToolTip(itemStack, toolTip);
            }
        }
    }

    private static void modifyToolTip(ItemStack itemStack, List<String> toolTip) {
        if (toolTip.isEmpty()) {
            return;
        }
        int level = IntensifyConfig.getEnhancementIntensifySystem().getLevel(itemStack);
        boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(itemStack);
        String component = toolTip.get(0);
        if (level > 0) {
            component = component + "+" + level;
        } else if (eneng) {
            component = component + "*";
        }
        if (level >= 25) {
            component = TextFormatting.RED + component;
        } else if (level >= 20) {
            component = TextFormatting.LIGHT_PURPLE + component;
        } else if (level >= 15) {
            component = TextFormatting.YELLOW + component;
        } else if (level >= 10) {
            component = TextFormatting.BLUE + component;
        } else if (level > 0 && eneng) {
            component = TextFormatting.GREEN + component;
        } else if (eneng) {
            component = TextFormatting.AQUA + component;
        }
        toolTip.set(0, component);
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World level = entity.world;

        if (!level.isRemote) {
            Optional<ItemStack> itemStack =
                    createMobKillStoneDrop(
                            entity,
                            entity instanceof EntityLiving,
                            event.getSource().getImmediateSource() instanceof EntityPlayer);
            if (itemStack.isPresent()) {
                EntityItem itemEntity =
                        new EntityItem(
                                entity.world,
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                itemStack.get());
                event.getDrops().add(itemEntity);
            }
        }
    }

    static Optional<ItemStack> createMobKillStoneDrop(
            Object lookupKey, boolean livingMob, boolean directlyKilledByPlayer) {
        if (!livingMob || !directlyKilledByPlayer) {
            return Optional.empty();
        }
        return ConfigRegistry.stoneDropoutProbabilityConfig
                .dropStone(DropTypeEnum.MOB_KILLED, lookupKey)
                .map(ItemStack::new);
    }

    @SubscribeEvent
    public void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        World level = event.player.world;

        if (level.isRemote || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ItemStack smelting = event.smelting;
        if (player.getServer() != null) {
            boolean eneng = IntensifyConfig.getEnengIntensifySystem().isEneng(smelting);
            if (eneng && player.getServer() != null) {
                completeAdvancement(
                        player.getAdvancements(),
                        player.getServer(),
                        AdvancementRegistry.FIRST_ENENG_ADVANCEMENT_ID);
            }
            int itemLevel = IntensifyConfig.getEnhancementIntensifySystem().getLevel(smelting);
            if (itemLevel > 0) {
                completeAdvancement(
                        player.getAdvancements(),
                        player.getServer(),
                        AdvancementRegistry.FIRST_STRENGTHENING_ADVANCEMENT_ID);
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
}
