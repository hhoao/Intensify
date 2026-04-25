package org.hhoa.mc.intensify;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        return new ResourceLocation(MODID, path);
    }

    public static String locationStr(String path) {
        return new ResourceLocation(MODID, path).toString();
    }

    public Intensify() {
        FMLJavaModLoadingContext fmlJavaModLoadingContext = FMLJavaModLoadingContext.get();
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        LOGGER.info("Intensify enable");
        IEventBus modEventBus = fmlJavaModLoadingContext.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.register(new IntensifyModEventHandler());
        forgeEventBus.register(new IntensifyForgeEventHandler());
        ConfigRegistry.initialize(modLoadingContext);
        ItemRegistry.initialize(modEventBus);
        RecipeRegistry.initialize(modEventBus);
        LootRegistry.initialize(modEventBus);
        LootConditionsRegistry.initialize(modEventBus);
    }
}
