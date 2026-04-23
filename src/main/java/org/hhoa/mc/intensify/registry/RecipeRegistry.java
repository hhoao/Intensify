package org.hhoa.mc.intensify.registry;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.CommonIntensifyRecipe;
import org.hhoa.mc.intensify.recipes.impl.EnengRecipe;
import org.hhoa.mc.intensify.recipes.impl.StrengtheningRecipe;
import org.hhoa.mc.intensify.util.FurnaceHelper;

public class RecipeRegistry {
    public static final IntensifyRecipe STRENGTHENING_RECIPE = new StrengtheningRecipe();
    public static final IntensifyRecipe ENENG_RECIPE = new EnengRecipe();
    public static final IntensifyRecipe COMMON_INTENSIFY_RECIPE = new CommonIntensifyRecipe();

    private static final List<IntensifyRecipe> RECIPES =
            Arrays.asList(STRENGTHENING_RECIPE, ENENG_RECIPE, COMMON_INTENSIFY_RECIPE);
    private static final Map<ResourceLocation, IntensifyRecipe> RECIPES_BY_ID = new LinkedHashMap<>();

    static {
        for (IntensifyRecipe recipe : RECIPES) {
            RECIPES_BY_ID.put(recipe.getId(), recipe);
        }
    }

    public static ItemStack getSmeltingResult(
            TileEntityFurnace furnace, ItemStack fallback, boolean finalizing) {
        IntensifyRecipe recipe = resolveRecipe(furnace);
        if (recipe == null) {
            return fallback;
        }
        return finalizing ? recipe.assemble(furnace) : recipe.getPreviewResult(furnace);
    }

    @Nullable
    private static IntensifyRecipe resolveRecipe(TileEntityFurnace furnace) {
        NBTTagCompound data = furnace.getTileData();
        if (FurnaceHelper.getLitTime(furnace) <= 0) {
            for (IntensifyRecipe recipe : RECIPES) {
                if (recipe.matchesStart(furnace)) {
                    data.setString(IntensifyConstants.LAST_RECIPE_TAG_ID, recipe.getId().toString());
                    return recipe;
                }
            }
            data.removeTag(IntensifyConstants.LAST_RECIPE_TAG_ID);
            return null;
        }

        String recipeId = data.getString(IntensifyConstants.LAST_RECIPE_TAG_ID);
        if (recipeId.isEmpty()) {
            return null;
        }

        IntensifyRecipe recipe = RECIPES_BY_ID.get(new ResourceLocation(recipeId));
        if (recipe != null && recipe.matchesActive(furnace)) {
            return recipe;
        }

        data.removeTag(IntensifyConstants.LAST_RECIPE_TAG_ID);
        return null;
    }
}
