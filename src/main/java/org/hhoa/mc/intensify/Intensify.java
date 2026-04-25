package org.hhoa.mc.intensify;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.hhoa.mc.intensify.registry.AttachmentRegistry;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.registry.LootConditionsRegistry;
import org.hhoa.mc.intensify.registry.LootRegistry;
import org.hhoa.mc.intensify.registry.RecipeRegistry;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Intensify.MODID)
public class Intensify {
    public static final String MODID = "intensify";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String locationStr(String path) {
        return location(path).toString();
    }

    public Intensify(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Intensify enable");
        modEventBus.register(new IntensifyModEventHandler());
        NeoForge.EVENT_BUS.register(new IntensifyForgeEventHandler());
        ConfigRegistry.initialize(modContainer);
        AttachmentRegistry.initialize(modEventBus);
        ItemRegistry.initialize(modEventBus);
        RecipeRegistry.initialize(modEventBus);
        LootRegistry.initialize(modEventBus);
        LootConditionsRegistry.initialize(modEventBus);
    }
}
