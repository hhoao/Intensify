package org.hhoa.mc.intensify.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.util.ItemModifierHelper;
import org.hhoa.mc.intensify.util.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultEnhancementIntensifySystem extends EnhancementIntensifySystem {
    private final double baseSuccessProbability; // 初始基础成功概率
    private final double baseDowngradeProbability; // 初始基础降级概率
    private final double probabilityIncreaseOnFailure; // 每次失败增加的成功概率
    private final double maxSuccessProbability; // 最大成功概率
    private final double maxFailedProbability;
    private final double alpha; // 控制成功概率下降的速度
    private final double beta; // 降级概率公式控制参数


    public DefaultEnhancementIntensifySystem(double baseSuccessProbability,
                                             double probabilityIncreaseOnFailure,
                                             double maxSuccessProbability,
                                             double maxFailedProbability,
                                             double baseDowngradeProbability,
                                             double beta,
                                             double alpha) {
        this.maxFailedProbability = maxFailedProbability;
        this.alpha = alpha;
        this.baseSuccessProbability = baseSuccessProbability;
        this.probabilityIncreaseOnFailure = probabilityIncreaseOnFailure;
        this.maxSuccessProbability = maxSuccessProbability;
        this.baseDowngradeProbability = baseDowngradeProbability;
        this.beta = beta;
    }

    /**
     * 计算当前强化的成功概率
     * @return 当前强化的成功概率
     */
    private double calculateSuccessProbability(int level, int failCount) {
        double currentBaseProbability = baseSuccessProbability / Math.sqrt(1 + alpha * level);

        double totalProbability = currentBaseProbability + failCount * probabilityIncreaseOnFailure;

        return Math.min(totalProbability, maxSuccessProbability);
    }

    @Override
    public void intensify(ServerPlayer player, ItemStack itemStack,
                          ToolIntensifyConfig intensifyConfig) {
        EquipmentSlot equipmentSlotForItem = LivingEntity.getEquipmentSlotForItem(itemStack);
        CompoundTag tag = itemStack.getOrCreateTag();
        int currentLevel = getLevel(itemStack);
        int currentFailuresCount = getFailuresCount(tag);
        EnhanceResult enhanceResult = enhance(currentLevel, currentFailuresCount);
        if (enhanceResult == EnhanceResult.NOT_CHANGE) {
            setFailuresCount(itemStack.getOrCreateTag(), currentFailuresCount + 1);
            sendMessage(player, String.format("强化失败!!!，保留等级，当前等级为: %s, 当前失败次数为: %s", currentLevel, currentFailuresCount + 1));
        }

        if (enhanceResult != EnhanceResult.NOT_CHANGE) {
            boolean needProtect = false;
            if (enhanceResult == EnhanceResult.DOWNGRADE) {
                needProtect = PlayerUtils.hasItem(player, ItemRegistry.PROTECTION_STONE.get());
            }
            if (needProtect) {
                PlayerUtils.removeSingleItemFromPlayer(player, ItemRegistry.PROTECTION_STONE.get(), 1);
                setFailuresCount(itemStack.getOrCreateTag(), currentFailuresCount + 1);
                sendMessage(player, String.format("强化失败!!!，等级被保护，当前等级为: %s, 当前失败次数为: %s", currentLevel, currentFailuresCount + 1));
            } else {
                List<ToolIntensifyConfig.AttributeConfig> attributes = intensifyConfig.getAttributes();
                for (ToolIntensifyConfig.AttributeConfig attribute : attributes) {
                    Attribute type = attribute.getType();
                    List<ToolIntensifyConfig.GrowConfig> grows = attribute.getGrows();
                    for (ToolIntensifyConfig.GrowConfig grow : grows) {
                        Double value = grow.getValue();
                        ToolIntensifyConfig.GrowTypeEnum growType = grow.getType();
                        Range<Integer> range = grow.getRange();

                        if (range.contains(currentLevel + 1) && enhanceResult == EnhanceResult.UPGRADE) {
                            upgradeAttribute(itemStack, type, value, growType, equipmentSlotForItem);
                        } else if (range.contains(currentLevel - 1) && enhanceResult == EnhanceResult.DOWNGRADE) {
                            downgradeAttribute(itemStack, type, value, growType, equipmentSlotForItem);
                        }
                    }
                }
                if (enhanceResult == EnhanceResult.UPGRADE) {
                    int nextLevel = currentLevel + 1;
                    setLevel(itemStack.getOrCreateTag(), nextLevel);
                    setFailuresCount(itemStack.getOrCreateTag(), 0);
                    sendMessage(player, "强化成功!!!，等级提升, 当前等级为 :" + (currentLevel + 1));
                } else if (enhanceResult == EnhanceResult.DOWNGRADE) {
                    int nextLevel = currentLevel - 1;
                    setLevel(itemStack.getOrCreateTag(), nextLevel);
                    setFailuresCount(itemStack.getOrCreateTag(), currentFailuresCount + 1);
                    sendMessage(player, String.format("强化失败!!!，降低等级，当前等级为: %s, 当前失败次数为: %s", nextLevel, currentFailuresCount + 1));
                }
            }
        }
    }


    private static void sendMessage(ServerPlayer player, String currentLevel) {
        if (player != null) {
            player.sendSystemMessage(Component.literal(
                currentLevel));
        }
    }

    private void downgradeAttribute(ItemStack itemStack,
                                    Attribute type,
                                    Double decreaseValue,
                                    ToolIntensifyConfig.GrowTypeEnum growType,
                                    EquipmentSlot equipmentSlotForItem) {
        List<AttributeModifier> oldModifiers = getAttributeModifiers(itemStack, type, equipmentSlotForItem);
        for (AttributeModifier oldAttributeModifier : oldModifiers) {
            double amount = oldAttributeModifier.getAmount();
            double newValue = 0;
            if (growType == ToolIntensifyConfig.GrowTypeEnum.FIXED) {
                newValue = amount - decreaseValue;
            } else if (growType == ToolIntensifyConfig.GrowTypeEnum.PROPORTIONAL) {
                newValue = amount / (1 + decreaseValue);
            }
            AttributeModifier newAttributeModifier = new AttributeModifier(
                getAttributeModifierName(type),
                newValue,
                AttributeModifier.Operation.ADDITION);
            ItemModifierHelper.removeAttributeModifier(itemStack, oldAttributeModifier.getId());
            itemStack.addAttributeModifier(
                type,
                newAttributeModifier,
                equipmentSlotForItem);
        }

    }

    private void upgradeAttribute(ItemStack itemStack,
                                  Attribute type,
                                  Double incrementValue,
                                  ToolIntensifyConfig.GrowTypeEnum growType,
                                  EquipmentSlot equipmentSlotForItem) {
        List<AttributeModifier> oldModifiers = getAttributeModifiers(itemStack, type, equipmentSlotForItem);

        for (AttributeModifier oldAttributeModifier : oldModifiers) {
            double amount = oldAttributeModifier.getAmount();
            double newValue = 0;
            if (growType == ToolIntensifyConfig.GrowTypeEnum.FIXED) {
                newValue = amount + incrementValue;
            } else if (growType == ToolIntensifyConfig.GrowTypeEnum.PROPORTIONAL) {
                newValue = amount * (1 + incrementValue);
            }
            AttributeModifier newAttributeModifier = new AttributeModifier(
                getAttributeModifierName(type),
                newValue,
                AttributeModifier.Operation.ADDITION);
            ItemModifierHelper.removeAttributeModifier(itemStack, oldAttributeModifier.getId());
            itemStack.addAttributeModifier(
                type,
                newAttributeModifier,
                equipmentSlotForItem);
        }
    }

    private @NotNull List<AttributeModifier> getAttributeModifiers(ItemStack itemStack, Attribute type, EquipmentSlot equipmentSlotForItem) {
        String attributeModifierName = getAttributeModifierName(type);

        Multimap<Attribute, AttributeModifier> attributeAttributeModifierMultimap =
            itemStack.getAttributeModifiers(equipmentSlotForItem);
        Collection<AttributeModifier> attributeModifiers = attributeAttributeModifierMultimap.get(type);
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
        double successProbability = calculateSuccessProbability(level, failuresCount);
        double downgradeProbability = calculateDowngradeProbability(level);

        double randomValue = Math.random();

        if (randomValue < successProbability) {
            return EnhanceResult.UPGRADE;
        }

        randomValue = Math.random();
        if (randomValue < downgradeProbability) {
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

    /**
     * 计算当前强化的降级概率
     * @return 当前强化的降级概率
     */
    private double calculateDowngradeProbability(int level) {
        return Math.min(maxFailedProbability, baseDowngradeProbability * Math.pow(1 + level, beta));
    }
}
