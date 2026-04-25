package org.hhoa.mc.intensify.provider;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyItemModelProvider implements DataProvider {
    private final PackOutput.PathProvider itemModelOutput;
    private final PackOutput.PathProvider itemDefinitionOutput;

    public IntensifyItemModelProvider(PackOutput output) {
        this.itemModelOutput = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
        this.itemDefinitionOutput = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(
                saveItemResources(output, ItemRegistry.STRENGTHENING_STONE.getId(), Items.COAL),
                saveItemResources(output, ItemRegistry.ENENG_STONE.getId(), Items.LAPIS_LAZULI),
                saveItemResources(output, ItemRegistry.PROTECTION_STONE.getId(), Items.EMERALD),
                saveItemResources(output, ItemRegistry.ETERNAL_STONE.getId(), Items.DIAMOND));
    }

    @Override
    public String getName() {
        return "Intensify Item Models";
    }

    private CompletableFuture<?> saveItemResources(
            CachedOutput output, ResourceLocation modelId, net.minecraft.world.level.ItemLike textureItem) {
        CompletableFuture<?> modelFuture = saveItemModel(output, modelId, textureItem);
        CompletableFuture<?> itemFuture = saveItemDefinition(output, modelId);
        return CompletableFuture.allOf(modelFuture, itemFuture);
    }

    private CompletableFuture<?> saveItemModel(
            CachedOutput output, ResourceLocation modelId, net.minecraft.world.level.ItemLike textureItem) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty(
                "layer0", BuiltInRegistries.ITEM.getKey(textureItem.asItem()).withPrefix("item/").toString());
        root.add("textures", textures);

        Path outputPath = itemModelOutput.json(modelId);
        return DataProvider.saveStable(output, root, outputPath);
    }

    private CompletableFuture<?> saveItemDefinition(CachedOutput output, ResourceLocation itemId) {
        JsonObject root = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", itemId.withPrefix("item/").toString());
        root.add("model", model);

        Path outputPath = itemDefinitionOutput.json(itemId);
        return DataProvider.saveStable(output, root, outputPath);
    }
}
