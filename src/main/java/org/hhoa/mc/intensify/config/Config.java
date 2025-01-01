package org.hhoa.mc.intensify.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.hhoa.mc.intensify.core.DefaultEnengIntensifySystem;
import org.hhoa.mc.intensify.core.DefaultEnhancementIntensifySystem;
import org.hhoa.mc.intensify.core.EnengIntensifySystem;
import org.hhoa.mc.intensify.core.EnhancementIntensifySystem;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec SPEC = BUILDER.build();
    private static DefaultEnengIntensifySystem defaultEnengIntensifySystem;
    private static DefaultEnhancementIntensifySystem defaultEnhancementIntensifySystem;
    public static final int BURN_TIME = 5;
    private static StoneDropoutProbabilityConfig stoneDropoutProbabilityConfig;

    public static StoneDropoutProbabilityConfig getStoneDropoutProbabilityConfig() {
        return stoneDropoutProbabilityConfig;
    }

    public static EnengIntensifySystem getEnengIntensifySystem() {
        return defaultEnengIntensifySystem;
    }

    public static EnhancementIntensifySystem getEnhancementIntensifySystem() {
        return defaultEnhancementIntensifySystem;
    }


    public static void initialize(FMLJavaModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, SPEC);
        Pair<ForgeConfigSpec, StoneDropoutProbabilityConfig> configSpecStoneDropoutProbabilityConfigPair =
            StoneDropoutProbabilityConfig.create();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, configSpecStoneDropoutProbabilityConfigPair.getLeft(),
            "probability.toml");
        stoneDropoutProbabilityConfig = configSpecStoneDropoutProbabilityConfigPair.getRight();
        defaultEnengIntensifySystem =
            new DefaultEnengIntensifySystem();
        defaultEnhancementIntensifySystem =
            new DefaultEnhancementIntensifySystem(
                0.8,
                0.03,
                0.9,
                0.6,
                0.1,
                0.6,
                0.5);
    }
}
