package org.hhoa.mc.intensify.recipes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FurnaceRecipeBookDisplayRecipeTest {
    @Test
    void compiledDisplayRecipeExposesFuelOnlyPlacementContract() throws Exception {
        ClassLoader loader = runtimeInspectionClassLoader();
        Class<?> recipeClass =
                Class.forName(
                        "org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe",
                        false,
                        loader);
        Class<?> singleRecipeInputClass =
                Class.forName("net.minecraft.world.item.crafting.SingleRecipeInput", false, loader);
        Class<?> levelClass = Class.forName("net.minecraft.world.level.Level", false, loader);
        recipeClass.getDeclaredMethod("isFuelOnlyPlacement");
        recipeClass.getDeclaredMethod("getPlaceableFuel");
        recipeClass.getDeclaredMethod("matches", singleRecipeInputClass, levelClass);

        String javapOutput =
                runJavap("org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe");
        String isFuelOnlyPlacementBlock =
                extractMethodBlock(javapOutput, "public boolean isFuelOnlyPlacement();");
        String getPlaceableFuelBlock =
                extractMethodBlock(
                        javapOutput,
                        "public net.minecraft.world.item.ItemStack getPlaceableFuel();");
        String matchesBlock =
                extractMethodBlock(
                        javapOutput,
                        "public boolean matches(net.minecraft.world.item.crafting.SingleRecipeInput, net.minecraft.world.level.Level);");

        assertTrue(
                isFuelOnlyPlacementBlock.contains("0: iconst_1")
                        && isFuelOnlyPlacementBlock.contains("1: ireturn"),
                "fuel-only placement contract should return true");
        assertTrue(
                matchesBlock.contains("0: iconst_0") && matchesBlock.contains("1: ireturn"),
                "display recipe should never match live furnace input");
        assertTrue(
                getPlaceableFuelBlock.contains("0: aload_0")
                        && getPlaceableFuelBlock.contains("1: getfield"),
                "placeable fuel should read from the stored display fuel");
        assertTrue(
                getPlaceableFuelBlock.contains("Field displayFuel:Lnet/minecraft/world/item/ItemStack;"),
                "placeable fuel should use the displayFuel backing field");
        assertTrue(
                getPlaceableFuelBlock.contains(
                        "Method net/minecraft/world/item/ItemStack.copy:()Lnet/minecraft/world/item/ItemStack;"),
                "placeable fuel should return a defensive copy");
    }

    private static String runJavap(String className) throws IOException, InterruptedException {
        Process process =
                new ProcessBuilder(
                                javapPath(),
                                "-v",
                                "-classpath",
                                runtimeClasspath(),
                                "-p",
                                className)
                        .redirectErrorStream(true)
                        .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        assertTrue(exitCode == 0, "javap should inspect compiled class successfully:\n" + output);
        return output;
    }

    private static String javapPath() {
        return Path.of(System.getProperty("java.home"), "bin", "javap").toString();
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

    private static ClassLoader runtimeInspectionClassLoader() throws IOException {
        return new java.net.URLClassLoader(
                runtimeClasspathUrls(),
                ClassLoader.getPlatformClassLoader());
    }

    private static java.net.URL[] runtimeClasspathUrls() throws IOException {
        return List.of(runtimeClasspath().split(File.pathSeparator)).stream()
                .filter(entry -> !entry.isBlank())
                .map(Path::of)
                .map(Path::toUri)
                .map(
                        uri -> {
                            try {
                                return uri.toURL();
                            } catch (IOException exception) {
                                throw new IllegalStateException(exception);
                            }
                        })
                .toArray(java.net.URL[]::new);
    }

    private static String extractMethodBlock(String javapOutput, String methodSignature) {
        List<String> lines = javapOutput.lines().toList();
        StringBuilder block = new StringBuilder();
        boolean inBlock = false;

        for (String line : lines) {
            if (!inBlock && line.equals("  " + methodSignature)) {
                inBlock = true;
            }
            if (!inBlock) {
                continue;
            }
            if (block.length() > 0
                    && line.startsWith("  ")
                    && !line.startsWith("    ")
                    && !line.equals("  " + methodSignature)) {
                break;
            }
            block.append(line).append('\n');
        }

        assertTrue(block.length() > 0, "missing javap method block for " + methodSignature);
        return block.toString();
    }
}
