package org.hhoa.mc.intensify.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class ItemModelResourcesTest {

    private static final List<String> TOOL_CONFIGS =
            Arrays.asList(
                    "axe",
                    "boots",
                    "bow",
                    "chestplate",
                    "crossbow",
                    "elytra",
                    "fishing_rod",
                    "helmet",
                    "hoe",
                    "leggings",
                    "pickaxe",
                    "shield",
                    "shovel",
                    "sword",
                    "trident");

    @Test
    public void bundledStoneItemModelsExist() {
        assertResource("assets/intensify/models/item/strengthening_stone.json");
        assertResource("assets/intensify/models/item/eneng_stone.json");
        assertResource("assets/intensify/models/item/protection_stone.json");
        assertResource("assets/intensify/models/item/eternal_stone.json");
    }

    @Test
    public void bundledConfigsDoNotReferenceUnsupportedVanillaAttributes() throws IOException {
        for (String config : TOOL_CONFIGS) {
            String path = "assets/intensify/config/intensify/" + config + ".toml";
            try (InputStream stream =
                    ItemModelResourcesTest.class.getClassLoader().getResourceAsStream(path)) {
                Assert.assertNotNull(path, stream);
                String content = new String(readAllBytes(stream), StandardCharsets.UTF_8);
                Assert.assertFalse(path, content.contains("generic.attack_knockback"));
            }
        }
    }

    @Test
    public void bundledStoneModelsUseOneTwelveTexturePaths() throws IOException {
        assertModelUsesOneTwelveTexturePath("assets/intensify/models/item/strengthening_stone.json");
        assertModelUsesOneTwelveTexturePath("assets/intensify/models/item/eneng_stone.json");
        assertModelUsesOneTwelveTexturePath("assets/intensify/models/item/protection_stone.json");
        assertModelUsesOneTwelveTexturePath("assets/intensify/models/item/eternal_stone.json");
    }

    private static void assertResource(String path) {
        Assert.assertNotNull(path, ItemModelResourcesTest.class.getClassLoader().getResource(path));
    }

    private static void assertModelUsesOneTwelveTexturePath(String path) throws IOException {
        try (InputStream stream =
                ItemModelResourcesTest.class.getClassLoader().getResourceAsStream(path)) {
            Assert.assertNotNull(path, stream);
            String content = new String(readAllBytes(stream), StandardCharsets.UTF_8);
            Assert.assertFalse(path, content.contains("\"layer0\": \"minecraft:item/"));
            Assert.assertTrue(path, content.contains("\"layer0\": \"minecraft:items/"));
        }
    }

    private static byte[] readAllBytes(InputStream stream) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        while ((read = stream.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }
}
