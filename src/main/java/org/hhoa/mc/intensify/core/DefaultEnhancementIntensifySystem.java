package org.hhoa.mc.intensify.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;
import org.hhoa.mc.intensify.util.PlayerUtils;
import org.hhoa.mc.intensify.util.WorldAnnouncements;
import org.jetbrains.annotations.NotNull;

public class DefaultEnhancementIntensifySystem extends EnhancementIntensifySystem {
    private final double baseSuccessProbability; // 初始基础成功概率
    private final double probabilityIncreaseOnFailure; // 每次失败增加的成功概率
    private final double maxSuccessProbability; // 最大成功概率
    private final double failedDowngradeMinLevel; // 失败降级的最低等级
    private final double alpha; // 控制成功概率下降的速度

    public DefaultEnhancementIntensifySystem(
            double baseSuccessProbability,
            double probabilityIncreaseOnFailure,
            double maxSuccessProbability,
            double failedDowngradeMinLevel,
            double alpha) {
        this.failedDowngradeMinLevel = failedDowngradeMinLevel;
        this.alpha = alpha;
        this.baseSuccessProbability = baseSuccessProbability;
        this.probabilityIncreaseOnFailure = probabilityIncreaseOnFailure;
        this.maxSuccessProbability = maxSuccessProbability;
    }

    /**
     * 计算当前强化的成功概率
     *
     * @return 当前强化的成功概率
     */
    private double calculateSuccessProbability(int level, int failCount) {
        double currentBaseProbability = baseSuccessProbability / Math.sqrt(1 + alpha * level);

        double totalProbability = currentBaseProbability + failCount * probabilityIncreaseOnFailure;

        return Math.min(totalProbability, maxSuccessProbability)
                * ConfigRegistry.UPGRADE_MULTIPLIER.get();
    }

    @Override
    public void intensify(
            ServerPlayer player, ItemStack itemStack, ToolIntensifyConfig intensifyConfig) {
        EquipmentSlot equipmentSlotForItem = LivingEntity.getEquipmentSlotForItem(itemStack);
        CompoundTag tag = itemStack.getOrCreateTag();
        int currentLevel = getLevel(itemStack);
        int currentFailuresCount = getFailuresCount(tag);
        EnhanceResult enhanceResult = enhance(currentLevel, currentFailuresCount);
        if (enhanceResult == EnhanceResult.NOT_CHANGE) {
            setFailuresCount(itemStack.getOrCreateTag(), currentFailuresCount + 1);
            sendMessage(
                    player,
                    TranslatableTexts.STRENGTHENING_UNCHANGED.get(
                            currentLevel, currentFailuresCount + 1));
        }

        if (enhanceResult != EnhanceResult.NOT_CHANGE) {
            boolean needProtect = false;
            if (enhanceResult == EnhanceResult.DOWNGRADE) {
                needProtect = PlayerUtils.hasItem(player, ItemRegistry.PROTECTION_STONE.get());
            }
            if (needProtect) {
                PlayerUtils.removeSingleItemFromPlayer(
                        player, ItemRegistry.PROTECTION_STONE.get(), 1);
                setFailuresCount(itemStack.getOrCreateTag(), currentFailuresCount + 1);
                sendMessage(
                        player,
                        TranslatableTexts.STRENGTHENING_PROTECTED.get(
                                currentLevel, currentFailuresCount + 1));
            } else {
                List<ToolIntensifyConfig.AttributeConfig> attributes =
                        intensifyConfig.getAttributes();
                for (ToolIntensifyConfig.AttributeConfig attribute : attributes) {
                    Attribute type = attribute.getType();
                    List<ToolIntensifyConfig.GrowConfig> grows = attribute.getGrows();
                    for (ToolIntensifyConfig.GrowConfig grow : grows) {
                        Double value = randomizeAndMultiply(grow.getValue());
                        ToolIntensifyConfig.GrowTypeEnum growType = grow.getType();
                        int speed = grow.getSpeed();
                        Range<Integer> range = grow.getRange();

                        if (range.contains(currentLevel + 1)
                                && (((currentLevel + 1) - range.lowerEndpoint()) % speed) == 0
                                && enhanceResult == EnhanceResult.UPGRADE) {
                            upgradeAttribute(
                                    itemStack, type, value, growType, equipmentSlotForItem);
                        } else if (range.contains(currentLevel - 1)
                                && (((currentLevel - 1) - range.lowerEndpoint()) % speed) == 0
                                && enhanceResult == EnhanceResult.DOWNGRADE) {
                            downgradeAttribute(
                                    itemStack, type, value, growType, equipmentSlotForItem);
                        }
                    }
                }
                if (enhanceResult == EnhanceResult.UPGRADE) {
                    int nextLevel = currentLevel + 1;
                    setLevel(itemStack.getOrCreateTag(), nextLevel);
                    setFailuresCount(itemStack.getOrCreateTag(), 0);
                    sendMessage(
                            player, TranslatableTexts.STRENGTHENING_UPGRADE.get(currentLevel + 1));
                    WorldAnnouncements.announceStrengthening(player, itemStack, nextLevel);
                } else if (enhanceResult == EnhanceResult.DOWNGRADE) {
                    int nextLevel = currentLevel - 1;
                    setLevel(itemStack.getOrCreateTag(), nextLevel);
                    setFailuresCount(itemStack.getOrCreateTag(), currentFailuresCount + 1);
                    sendMessage(
                            player,
                            TranslatableTexts.STRENGTHENING_DOWNGRADE.get(
                                    nextLevel, currentFailuresCount + 1));
                }
            }
        }
    }

    private Double randomizeAndMultiply(Double value) {
        value = value * ConfigRegistry.ATTRIBUTE_MULTIPLIER.get();
        double lower = value * 0.1;
        double upper = value * (1 + 0.1);
        return ThreadLocalRandom.current().nextDouble(lower, upper);
    }

    private static void sendMessage(ServerPlayer player, String currentLevel) {
        if (player != null) {
            player.sendSystemMessage(Component.literal(currentLevel));
        }
    }

    private void downgradeAttribute(
            ItemStack itemStack,
            Attribute type,
            Double decreaseValue,
            ToolIntensifyConfig.GrowTypeEnum growType,
            EquipmentSlot equipmentSlotForItem) {
        List<AttributeModifier> oldModifiers =
                getAttributeModifiers(itemStack, type, equipmentSlotForItem);
        for (AttributeModifier oldAttributeModifier : oldModifiers) {
            double amount = oldAttributeModifier.getAmount();
            double newValue = 0;
            if (growType == ToolIntensifyConfig.GrowTypeEnum.FIXED) {
                newValue = amount - decreaseValue;
            } else if (growType == ToolIntensifyConfig.GrowTypeEnum.PROPORTIONAL) {
                newValue = amount / (1 + decreaseValue);
            }
            AttributeModifier newAttributeModifier =
                    new AttributeModifier(
                            getAttributeModifierName(type),
                            newValue,
                            AttributeModifier.Operation.ADDITION);
            ItemModifierHelper.removeAttributeModifier(itemStack, oldAttributeModifier.getId());
            itemStack.addAttributeModifier(type, newAttributeModifier, equipmentSlotForItem);
        }
    }

    private void upgradeAttribute(
            ItemStack itemStack,
            Attribute type,
            Double incrementValue,
            ToolIntensifyConfig.GrowTypeEnum growType,
            EquipmentSlot equipmentSlotForItem) {
        List<AttributeModifier> oldModifiers =
                getAttributeModifiers(itemStack, type, equipmentSlotForItem);

        if (oldModifiers.isEmpty()) {
            AttributeModifier attributeModifier =
                    new AttributeModifier(
                            getAttributeModifierName(type),
                            incrementValue,
                            AttributeModifier.Operation.ADDITION);
            itemStack.addAttributeModifier(type, attributeModifier, equipmentSlotForItem);
        } else {
            for (AttributeModifier oldAttributeModifier : oldModifiers) {
                double amount = oldAttributeModifier.getAmount();
                double newValue = 0;
                if (growType == ToolIntensifyConfig.GrowTypeEnum.FIXED) {
                    newValue = amount + incrementValue;
                } else if (growType == ToolIntensifyConfig.GrowTypeEnum.PROPORTIONAL) {
                    newValue = amount * (1 + incrementValue);
                }
                AttributeModifier newAttributeModifier =
                        new AttributeModifier(
                                getAttributeModifierName(type),
                                newValue,
                                AttributeModifier.Operation.ADDITION);
                ItemModifierHelper.removeAttributeModifier(itemStack, oldAttributeModifier.getId());
                itemStack.addAttributeModifier(type, newAttributeModifier, equipmentSlotForItem);
            }
        }
    }

    private @NotNull List<AttributeModifier> getAttributeModifiers(
            ItemStack itemStack, Attribute type, EquipmentSlot equipmentSlotForItem) {
        String attributeModifierName = getAttributeModifierName(type);

        Multimap<Attribute, AttributeModifier> attributeAttributeModifierMultimap =
                itemStack.getAttributeModifiers(equipmentSlotForItem);
        Collection<AttributeModifier> attributeModifiers =
                attributeAttributeModifierMultimap.get(type);
        List<AttributeModifier> oldModifiers = new ArrayList<>();
        for (AttributeModifier attributeModifier : attributeModifiers) {
            String name = attributeModifier.getName();
            if (name.equals(attributeModifierName)) {
                oldModifiers.add(attributeModifier);
            }
        }
        return oldModifiers;
    }

    private int getFailuresCount(CompoundTag tag) {
        return tag.getInt(getTagId("failures_count"));
    }

    @Override
    public int getLevel(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        return tag.getInt(getTagId("level"));
    }

    private void setFailuresCount(CompoundTag tag, int failuresCount) {
        tag.putInt(getTagId("failures_count"), failuresCount);
    }

    private void setLevel(CompoundTag tag, int level) {
        tag.putInt(getTagId("level"), level);
    }

    /**
     * 执行一次强化
     *
     * @return 强化是否成功
     */
    EnhanceResult enhance(int level, int failuresCount) {
        double successAndFailedProbability = calculateSuccessProbability(level, failuresCount);

        double randomValue = Math.random();

        if (randomValue < successAndFailedProbability) {
            return EnhanceResult.UPGRADE;
        }

        randomValue = Math.random();
        if (randomValue < successAndFailedProbability && level > failedDowngradeMinLevel) {
            return EnhanceResult.DOWNGRADE;
        } else {
            return EnhanceResult.NOT_CHANGE;
        }
    }

    public enum EnhanceResult {
        UPGRADE,
        NOT_CHANGE,
        DOWNGRADE
    }
}
