package org.hhoa.mc.intensify.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyItemModelProvider extends ItemModelProvider {
    public IntensifyItemModelProvider(
            PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        String generatedItemId = "item/generated";
        String layer0 = "layer0";
        String itemPrefix = "item/";
        singleTexture(
                ItemRegistry.STRENGTHENING_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                ForgeRegistries.ITEMS.getKey(Items.COAL).withPrefix(itemPrefix));

        singleTexture(
                ItemRegistry.ENENG_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                ForgeRegistries.ITEMS.getKey(Items.LAPIS_LAZULI).withPrefix(itemPrefix));

        singleTexture(
                ItemRegistry.PROTECTION_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                ForgeRegistries.ITEMS.getKey(Items.EMERALD).withPrefix(itemPrefix));

        singleTexture(
                ItemRegistry.ETERNAL_STONE.getId().toString(),
                new ResourceLocation(generatedItemId),
                layer0,
                ForgeRegistries.ITEMS.getKey(Items.DIAMOND).withPrefix(itemPrefix));
    }
}
