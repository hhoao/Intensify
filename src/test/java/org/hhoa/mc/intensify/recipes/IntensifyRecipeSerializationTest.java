package org.hhoa.mc.intensify.recipes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class IntensifyRecipeSerializationTest {
    @Test
    void intensifyRecipesDoNotExposeEmptyRecipeBookResults() throws Exception {
        Path recipe = Path.of("src/main/java/org/hhoa/mc/intensify/recipes/IntensifyRecipe.java");
        String text = Files.readString(recipe);

        assertTrue(
                text.contains("Ingredient.of(Items.FURNACE)"),
                "custom intensify recipes should keep a non-empty placeholder ingredient");
        assertFalse(
                text.contains("ItemStack.EMPTY,\n                experience"),
                "custom intensify recipes must not publish an empty result item into recipe-book sync");
    }
}
