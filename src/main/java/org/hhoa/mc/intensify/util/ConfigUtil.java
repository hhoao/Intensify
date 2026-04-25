package org.hhoa.mc.intensify.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;

public class ConfigUtil {
    public static CommentedConfig readTomlConfig(String fileName) {
        TomlParser tomlParser = new TomlParser();
        ResourceLocation resourceLocation =
                new ResourceLocation(Intensify.MODID, "config/" + fileName);
        Optional<CommentedConfig> commentedConfig =
                Minecraft.getInstance()
                        .getResourceManager()
                        .getResource(resourceLocation)
                        .map(
                                resource -> {
                                    try (Reader reader = resource.openAsReader()) {
                                        return tomlParser.parse(reader);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
        return commentedConfig.orElse(null);
    }
}
