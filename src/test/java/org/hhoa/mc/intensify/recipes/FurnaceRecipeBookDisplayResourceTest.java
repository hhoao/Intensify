package org.hhoa.mc.intensify.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class FurnaceRecipeBookDisplayResourceTest {
    @Test
    void serializerSourceRetainsFuelAndResultFields() throws IOException {
        Path serializerSource =
                Path.of(
                        "src/main/java/org/hhoa/mc/intensify/recipes/display/"
                                + "FurnaceRecipeBookDisplayRecipeSerializer.java");
        assertTrue(Files.exists(serializerSource), "missing display recipe serializer source file");

        String text = Files.readString(serializerSource);
        assertTrue(text.contains(".fieldOf(\"fuel\")"), "serializer should retain the fuel field");
        assertTrue(
                text.contains(".fieldOf(\"result\")"), "serializer should retain the result field");
    }

    @Test
    void recipeBookDisplayResourcesExistOnClasspathWithDiamondSwordStoneExamples()
            throws IOException {
        assertDisplayRecipe(
                "data/intensify/recipe/recipe_book_display/eneng_stone.json",
                "intensify:eneng_stone");
        assertDisplayRecipe(
                "data/intensify/recipe/recipe_book_display/strengthening_stone.json",
                "intensify:strengthening_stone");
        assertDisplayRecipe(
                "data/intensify/recipe/recipe_book_display/eternal_stone.json",
                "intensify:eternal_stone");
    }

    private static void assertDisplayRecipe(String resourcePath, String expectedFuelItem)
            throws IOException {
        try (InputStream inputStream =
                FurnaceRecipeBookDisplayResourceTest.class
                        .getClassLoader()
                        .getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "missing recipe book display resource: " + resourcePath);

            @SuppressWarnings("unchecked")
            Map<String, Object> json =
                    new Yaml().load(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

            assertEquals(
                    "minecraft:diamond_sword",
                    requiredItemId(json, "input", resourcePath),
                    resourcePath + " should use diamond_sword as the display input");
            assertEquals(
                    expectedFuelItem,
                    requiredItemId(json, "fuel", resourcePath),
                    resourcePath + " should use the expected stone as the display fuel");
            assertEquals(
                    "minecraft:diamond_sword",
                    requiredItemId(json, "result", resourcePath),
                    resourcePath + " should use diamond_sword as the display result");
        }
    }

    private static String requiredItemId(
            Map<String, Object> json, String stackKey, String resourcePath) {
        assertTrue(
                json.containsKey(stackKey),
                resourcePath + " should define a top-level '" + stackKey + "' object");
        Object stackValue = json.get(stackKey);
        assertNotNull(stackValue, resourcePath + " should not set '" + stackKey + "' to null");
        assertTrue(
                stackValue instanceof Map<?, ?>,
                resourcePath + " should encode '" + stackKey + "' as an object");

        Map<?, ?> stack = (Map<?, ?>) stackValue;
        assertTrue(
                stack.containsKey("id"),
                resourcePath + " should define '" + stackKey + ".id'");
        Object itemValue = stack.get("id");
        assertNotNull(itemValue, resourcePath + " should not set '" + stackKey + ".id' to null");
        assertTrue(
                itemValue instanceof String,
                resourcePath + " should encode '" + stackKey + ".id' as a string");
        return (String) itemValue;
    }
}
