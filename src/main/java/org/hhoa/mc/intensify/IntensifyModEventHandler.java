package org.hhoa.mc.intensify;

import static org.hhoa.mc.intensify.Intensify.MODID;

import com.electronwill.nightconfig.core.CommentedConfig;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.provider.IntensifyAdvancementProvider;
import org.hhoa.mc.intensify.provider.IntensifyItemModelProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootModifierProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootTableProvider;
import org.hhoa.mc.intensify.provider.IntensifyStoneRecipeProvider;
import org.hhoa.mc.intensify.registry.ItemRegistry;

public class IntensifyModEventHandler {
    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        IntensifyConfig.initialize();
    }

    @SubscribeEvent
    public void onConfigLoad(ModConfigEvent event) {
        CommentedConfig configData = event.getConfig().getConfigData();
        System.out.println(configData);
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        event.getGenerator()
                .addProvider(
                        event.includeServer(),
                        new IntensifyLootModifierProvider(
                                event.getGenerator().getPackOutput(), MODID));
        event.getGenerator()
                .addProvider(
                        event.includeServer(),
                        new IntensifyItemModelProvider(
                                event.getGenerator().getPackOutput(),
                                MODID,
                                event.getExistingFileHelper()));
        event.getGenerator()
                .addProvider(
                        event.includeServer(),
                        new IntensifyStoneRecipeProvider(event.getGenerator().getPackOutput()));

        event.getGenerator()
                .addProvider(
                        true,
                        new IntensifyAdvancementProvider(
                                event.getGenerator().getPackOutput(),
                                event.getLookupProvider(),
                                event.getExistingFileHelper(),
                                List.of(
                                        new IntensifyAdvancementProvider
                                                .ModAdvancementGenerator())));
        event.getGenerator()
                .addProvider(
                        true, new IntensifyLootTableProvider(event.getGenerator().getPackOutput()));
    }

    @SubscribeEvent
    public void onRegisterCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemRegistry.STRENGTHENING_STONE);
            event.accept(ItemRegistry.ENENG_STONE);
            event.accept(ItemRegistry.PROTECTION_STONE);
            event.accept(ItemRegistry.ETERNAL_STONE);
        }
    }
}
