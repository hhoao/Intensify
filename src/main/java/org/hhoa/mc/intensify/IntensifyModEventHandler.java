package org.hhoa.mc.intensify;

import static org.hhoa.mc.intensify.Intensify.MODID;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.hhoa.mc.intensify.config.IntensifyConfig;
import org.hhoa.mc.intensify.provider.IntensifyAdvancementProvider;
import org.hhoa.mc.intensify.provider.IntensifyItemModelProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootModifierProvider;
import org.hhoa.mc.intensify.provider.IntensifyLootTableProvider;
import org.hhoa.mc.intensify.provider.IntensifyStoneRecipeProvider;

public class IntensifyModEventHandler {
    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        IntensifyConfig.initialize();
    }

    @SubscribeEvent
    public void onConfigLoad(ModConfig.ModConfigEvent event) {
        CommentedConfig configData = event.getConfig().getConfigData();
        System.out.println(configData);
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        event.getGenerator()
                .addProvider(new IntensifyLootModifierProvider(event.getGenerator(), MODID));
        event.getGenerator()
                .addProvider(
                        new IntensifyItemModelProvider(
                                event.getGenerator(), MODID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new IntensifyStoneRecipeProvider(event.getGenerator()));

        event.getGenerator().addProvider(new IntensifyAdvancementProvider(event.getGenerator()));
        event.getGenerator().addProvider(new IntensifyLootTableProvider(event.getGenerator()));
    }
}
