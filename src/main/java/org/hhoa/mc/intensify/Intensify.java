package org.hhoa.mc.intensify;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.intensify.capabilities.IFirstLoginCapability;
import org.hhoa.mc.intensify.registry.ConfigRegistry;
import org.hhoa.mc.intensify.registry.ItemRegistry;
import org.hhoa.mc.intensify.registry.LootConditionsRegistry;
import org.hhoa.mc.intensify.registry.LootRegistry;
import org.hhoa.mc.intensify.registry.RecipeRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Intensify.MODID)
public class Intensify {
    public static final String MODID = "intensify";
    private static final Logger LOGGER = LogManager.getLogger(Intensify.class);

    @CapabilityInject(IFirstLoginCapability.class)
    public static Capability<IFirstLoginCapability> FIRST_LOGIN_CAPABILITY;

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
