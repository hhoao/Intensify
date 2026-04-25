package org.hhoa.mc.intensify.mixin;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

class FurnaceRecipeBookComponentMixinTest {
    @Test
    void compiledMixinOnlyRoutesGhostingToFuelAndResultSlots() throws Exception {
        String javapOutput = runJavap("org.hhoa.mc.intensify.mixin.FurnaceRecipeBookComponentMixin");
        String methodBlock =
                extractMethodBlock(
                        javapOutput,
                        "private void intensify$fillFuelOnlyGhostRecipe(net.minecraft.client.gui.screens.recipebook.GhostSlots, net.minecraft.world.item.crafting.display.RecipeDisplay, net.minecraft.util.context.ContextMap, org.spongepowered.asm.mixin.injection.callback.CallbackInfo);");

        assertTrue(methodBlock.contains("setResult"), "client ghost override should keep result ghosting");
        assertTrue(methodBlock.contains("setInput"), "client ghost override should populate the fuel slot ghost");
        assertTrue(
                methodBlock.contains("FurnaceRecipeDisplay.fuel"),
                "client ghost override should read the display fuel, not the input ingredient");
        assertFalse(
                methodBlock.contains("FurnaceRecipeDisplay.ingredient"),
                "client ghost override must not place a ghost ingredient in the input slot");
    }

    private static String runJavap(String className) throws IOException, InterruptedException {
        Process process =
                new ProcessBuilder(
                                javapPath(),
                                "-classpath",
                                runtimeClasspath(),
                                "-p",
                                "-c",
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
