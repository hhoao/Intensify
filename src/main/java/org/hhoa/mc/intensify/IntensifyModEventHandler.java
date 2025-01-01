package org.hhoa.mc.intensify;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;
import org.hhoa.mc.intensify.provider.IntensifyItemModelProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootModifierProvider;
import org.hhoa.mc.intensify.provider.IntensifyStoneRecipeProvider;
import org.hhoa.mc.intensify.registry.ItemRegistry;

import static org.hhoa.mc.intensify.Intensify.MODID;

public class IntensifyModEventHandler {
    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        ToolIntensifyConfig.initialize();
    }

    @SubscribeEvent
    public void onConfigLoad(ModConfigEvent event) {
        CommentedConfig configData = event.getConfig().getConfigData();
        System.out.println(configData);
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        event.getGenerator().addProvider(
            event.includeServer(),
            new IntensifyLootModifierProvider(event.getGenerator().getPackOutput(), MODID));
        event.getGenerator().addProvider(
            event.includeServer(),
            new IntensifyItemModelProvider(event.getGenerator().getPackOutput(), MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(
            event.includeServer(),
            new IntensifyStoneRecipeProvider(event.getGenerator().getPackOutput()));
    }

    @SubscribeEvent
    public void onRegisterCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ItemRegistry.STRENGTHENING_STONE);
            event.accept(ItemRegistry.ENENG_STONE);
            event.accept(ItemRegistry.PROTECTION_STONE);
            event.accept(ItemRegistry.ETERNAL_STONE);
        }
    }
}
