package org.hhoa.mc.intensify.registry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.StoneDropoutProbabilityConfig;

public class ConfigRegistry {
    public static final MutableDoubleValue UPGRADE_MULTIPLIER =
            new MutableDoubleValue("upgrade_multiplier", 1.0D);
    public static final MutableDoubleValue ATTRIBUTE_MULTIPLIER =
            new MutableDoubleValue("attribute_multiplier", 1.0D);
    public static final MutableBooleanValue WORLD_ANNOUNCEMENTS_ENABLED =
            new MutableBooleanValue("world_announcements", true);
    public static final StoneDropoutProbabilityConfig stoneDropoutProbabilityConfig =
            new StoneDropoutProbabilityConfig();
    private static final List<Integer> DEFAULT_STRENGTHENING_ANNOUNCEMENT_LEVELS =
            Collections.unmodifiableList(Arrays.asList(10, 15, 20, 25));
    private static List<Integer> strengtheningAnnouncementLevels =
            new ArrayList<>(DEFAULT_STRENGTHENING_ANNOUNCEMENT_LEVELS);

    private static Configuration commonConfig;

    public static void initialize(File configDirectory) {
        commonConfig = new Configuration(new File(configDirectory, Intensify.MODID + ".cfg"));
        syncCommon();
        stoneDropoutProbabilityConfig.initialize(
                new File(configDirectory, Intensify.MODID + "-probability.cfg"));
    }

    public static void save() {
        saveCommon();
        stoneDropoutProbabilityConfig.save();
    }

    private static void syncCommon() {
        if (commonConfig == null) {
            return;
        }
        commonConfig.load();
        UPGRADE_MULTIPLIER.setInternal(
                commonConfig
                        .get(
                                "general",
                                UPGRADE_MULTIPLIER.getKey(),
                                UPGRADE_MULTIPLIER.getDefaultValue())
                        .getDouble());
        ATTRIBUTE_MULTIPLIER.setInternal(
                commonConfig
                        .get(
                                "general",
                                ATTRIBUTE_MULTIPLIER.getKey(),
                                ATTRIBUTE_MULTIPLIER.getDefaultValue())
                        .getDouble());
        WORLD_ANNOUNCEMENTS_ENABLED.setInternal(
                commonConfig
                        .get(
                                "announcements",
                                WORLD_ANNOUNCEMENTS_ENABLED.getKey(),
                                WORLD_ANNOUNCEMENTS_ENABLED.getDefaultValue())
                        .getBoolean());
        strengtheningAnnouncementLevels =
                parseAnnouncementLevels(
                        commonConfig.getStringList(
                                "strengthening_levels",
                                "announcements",
                                toStringArray(DEFAULT_STRENGTHENING_ANNOUNCEMENT_LEVELS),
                                "Strengthening levels that trigger a world announcement."));
        saveCommon();
    }

    public static List<Integer> getStrengtheningAnnouncementLevels() {
        return Collections.unmodifiableList(strengtheningAnnouncementLevels);
    }

    private static void saveCommon() {
        if (commonConfig == null) {
            return;
        }
        commonConfig
                .get("general", UPGRADE_MULTIPLIER.getKey(), UPGRADE_MULTIPLIER.getDefaultValue())
                .set(UPGRADE_MULTIPLIER.get());
        commonConfig
                .get(
                        "general",
                        ATTRIBUTE_MULTIPLIER.getKey(),
                        ATTRIBUTE_MULTIPLIER.getDefaultValue())
                .set(ATTRIBUTE_MULTIPLIER.get());
        commonConfig
                .get(
                        "announcements",
                        WORLD_ANNOUNCEMENTS_ENABLED.getKey(),
                        WORLD_ANNOUNCEMENTS_ENABLED.getDefaultValue())
                .set(WORLD_ANNOUNCEMENTS_ENABLED.get());
        if (commonConfig.hasChanged()) {
            commonConfig.save();
        }
    }

    private static List<Integer> parseAnnouncementLevels(String[] values) {
        List<Integer> levels = new ArrayList<>();
        for (String value : values) {
            try {
                levels.add(Integer.parseInt(value.trim()));
            } catch (NumberFormatException ignored) {
                // Ignore malformed entries so one typo does not disable announcements.
            }
        }
        return levels.isEmpty()
                ? new ArrayList<>(DEFAULT_STRENGTHENING_ANNOUNCEMENT_LEVELS)
                : levels;
    }

    private static String[] toStringArray(List<Integer> values) {
        String[] result = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = String.valueOf(values.get(i));
        }
        return result;
    }

    public static final class MutableDoubleValue {
        private final String key;
        private final double defaultValue;
        private double value;

        public MutableDoubleValue(String key, double defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public String getKey() {
            return this.key;
        }

        public double getDefaultValue() {
            return this.defaultValue;
        }

        public double get() {
            return this.value;
        }

        public void set(double value) {
            this.value = value;
        }

        void setInternal(double value) {
            this.value = value;
        }
    }

    public static final class MutableBooleanValue {
        private final String key;
        private final boolean defaultValue;
        private boolean value;

        public MutableBooleanValue(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public String getKey() {
            return this.key;
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean value) {
            this.value = value;
        }

        void setInternal(boolean value) {
            this.value = value;
        }
    }
}
