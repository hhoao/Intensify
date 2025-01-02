package org.hhoa.mc.intensify.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.intensify.config.ToolIntensifyConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigLoader {
    public static List<ToolIntensifyConfig> loadToolIntensifyConfigFromDir(String dir) {
        TomlParser tomlParser = new TomlParser();
        Map<ResourceLocation, Resource> resourceLocationResourceMap = Minecraft.getInstance()
            .getResourceManager().listResources(dir, (resourceLocation) -> true);
        List<ToolIntensifyConfig> toolIntensifyConfigs = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Resource> resourceLocationResourceEntry : resourceLocationResourceMap.entrySet()) {
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

    private static ToolIntensifyConfig loadToolIntensifyConfig(CommentedConfig tomlConfig, String name) {
        ToolIntensifyConfig toolIntensifyConfig = new ToolIntensifyConfig();
        toolIntensifyConfig.setName(name);
        Config swordNode = tomlConfig.get(name);
        if (swordNode != null) {
            toolIntensifyConfig.setEnable(swordNode.get("enable"));

            getAndSetAttributes(swordNode, toolIntensifyConfig);
        }
        return toolIntensifyConfig;
    }

    private static void getAndSetAttributes(Config swordNode, ToolIntensifyConfig toolIntensifyConfig) {
        List<Config> attributes = swordNode.get("attributes");
        if (attributes != null) {
            for (Config attrNode : attributes) {
                ToolIntensifyConfig.AttributeConfig attributeConfig = new ToolIntensifyConfig.AttributeConfig();
                String type = attrNode.get("type");
                if (type == null) {
                    throw new RuntimeException(String.format("Attribute type %s not set", attrNode));
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

                getAndSetEneng(attrNode, attributeConfig);

                configureGroups(attrNode, attributeConfig);

                toolIntensifyConfig.getAttributes().add(attributeConfig);
            }
        }
    }

    private static void configureGroups(Config attrNode, ToolIntensifyConfig.AttributeConfig attributeConfig) {
        List<Config> growNodes = attrNode.get("grows");
        if (growNodes != null) {
            for (Config growNode : growNodes) {
                ToolIntensifyConfig.GrowConfig growConfig = new ToolIntensifyConfig.GrowConfig();

                growConfig.setType(growNode.getEnum("type", ToolIntensifyConfig.GrowTypeEnum.class));
                growConfig.setRange(growNode.get("range"));
                growConfig.setValue(growNode.get("value"));
                Integer speed = growNode.get("speed");
                if (speed != null) {
                    growConfig.setSpeed(speed);
                }
                attributeConfig.getGrows().add(growConfig);
            }
        }
    }

    private static void getAndSetEneng(Config attrNode, ToolIntensifyConfig.AttributeConfig attributeConfig) {
        Config enengNode = attrNode.get("eneng");
        if (enengNode != null) {
            ToolIntensifyConfig.EnengConfig enengConfig = new ToolIntensifyConfig.EnengConfig();
            enengConfig.setEnable(enengNode.get("enable"));
            enengConfig.setValue(enengNode.get("value"));
            attributeConfig.setEneng(enengConfig);
        }
    }
}
