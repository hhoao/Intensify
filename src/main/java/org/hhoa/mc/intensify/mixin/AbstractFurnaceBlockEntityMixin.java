package org.hhoa.mc.intensify.mixin;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.config.IntensifyConstants;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.item.IntensifyStone;
import org.hhoa.mc.intensify.item.IntensifyStoneType;
import org.hhoa.mc.intensify.recipes.IntensifyRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private static void intensify$serverTick(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            AbstractFurnaceBlockEntity furnace,
            CallbackInfo ci) {
        ItemStack input = furnace.getItem(0);
        ItemStack fuel = furnace.getItem(1);
        if (!intensify$shouldHandleCustomFlow(furnace, input, fuel)) {
            return;
        }
        ci.cancel();
        intensify$runCustomFlow(level, pos, state, furnace);
    }

    @Unique
    private static boolean intensify$shouldHandleCustomFlow(
            AbstractFurnaceBlockEntity furnace, ItemStack input, ItemStack fuel) {
        if (!(furnace instanceof FurnaceBlockEntity)) {
            return false;
        }
        if (fuel.getItem() instanceof IntensifyStone) {
            return true;
        }
        if (furnace.getPersistentData().contains(IntensifyConstants.LAST_RECIPE_TAG_ID)) {
            return true;
        }
        return !input.isEmpty()
                && IntensifyConfig.getToolIntensifyConfig(input.getItem()) != null;
    }

    @Unique
    private static void intensify$runCustomFlow(
            Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }

        boolean wasLit = furnace.litTimeRemaining > 0;
        boolean changed = false;

        if (wasLit) {
            furnace.litTimeRemaining--;
        }

        ItemStack input = furnace.getItem(0);
        ItemStack fuel = furnace.getItem(1);
        boolean hasInput = !input.isEmpty();
        boolean hasFuel = !fuel.isEmpty();

        RecipeHolder<IntensifyRecipe> activeRecipe =
                intensify$resolveContinuationRecipe(level, furnace, input);

        if (furnace.litTimeRemaining <= 0) {
            RecipeHolder<IntensifyRecipe> startRecipe =
                    intensify$resolveStartRecipe(level, furnace, input, fuel);
            activeRecipe = startRecipe;
            if (startRecipe != null) {
                furnace.cookingTotalTime = startRecipe.value().cookingTime();
            }

            if (startRecipe != null
                    && hasInput
                    && hasFuel
                    && intensify$canBurn(startRecipe, furnace, input)) {
                int burnDuration = fuel.getBurnTime(RecipeType.SMELTING, serverLevel.fuelValues());
                furnace.litTimeRemaining = burnDuration;
                furnace.litTotalTime = burnDuration;
                if (furnace.litTimeRemaining > 0) {
                    changed = true;
                    Item remainingItem = fuel.getItem();
                    fuel.shrink(1);
                    if (fuel.isEmpty()) {
                        furnace.setItem(1, remainingItem.getCraftingRemainder());
                    } else if (!hasFuel) {
                        furnace.setItem(1, new ItemStack(remainingItem));
                    }
                }
            }
        }

        if (furnace.litTimeRemaining > 0
                && activeRecipe != null
                && intensify$canBurn(activeRecipe, furnace, input)) {
            if (furnace.cookingTotalTime <= 0) {
                furnace.cookingTotalTime = activeRecipe.value().cookingTime();
            }
            furnace.cookingTimer++;
            if (furnace.cookingTimer >= furnace.cookingTotalTime) {
                furnace.cookingTimer = 0;
                furnace.cookingTotalTime = activeRecipe.value().cookingTime();
                if (intensify$burn(level, furnace, activeRecipe)) {
                    furnace.setRecipeUsed(activeRecipe);
                    changed = true;
                }
            }
        } else if (furnace.litTimeRemaining > 0) {
            furnace.cookingTimer = 0;
        } else if (furnace.cookingTimer > 0) {
            furnace.cookingTimer = Mth.clamp(furnace.cookingTimer - 2, 0, furnace.cookingTotalTime);
        }

        boolean litNow = furnace.litTimeRemaining > 0;
        if (wasLit != litNow) {
            changed = true;
            BooleanProperty litProperty = AbstractFurnaceBlock.LIT;
            level.setBlock(pos, state.setValue(litProperty, litNow), 3);
        }

        if (changed) {
            furnace.setChanged();
        }
    }

    @Unique
    private static RecipeHolder<IntensifyRecipe> intensify$resolveStartRecipe(
            Level level, AbstractFurnaceBlockEntity furnace, ItemStack input, ItemStack fuel) {
        if (input.isEmpty() || fuel.isEmpty()) {
            intensify$clearLastRecipe(furnace);
            return null;
        }

        RecipeHolder<IntensifyRecipe> recipe = intensify$resolveRecipeForFuel(level, fuel);
        if (recipe != null && recipe.value().matchesStart(input, fuel)) {
            intensify$setLastRecipe(furnace, recipe.id().location());
            return recipe;
        }

        intensify$clearLastRecipe(furnace);
        return null;
    }

    @Unique
    private static RecipeHolder<IntensifyRecipe> intensify$resolveContinuationRecipe(
            Level level, AbstractFurnaceBlockEntity furnace, ItemStack input) {
        if (input.isEmpty()) {
            intensify$clearLastRecipe(furnace);
            return null;
        }

        String lastRecipeId = furnace.getPersistentData()
                .getString(IntensifyConstants.LAST_RECIPE_TAG_ID)
                .orElse("");
        if (lastRecipeId.isEmpty()) {
            return null;
        }

        ResourceLocation recipeId = ResourceLocation.parse(lastRecipeId);
        if (level.getServer() == null) {
            return null;
        }

        Optional<RecipeHolder<?>> holder = level.getServer()
                .getRecipeManager()
                .byKey(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, recipeId));
        if (holder.isEmpty() || !(holder.get().value() instanceof IntensifyRecipe recipe)) {
            intensify$clearLastRecipe(furnace);
            return null;
        }

        if (!recipe.matchesContinuation(input)) {
            intensify$clearLastRecipe(furnace);
            return null;
        }

        @SuppressWarnings("unchecked")
        RecipeHolder<IntensifyRecipe> typedHolder = (RecipeHolder<IntensifyRecipe>) holder.get();
        return typedHolder;
    }

    @Unique
    private static RecipeHolder<IntensifyRecipe> intensify$resolveRecipeForFuel(
            Level level, ItemStack fuel) {
        ResourceLocation recipeId = null;
        Item fuelItem = fuel.getItem();
        if (fuelItem == org.hhoa.mc.intensify.registry.ItemRegistry.ENENG_STONE.get()) {
            recipeId = Intensify.location(IntensifyStoneType.ENENG_STONE.getIdentifier());
        } else if (fuelItem
                == org.hhoa.mc.intensify.registry.ItemRegistry.STRENGTHENING_STONE.get()) {
            recipeId = Intensify.location(IntensifyStoneType.STRENGTHENING_STONE.getIdentifier());
        } else if (fuelItem == org.hhoa.mc.intensify.registry.ItemRegistry.ETERNAL_STONE.get()) {
            recipeId = Intensify.location(IntensifyStoneType.INTENSIFY_STONE.getIdentifier());
        }

        if (recipeId == null) {
            return null;
        }

        if (level.getServer() == null) {
            return null;
        }

        Optional<RecipeHolder<?>> holder = level.getServer()
                .getRecipeManager()
                .byKey(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, recipeId));
        if (holder.isEmpty() || !(holder.get().value() instanceof IntensifyRecipe)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        RecipeHolder<IntensifyRecipe> typedHolder = (RecipeHolder<IntensifyRecipe>) holder.get();
        return typedHolder;
    }

    @Unique
    private static boolean intensify$canBurn(
            RecipeHolder<IntensifyRecipe> recipe, AbstractFurnaceBlockEntity furnace, ItemStack input) {
        if (input.isEmpty()) {
            return false;
        }

        ItemStack resultSlot = furnace.getItem(2);
        if (resultSlot.isEmpty()) {
            return true;
        }

        ItemStack preview = input.copyWithCount(1);
        return ItemStack.isSameItemSameComponents(resultSlot, preview)
                && resultSlot.getCount() < Math.min(furnace.getMaxStackSize(), preview.getMaxStackSize());
    }

    @Unique
    private static boolean intensify$burn(
            Level level, AbstractFurnaceBlockEntity furnace, RecipeHolder<IntensifyRecipe> recipeHolder) {
        ItemStack input = furnace.getItem(0);
        if (input.isEmpty()) {
            intensify$clearLastRecipe(furnace);
            return false;
        }

        ItemStack result = input.copyWithCount(1);
        ToolIntensifyConfig toolConfig = IntensifyConfig.getToolIntensifyConfig(result.getItem());
        if (toolConfig == null) {
            intensify$clearLastRecipe(furnace);
            return false;
        }

        ServerPlayer player = intensify$resolveFurnaceOwner(level, furnace);
        recipeHolder.value().intensify(result, level.registryAccess(), toolConfig, player);

        ItemStack resultSlot = furnace.getItem(2);
        if (resultSlot.isEmpty()) {
            furnace.setItem(2, result);
        } else if (ItemStack.isSameItemSameComponents(resultSlot, result)) {
            resultSlot.grow(result.getCount());
        } else {
            return false;
        }

        input.shrink(1);
        intensify$clearLastRecipe(furnace);
        return true;
    }

    @Unique
    private static ServerPlayer intensify$resolveFurnaceOwner(
            Level level, AbstractFurnaceBlockEntity furnace) {
        String ownerName = furnace.getPersistentData()
                .getString(IntensifyConstants.FURNACE_OWNER_TAG_ID)
                .orElse("");
        if (ownerName.isEmpty() || level.getServer() == null) {
            return null;
        }
        return level.getServer().getPlayerList().getPlayerByName(ownerName);
    }

    @Unique
    private static void intensify$setLastRecipe(
            AbstractFurnaceBlockEntity furnace, ResourceLocation recipeId) {
        furnace.getPersistentData().putString(IntensifyConstants.LAST_RECIPE_TAG_ID, recipeId.toString());
    }

    @Unique
    private static void intensify$clearLastRecipe(AbstractFurnaceBlockEntity furnace) {
        furnace.getPersistentData().remove(IntensifyConstants.LAST_RECIPE_TAG_ID);
    }
}
