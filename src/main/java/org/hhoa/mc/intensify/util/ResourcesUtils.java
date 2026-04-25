package org.hhoa.mc.intensify.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * @author xianxing
 * @since 2024/11/7
 */
public class ResourcesUtils {
    public static String readResourceLocationAsString(ResourceLocation resourceLocation) {
        Optional<String> s =
                Minecraft.getInstance()
                        .getResourceManager()
                        .getResource(resourceLocation)
                        .map(
                                resource -> {
                                    try {
                                        try (InputStream inputStream = resource.open()) {
                                            return new String(
                                                    inputStream.readAllBytes(),
                                                    StandardCharsets.UTF_8);
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
        return s.orElse(null);
    }
}
