package org.hhoa.mc.intensify.provider;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyItemModelProvider extends ItemModelProvider {
    public IntensifyItemModelProvider(
            DataGenerator output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        String generatedItemId = "item/generated";
        String layer0 = "layer0";
        singleTexture(
                ItemRegistry.STRENGTHENING_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                withItemPrefix(ForgeRegistries.ITEMS.getKey(Items.COAL)));

        singleTexture(
                ItemRegistry.ENENG_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                withItemPrefix(ForgeRegistries.ITEMS.getKey(Items.LAPIS_LAZULI)));

        singleTexture(
                ItemRegistry.PROTECTION_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                withItemPrefix(ForgeRegistries.ITEMS.getKey(Items.EMERALD)));

        singleTexture(
                ItemRegistry.ETERNAL_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                withItemPrefix(ForgeRegistries.ITEMS.getKey(Items.DIAMOND)));
    }

    private ResourceLocation withItemPrefix(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), "item/" + location.getPath());
    }
}
