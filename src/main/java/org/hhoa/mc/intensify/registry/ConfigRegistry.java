package org.hhoa.mc.intensify.registry;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;

public class ConfigRegistry {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Double> UPGRADE_MULTIPLIER =
            BUILDER.define("upgrade_multiplier", 1.0);

    public static final ForgeConfigSpec.ConfigValue<Double> ATTRIBUTE_MULTIPLIER =
            BUILDER.define("attribute_multiplier", 1.0);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static final Pair<ForgeConfigSpec, StoneDropoutProbabilityConfig>
            configSpecStoneDropoutProbabilityConfigPair = StoneDropoutProbabilityConfig.create();
    public static final StoneDropoutProbabilityConfig stoneDropoutProbabilityConfig =
            configSpecStoneDropoutProbabilityConfigPair.getRight();

    public static void initialize(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, SPEC);
        modLoadingContext.registerConfig(
                ModConfig.Type.COMMON,
                configSpecStoneDropoutProbabilityConfigPair.getLeft(),
                "probability.toml");
    }
}
