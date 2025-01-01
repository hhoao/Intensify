package org.hhoa.mc.intensify.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLPaths;
import org.hhoa.mc.intensify.Intensify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ConfigUtil {
    public static CommentedConfig readTomlConfig(String fileName) {
        TomlParser tomlParser = new TomlParser();
        ResourceLocation resourceLocation = new ResourceLocation(Intensify.MODID, "config/" + fileName);
        Optional<CommentedConfig> commentedConfig = Minecraft.getInstance()
            .getResourceManager()
            .getResource(resourceLocation)
            .map(resource -> {
                try (Reader reader = resource.openAsReader()) {
                    return tomlParser.parse(reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        return commentedConfig.orElse(null);
    }
}
