package org.hhoa.mc.intensify.registry;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;

public class ConfigRegistry {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Double> UPGRADE_MULTIPLIER =
            BUILDER.define("upgrade_multiplier", 1.0);

    public static final ModConfigSpec.ConfigValue<Double> ATTRIBUTE_MULTIPLIER =
            BUILDER.define("attribute_multiplier", 1.0);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static final Pair<ModConfigSpec, StoneDropoutProbabilityConfig>
            configSpecStoneDropoutProbabilityConfigPair = StoneDropoutProbabilityConfig.create();
    public static final StoneDropoutProbabilityConfig stoneDropoutProbabilityConfig =
            configSpecStoneDropoutProbabilityConfigPair.getRight();

    public static void initialize(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SPEC);
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                configSpecStoneDropoutProbabilityConfigPair.getLeft(),
                "probability.toml");
    }
}
