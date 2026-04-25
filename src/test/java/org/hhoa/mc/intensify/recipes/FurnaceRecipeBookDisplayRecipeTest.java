package org.hhoa.mc.intensify.recipes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FurnaceRecipeBookDisplayRecipeTest {
    @Test
    void compiledDisplayRecipeExposesFuelOnlyPlacementContract() throws Exception {
        String javapOutput =
                runJavap("org.hhoa.mc.intensify.recipes.display.FurnaceRecipeBookDisplayRecipe");

        assertTrue(
                javapOutput.contains("public boolean isFuelOnlyPlacement();"),
                "compiled display recipe should declare the fuel-only placement contract");
        assertTrue(
                javapOutput.contains("public net.minecraft.world.item.ItemStack getPlaceableFuel();"),
                "compiled display recipe should declare the placeable fuel contract");
        assertTrue(
                javapOutput.contains("public boolean matches(net.minecraft.world.item.crafting.SingleRecipeInput, net.minecraft.world.level.Level);"),
                "compiled display recipe should still expose matches(...)");
        assertTrue(
                javapOutput.contains("public boolean isFuelOnlyPlacement();\n    Code:\n       0: iconst_1\n       1: ireturn"),
                "fuel-only placement contract should return true");
        assertTrue(
                javapOutput.contains("public boolean matches(net.minecraft.world.item.crafting.SingleRecipeInput, net.minecraft.world.level.Level);\n    Code:\n       0: iconst_0\n       1: ireturn"),
                "display recipe should never match live furnace input");
        assertTrue(
                javapOutput.contains(
                        "public net.minecraft.world.item.ItemStack getPlaceableFuel();\n    Code:\n       0: aload_0\n       1: getfield"),
                "placeable fuel should read from the stored display fuel");
        assertTrue(
                javapOutput.contains("displayFuel:Lnet/minecraft/world/item/ItemStack;"),
                "placeable fuel should use the displayFuel backing field");
        assertTrue(
                javapOutput.contains(
                        "invokevirtual #"),
                "placeable fuel should invoke ItemStack.copy() rather than return the field directly");
        assertTrue(
                javapOutput.contains("net/minecraft/world/item/ItemStack.copy:()Lnet/minecraft/world/item/ItemStack;"),
                "placeable fuel should return a defensive copy");
    }

    private static String runJavap(String className) throws IOException, InterruptedException {
        Process process =
                new ProcessBuilder(
                                javapPath(),
                                "-classpath",
                                Path.of("build/classes/java/main").toString(),
                                "-c",
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
}
