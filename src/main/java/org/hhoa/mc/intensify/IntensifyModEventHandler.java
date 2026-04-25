package org.hhoa.mc.intensify;

import static org.hhoa.mc.intensify.Intensify.MODID;

import com.electronwill.nightconfig.core.CommentedConfig;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.gametest.FurnaceGameTests;
import org.hhoa.mc.intensify.provider.IntensifyAdvancementProvider;
import org.hhoa.mc.intensify.provider.IntensifyItemModelProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootModifierProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootTableProvider;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyModEventHandler {
    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        IntensifyConfig.initialize();
    }

    @SubscribeEvent
    public void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getLoadedConfig() != null) {
            CommentedConfig configData = event.getConfig().getLoadedConfig().config();
            System.out.println(configData);
        }
    }

    @SubscribeEvent
    public void runServerData(GatherDataEvent.Server event) {
        event.addProvider(
                new IntensifyLootModifierProvider(
                        event.getGenerator().getPackOutput(), event.getLookupProvider(), MODID));
        event.addProvider(
                new IntensifyAdvancementProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        List.of(new IntensifyAdvancementProvider.ModAdvancementGenerator())));
        event.addProvider(
                new IntensifyLootTableProvider(
                        event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    @SubscribeEvent
    public void runClientData(GatherDataEvent.Client event) {
        event.addProvider(new IntensifyItemModelProvider(event.getGenerator().getPackOutput()));
    }

    @SubscribeEvent
    public void onRegisterCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemRegistry.STRENGTHENING_STONE.get());
            event.accept(ItemRegistry.ENENG_STONE.get());
            event.accept(ItemRegistry.PROTECTION_STONE.get());
            event.accept(ItemRegistry.ETERNAL_STONE.get());
        }
    }

    @SubscribeEvent
    public void registerGameTests(RegisterGameTestsEvent event) {
        FurnaceGameTests.register(event);
    }
}
