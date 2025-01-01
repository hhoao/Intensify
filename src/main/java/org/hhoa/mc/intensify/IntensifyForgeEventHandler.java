package org.hhoa.mc.intensify;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.config.Config;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.enums.DropTypeEnum;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IntensifyForgeEventHandler {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (blockEntity instanceof FurnaceBlockEntity) {
            CompoundTag persistentData = blockEntity.getPersistentData();
            Player player = event.getEntity();
            persistentData.putString(IntensifyConstants.FURNACE_OWNER_TAG_ID, player.getName().getString());
        }
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        Level world = player.level();

        if (!world.isClientSide) {
            LivingEntity entity = event.getEntity();
            Optional<Item> item = Config.getStoneDropoutProbabilityConfig()
                .dropStone(DropTypeEnum.MOB_KILLED, entity.getType());
            if (item.isPresent()) {
                Item stone = item.get();
                ItemStack stoneItemStack = new ItemStack(stone);
                event.getDrops().add(stoneItemStack);
            }
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) throws IOException {
        ResourceLocation registryName =
            ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        List<Component> toolTip = event.getToolTip();
        if (registryName != null) {
            try {
                ItemStack itemStack = event.getItemStack();
                for (Map.Entry<Class<? extends Item>, ToolIntensifyConfig> classToolIntensifyConfigEntry : ToolIntensifyConfig.getClassConfigMap().entrySet()) {
                    if (itemStack.getItem().getClass().isAssignableFrom(classToolIntensifyConfigEntry.getKey())){
                        int level = Config.getEnhancementIntensifySystem().getLevel(itemStack);
                        boolean eneng = Config.getEnengIntensifySystem().isEneng(itemStack);
                        Component component = toolTip.get(0);
                        if (level > 0) {
                            List<Component> siblings = component.getSiblings();
                            siblings.add(Component.literal(" +" + level));
                        } else if (eneng) {
                            List<Component> siblings = component.getSiblings();
                            siblings.add(Component.literal(" *"));
                        }
                        if (component instanceof MutableComponent) {
                            MutableComponent mutableComponent = (MutableComponent) component;
                            Style newStyle = mutableComponent.getStyle();
                            if (level >= 30) {
                                newStyle = component.getStyle().withColor(ChatFormatting.RED);
                            } else if (level >= 20) {
                                newStyle = component.getStyle().withColor(ChatFormatting.LIGHT_PURPLE);
                            } else if (level >= 15) {
                                newStyle = component.getStyle().withColor(ChatFormatting.YELLOW);
                            } else if (level >= 10) {
                                newStyle =component.getStyle().withColor(ChatFormatting.BLUE);
                            } else if (level > 0 && eneng) {
                                newStyle = component.getStyle().withColor(ChatFormatting.GREEN);
                            }
                            mutableComponent.setStyle(newStyle);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Mob && event.getSource().getEntity() instanceof Player) {
            Optional<Item> item = Config.getStoneDropoutProbabilityConfig()
                .dropStone(DropTypeEnum.MOB_KILLED, entity.getType());
            if (item.isPresent()) {
                Item stone = item.get();
                ItemStack stoneItemStack = new ItemStack(stone);
                ItemEntity itemEntity =
                    new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stoneItemStack);

                event.getDrops().add(itemEntity);
            }
        }
    }
}
