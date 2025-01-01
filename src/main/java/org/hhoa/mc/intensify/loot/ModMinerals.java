package org.hhoa.mc.intensify.loot;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ModMinerals {
    public static final Map<Block,  Double> MINERAL_BLOCKS_PROBABILITY = new HashMap<>();
    public static final Map<EntityType<?>,  Double> MOB_PROBABILITY= new HashMap<>();
    public static final Map<Item,  Double> FISH_PROBABILITY= new HashMap<>();

    //block:
    //  # 金
    //  GOLD_ORE: 0.01
    //  # 铁
    //  IRON_ORE: 0.01
    //  # 煤
    //  COAL_ORE: 0.001
    //  # 下界金
    //  NETHER_GOLD_ORE: 0.01
    //  # 青金石
    //  LAPIS_ORE: 0.001
    //  # 钻石
    //  DIAMOND_ORE: 0.1
    //  # 红石
    //  REDSTONE_ORE: 0.001
    //  # 绿宝石
    //  EMERALD_ORE: 0.1
    //  # 下界石英
    //  NETHER_QUARTZ_ORE: 0.001
    //fish:
    //  # 生鳕鱼
    //  COD: 0.01
    //  # 生硅鱼
    //  SALMON: 0.01
    //  # 热带鱼
    //  TROPICAL_FISH: 0.01
    //  # 河豚
    //  PUFFERFISH: 0.01
    //
    //# 击杀怪物掉落炉岩碳的概率
    //# 其他怪物可以自己添加，格式 怪物key: 概率
    //kill:
    //  # 全部怪物概率
    //  monster: 0.01
    //  # 僵尸概率-可自定义扩展单个怪物的概率配置, 怪物类型需小写
    //  zombie: 0.1
    static {
        // 主世界
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.COAL_ORE, 0.001);      // 煤矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_COAL_ORE, 0.0015); // 深层煤矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.COPPER_ORE, 0.04);      // 铜矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_COPPER_ORE, 0.45);      // 深层铜矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.IRON_ORE, 0.01);      // 铁矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_IRON_ORE, 0.015); // 深层铁矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.GOLD_ORE, 0.015);      // 金矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_GOLD_ORE, 0.015); // 深层金矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.REDSTONE_ORE, 0.002);  // 红石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_REDSTONE_ORE, 0.0025); // 深层红石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.LAPIS_ORE, 0.002);     // 青金石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_LAPIS_ORE, 0.002); // 深层青金石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DIAMOND_ORE, 0.08);   // 钻石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_DIAMOND_ORE, 0.085); // 深层钻石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.EMERALD_ORE, 0.08);   // 绿宝石矿
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.DEEPSLATE_EMERALD_ORE, 0.085); // 深层绿宝石矿
        // 下界
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.NETHER_QUARTZ_ORE, 0.001); // 石英矿石
        MINERAL_BLOCKS_PROBABILITY.put(Blocks.ANCIENT_DEBRIS, 0.2); // 远古残骸


        // 钓鱼
        FISH_PROBABILITY.put(Items.COD, 0.01);
        FISH_PROBABILITY.put(Items.SALMON, 0.01);
        FISH_PROBABILITY.put(Items.TROPICAL_FISH, 0.01);
        FISH_PROBABILITY.put(Items.PUFFERFISH, 0.01);
        // 钓到其他 0.01


        MOB_PROBABILITY.put(EntityType.ZOMBIE, 0.01);
        // 其他 0.01
    }
}
