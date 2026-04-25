package org.hhoa.mc.intensify.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class FurnaceRecipeBookDisplayResourceTest {
    @Test
    void compiledSerializerRetainsFuelAndResultFieldNames() throws IOException {
        String serializerClass =
                runJavap("org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipeSerializer");
        assertTrue(
                containsUtf8Constant(serializerClass, "fuel"),
                "compiled serializer should retain the fuel field name");
        assertTrue(
                containsUtf8Constant(serializerClass, "result"),
                "compiled serializer should retain the result field name");
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

    private static String runJavap(String className) throws IOException {
        Process process =
                new ProcessBuilder(
                                Path.of(System.getProperty("java.home"), "bin", "javap").toString(),
                                "-v",
                                "-classpath",
                                runtimeClasspath(),
                                "-p",
                                className)
                        .redirectErrorStream(true)
                        .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        try {
            int exitCode = process.waitFor();
            assertTrue(exitCode == 0, "javap should inspect compiled class successfully:\n" + output);
            return output;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while waiting for javap", exception);
        }
    }

    private static String runtimeClasspath() throws IOException {
        Set<String> entries = new LinkedHashSet<>();
        String currentClasspath = System.getProperty("java.class.path", "");
        if (!currentClasspath.isBlank()) {
            entries.addAll(List.of(currentClasspath.split(File.pathSeparator)));
        }

        Path artifactsDir = Path.of("build/moddev/artifacts");
        if (Files.isDirectory(artifactsDir)) {
            try (var stream = Files.list(artifactsDir)) {
                stream.filter(path -> path.toString().endsWith(".jar"))
                        .map(path -> path.toAbsolutePath().toString())
                        .sorted()
                        .forEach(entries::add);
            }
        }

        Path legacyClasspath = Path.of("build/moddev/clientLegacyClasspath.txt");
        if (Files.exists(legacyClasspath)) {
            for (String line : Files.readAllLines(legacyClasspath)) {
                if (!line.isBlank()) {
                    entries.add(line);
                }
            }
        }

        return String.join(File.pathSeparator, entries);
    }

    private static boolean containsUtf8Constant(String javapOutput, String expectedValue) {
        String exactLine = "Utf8               " + expectedValue;
        return javapOutput.lines().anyMatch(line -> line.stripLeading().contains(exactLine));
    }
}
