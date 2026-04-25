package org.hhoa.mc.intensify.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import net.minecraft.resources.ResourceLocation;
import org.hhoa.mc.intensify.Intensify;

public class ConfigUtil {
    public static CommentedConfig readTomlConfig(String fileName) {
        TomlParser tomlParser = new TomlParser();
        ResourceLocation resourceLocation =
                ResourceLocation.fromNamespaceAndPath(Intensify.MODID, "config/" + fileName);
        String content = ResourcesUtils.readResourceLocationAsString(resourceLocation);
        if (content == null) {
            return null;
        }
        try (Reader reader = new StringReader(content)) {
            return tomlParser.parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
