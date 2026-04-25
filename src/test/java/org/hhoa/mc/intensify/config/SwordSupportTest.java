package org.hhoa.mc.intensify.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SwordSupportTest {
    @Test
    void swordMappingNoLongerRejectsToolComponentCarriers() throws Exception {
        Path constants = Path.of("src/main/java/org/hhoa/mc/intensify/config/IntensifyConstants.java");
        String text = Files.readString(constants);

        assertTrue(text.contains("TOOL_NAME_CLASS_MAPPING.put(\n                \"sword\""), "missing sword tool mapping");
        assertFalse(
                text.contains("&& !item.components().has(DataComponents.TOOL)"),
                "sword mapping must not reject swords just because they also carry TOOL data");
    }
}
