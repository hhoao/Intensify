package org.hhoa.mc.intensify.recipes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FurnaceRecipeBookDisplayRecipeTest {
    @Test
    void displayRecipeSourceOverridesDisplayToExposeExplicitFuel() throws Exception {
        Path recipeSource =
                Path.of(
                        "src/main/java/org/hhoa/mc/intensify/recipes/display/"
                                + "FurnaceRecipeBookDisplayRecipe.java");
        assertTrue(Files.exists(recipeSource), "missing display recipe source file");

        String text = Files.readString(recipeSource);
        assertTrue(text.contains("List<RecipeDisplay> display()"), "display recipe should override display()");
        assertTrue(
                text.contains("new FurnaceRecipeDisplay("),
                "display recipe should build an explicit FurnaceRecipeDisplay");
        assertTrue(
                text.contains("new SlotDisplay.ItemStackSlotDisplay(this.displayFuel)"),
                "display recipe should expose the configured fuel stack instead of AnyFuel");
        assertTrue(
                text.contains("return false;"),
                "display recipe matches(...) should never match live furnace input");
    }
}
