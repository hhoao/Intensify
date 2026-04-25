package org.hhoa.mc.intensify.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class MaceSupportTest {
    @Test
    void registersMaceAsIndependentToolType() throws Exception {
        Path constants = Path.of("src/main/java/org/hhoa/mc/intensify/config/IntensifyConstants.java");
        String text = Files.readString(constants);

        assertTrue(text.contains("TOOL_NAME_CLASS_MAPPING.put(\"mace\""), "missing mace tool mapping");
        assertTrue(text.contains("MaceItem.class"), "mace should map to MaceItem");
    }

    @Test
    void shipsDedicatedMaceDefaultConfig() throws Exception {
        Path config = Path.of("src/main/resources/assets/intensify/config/intensify/mace.toml");
        assertTrue(Files.exists(config), "missing mace default config");

        String text = Files.readString(config);
        assertTrue(text.contains("[mace]"), "mace config should use its own top-level section");
    }
}
