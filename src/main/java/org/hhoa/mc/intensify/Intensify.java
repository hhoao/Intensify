package org.hhoa.mc.intensify;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.hhoa.mc.intensify.config.Config;
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

    public Intensify(FMLJavaModLoadingContext fmlJavaModLoadingContext) {
        LOGGER.info("Intensify enable");
        IEventBus modEventBus = fmlJavaModLoadingContext.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.register(new IntensifyModEventHandler());
        forgeEventBus.register(new IntensifyForgeEventHandler());
        Config.initialize(fmlJavaModLoadingContext);
        ItemRegistry.initialize(modEventBus);
        RecipeRegistry.initialize(modEventBus);
        LootRegistry.initialize(modEventBus);
        LootConditionsRegistry.initialize(modEventBus);
    }
}
