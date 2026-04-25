package org.hhoa.mc.intensify.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

public class ConfigLoader {
    public static List<ToolIntensifyConfig> loadToolIntensifyConfigFromDir(String dir) {
        TomlParser tomlParser = new TomlParser();
        Map<ResourceLocation, Resource> resourceLocationResourceMap =
                Minecraft.getInstance()
                        .getResourceManager()
                        .listResources(dir, (resourceLocation) -> true);
        List<ToolIntensifyConfig> toolIntensifyConfigs = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Resource> resourceLocationResourceEntry :
                resourceLocationResourceMap.entrySet()) {
            Resource resource = resourceLocationResourceEntry.getValue();
            try (BufferedReader bufferedReader = resource.openAsReader()) {
                CommentedConfig tomlConfig = tomlParser.parse(bufferedReader);

                Map<String, Object> toolConfig = tomlConfig.valueMap();
                Set<Map.Entry<String, Object>> entries = toolConfig.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    toolIntensifyConfigs.add(loadToolIntensifyConfig(tomlConfig, entry.getKey()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return toolIntensifyConfigs;
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
        ResourceLocation resourceLocation = new ResourceLocation(type);
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
        if (attribute == null) {
            resourceLocation = new ResourceLocation("attributeslib", type);
            attribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
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
