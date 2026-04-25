package org.hhoa.mc.intensify;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.intensify.capabilities.FirstLoginCapabilityImpl;
import org.hhoa.mc.intensify.capabilities.FirstLoginCapabilityStorage;
import org.hhoa.mc.intensify.capabilities.IFirstLoginCapability;
import org.hhoa.mc.intensify.command.CommandIntensify;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.proxy.CommonProxy;
import org.hhoa.mc.intensify.registry.ConfigRegistry;

@Mod(
        modid = Intensify.MODID,
        name = Intensify.NAME,
        version = Intensify.VERSION,
        dependencies = "required-after:forge;required-after:attributeslib",
        acceptedMinecraftVersions = "[1.12,1.13)")
public class Intensify {
    public static final String MODID = "intensify";
    public static final String NAME = "Intensify";
    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = LogManager.getLogger(Intensify.class);

    @Mod.Instance(MODID)
    public static Intensify INSTANCE;

    @SidedProxy(
            clientSide = "org.hhoa.mc.intensify.proxy.ClientProxy",
            serverSide = "org.hhoa.mc.intensify.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @CapabilityInject(IFirstLoginCapability.class)
    public static Capability<IFirstLoginCapability> FIRST_LOGIN_CAPABILITY;

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static String locationStr(String path) {
        return new ResourceLocation(MODID, path).toString();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Intensify enable");
        CapabilityManager.INSTANCE.register(
                IFirstLoginCapability.class,
                new FirstLoginCapabilityStorage(),
                FirstLoginCapabilityImpl::new);
        ConfigRegistry.initialize(event.getModConfigurationDirectory());
        IntensifyConfig.initialize();
        MinecraftForge.EVENT_BUS.register(new IntensifyForgeEventHandler());
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandIntensify());
    }
}
