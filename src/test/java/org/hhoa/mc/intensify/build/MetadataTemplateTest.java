package org.hhoa.mc.intensify.build;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class MetadataTemplateTest {
    @Test
    void neoforgeMetadataTemplateExistsAndDeclaresCoreDependencies() throws Exception {
        Path template = Path.of("src/main/templates/META-INF/neoforge.mods.toml");
        assertTrue(Files.exists(template), "missing neoforge metadata template");

        String text = Files.readString(template);
        assertTrue(text.contains("modLoader=\"javafml\""));
        assertTrue(text.contains("modId=\"${mod_id}\""));
        assertTrue(text.contains("modId=\"attributeslib\""));
    }
}
