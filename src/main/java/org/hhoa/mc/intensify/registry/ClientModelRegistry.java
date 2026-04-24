package org.hhoa.mc.intensify.registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.hhoa.mc.intensify.Intensify;
import org.hhoa.mc.intensify.item.IntensifyStoneType;

@Mod.EventBusSubscriber(modid = Intensify.MODID, value = Side.CLIENT)
public final class ClientModelRegistry {
    private static final List<String> STONE_MODEL_IDS =
            Collections.unmodifiableList(
                    Arrays.asList(
                            IntensifyStoneType.STRENGTHENING_STONE.getIdentifier(),
                            IntensifyStoneType.ENENG_STONE.getIdentifier(),
                            IntensifyStoneType.PROTECTION_STONE.getIdentifier(),
                            IntensifyStoneType.ETERNAL_STONE.getIdentifier()));

    private ClientModelRegistry() {}

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerItemModel(
                ItemRegistry.STRENGTHENING_STONE,
                IntensifyStoneType.STRENGTHENING_STONE.getIdentifier());
        registerItemModel(ItemRegistry.ENENG_STONE, IntensifyStoneType.ENENG_STONE.getIdentifier());
        registerItemModel(
                ItemRegistry.PROTECTION_STONE, IntensifyStoneType.PROTECTION_STONE.getIdentifier());
        registerItemModel(
                ItemRegistry.ETERNAL_STONE, IntensifyStoneType.ETERNAL_STONE.getIdentifier());
    }

    static List<String> getStoneModelIds() {
        return STONE_MODEL_IDS;
    }

    private static void registerItemModel(Item item, String id) {
        ModelLoader.setCustomModelResourceLocation(
                item, 0, new ModelResourceLocation(Intensify.location(id), "inventory"));
    }
}
