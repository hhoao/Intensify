package org.hhoa.mc.intensify.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.fml.ModList;
import org.hhoa.mc.intensify.Intensify;

public class ConfigLoader {
    public static List<ToolIntensifyConfig> loadToolIntensifyConfigFromDir(String dir) {
        TomlParser tomlParser = new TomlParser();
        List<ToolIntensifyConfig> toolIntensifyConfigs = new ArrayList<>();
        Path configDir = resolveConfigDirectory(dir);
        if (Files.notExists(configDir)) {
            return toolIntensifyConfigs;
        }

        try (var configFiles = Files.walk(configDir, 1)) {
            configFiles
                    .filter(Files::isRegularFile)
                    .sorted()
                    .forEach(
                            path -> {
                                try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                                    CommentedConfig tomlConfig = tomlParser.parse(bufferedReader);

                                    Set<String> entries = tomlConfig.valueMap().keySet();
                                    for (String entry : entries) {
                                        toolIntensifyConfigs.add(
                                                loadToolIntensifyConfig(tomlConfig, entry));
                                    }
                                } catch (IOException e) {
                                    throw new UncheckedIOException(
                                            "Failed to load config " + path, e);
                                }
                            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config directory " + configDir, e);
        } catch (UncheckedIOException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

        return toolIntensifyConfigs;
    }

    private static Path resolveConfigDirectory(String dir) {
        var modList = ModList.get();
        if (modList == null) {
            throw new IllegalStateException("Mod list is not available while loading Intensify configs");
        }
        var modFileInfo = modList.getModFileById(Intensify.MODID);
        if (modFileInfo == null) {
            throw new IllegalStateException("Unable to resolve mod file for " + Intensify.MODID);
        }
        return modFileInfo.getFile().findResource("assets", Intensify.MODID, dir);
    }

    private static ToolIntensifyConfig loadToolIntensifyConfig(
            CommentedConfig tomlConfig, String name) {
        ToolIntensifyConfig toolIntensifyConfig = new ToolIntensifyConfig();
        toolIntensifyConfig.setName(name);
        try {
            Config swordNode = tomlConfig.get(name);
            if (swordNode != null) {
                for (Config.Entry entry : swordNode.entrySet()) {
                    if (entry.getKey().equals("enable")) {
                        toolIntensifyConfig.setEnable(entry.getValue());
                    } else if (entry.getKey().equals("attributes")) {
                        List<Config> attributes = swordNode.get("attributes");
                        configureAttributes(attributes, toolIntensifyConfig);
                    } else {
                        throw new RuntimeException(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading config " + name, e);
        }
        return toolIntensifyConfig;
    }

    private static void configureAttributes(
            List<Config> attributes, ToolIntensifyConfig toolIntensifyConfig) {
        if (attributes != null) {
            for (Config attrNode : attributes) {
                ToolIntensifyConfig.AttributeConfig attributeConfig =
                        new ToolIntensifyConfig.AttributeConfig();
                for (Config.Entry entry : attrNode.entrySet()) {
                    String key = entry.getKey();
                    if (key.equals("type")) {
                        String type = attrNode.get("type");
                        configureType(type, attributeConfig);
                    } else if (key.equals("eneng")) {
                        Config enengNode = attrNode.get("eneng");
                        configureEneng(enengNode, attributeConfig);
                    } else if (key.equals("grows")) {
                        List<Config> growNodes = attrNode.get("grows");
                        configureGroups(growNodes, attributeConfig);
                    } else {
                        throw new RuntimeException(key);
                    }
                }

                toolIntensifyConfig.getAttributes().add(attributeConfig);
            }
        }
    }

    private static void configureType(
            String type, ToolIntensifyConfig.AttributeConfig attributeConfig) {
        if (type == null) {
            throw new RuntimeException(String.format("Attribute type %s not set", type));
        }
        ResourceLocation resourceLocation = ResourceLocation.parse(type);
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(resourceLocation);
        if (attribute == null) {
            resourceLocation = ResourceLocation.fromNamespaceAndPath("attributeslib", type);
            attribute = BuiltInRegistries.ATTRIBUTE.get(resourceLocation);
        }
        if (attribute == null) {
            throw new RuntimeException(String.format("Attribute type %s not register", type));
        }
        attributeConfig.setType(attribute);
    }

    private static void configureGroups(
            List<Config> growNodes, ToolIntensifyConfig.AttributeConfig attributeConfig) {
        if (growNodes != null) {
            for (Config growNode : growNodes) {
                ToolIntensifyConfig.GrowConfig growConfig = new ToolIntensifyConfig.GrowConfig();

                for (Config.Entry entry : growNode.entrySet()) {
                    String key = entry.getKey();
                    if (key.equals("type")) {
                        ToolIntensifyConfig.GrowTypeEnum growTypeEnum =
                                ToolIntensifyConfig.GrowTypeEnum.valueOf(
                                        entry.getValue().toString().toUpperCase());
                        growConfig.setType(growTypeEnum);
                    } else if (key.equals("range")) {
                        growConfig.setRange(entry.getValue());
                    } else if (key.equals("value")) {
                        growConfig.setValue(entry.getValue());
                    } else if (key.equals("speed")) {
                        Integer speed = entry.getValue();
                        if (speed != null) {
                            growConfig.setSpeed(speed);
                        }
                    } else {
                        throw new RuntimeException(key);
                    }
                }

                attributeConfig.getGrows().add(growConfig);
            }
        }
    }

    private static void configureEneng(
            Config enengNode, ToolIntensifyConfig.AttributeConfig attributeConfig) {
        if (enengNode != null) {
            ToolIntensifyConfig.EnengConfig enengConfig = new ToolIntensifyConfig.EnengConfig();
            for (Config.Entry entry : enengNode.entrySet()) {
                if (entry.getKey().equals("enable")) {
                    enengConfig.setEnable(entry.getValue());
                } else if (entry.getKey().equals("value")) {
                    enengConfig.setValue(entry.getValue());
                } else {
                    throw new RuntimeException(entry.getKey());
                }
            }
            enengConfig.setEnable(enengNode.get("enable"));
            enengConfig.setValue(enengNode.get("value"));
            attributeConfig.setEneng(enengConfig);
        }
    }
}
