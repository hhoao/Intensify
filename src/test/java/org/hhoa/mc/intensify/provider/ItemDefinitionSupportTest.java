package org.hhoa.mc.intensify.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ItemDefinitionSupportTest {
    @Test
    void shipsItemDefinitionsForAllIntensifyStones() throws Exception {
        assertItemDefinitionExists("strengthening_stone");
        assertItemDefinitionExists("eneng_stone");
        assertItemDefinitionExists("protection_stone");
        assertItemDefinitionExists("eternal_stone");
    }

    private static void assertItemDefinitionExists(String itemName) throws Exception {
        Path itemDefinition =
                Path.of("src/generated/resources/assets/intensify/items/" + itemName + ".json");
        assertTrue(Files.exists(itemDefinition), "missing item definition for " + itemName);

        String text = Files.readString(itemDefinition);
        assertTrue(
                text.contains("\"model\": \"intensify:item/" + itemName + "\""),
                "item definition should point at intensify item model for " + itemName);
    }
}
