package org.hhoa.mc.intensify.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import net.minecraft.resources.ResourceLocation;

/**
 * @author xianxing
 * @since 2024/11/7
 */
public class ResourcesUtils {
    public static String readResourceLocationAsString(ResourceLocation resourceLocation) {
        try (InputStream inputStream =
                ResourcesUtils.class
                        .getClassLoader()
                        .getResourceAsStream(toClasspathPath(resourceLocation))) {
            if (inputStream == null) {
                return null;
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toClasspathPath(ResourceLocation resourceLocation) {
        return "assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
    }
}
