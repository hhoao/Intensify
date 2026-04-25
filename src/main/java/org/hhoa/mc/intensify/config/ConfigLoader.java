package org.hhoa.mc.intensify.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import org.hhoa.mc.intensify.Intensify;

public class ConfigLoader {
    private static final Map<String, IAttribute> ATTRIBUTE_BY_NAME = new HashMap<>();

    static {
        registerVanilla("generic.attack_damage", SharedMonsterAttributes.ATTACK_DAMAGE);
        registerVanilla("generic.attack_speed", SharedMonsterAttributes.ATTACK_SPEED);
        registerVanilla("generic.max_health", SharedMonsterAttributes.MAX_HEALTH);
        registerVanilla("generic.armor", SharedMonsterAttributes.ARMOR);
        registerVanilla("generic.armor_toughness", SharedMonsterAttributes.ARMOR_TOUGHNESS);
        registerVanilla("generic.knockback_resistance", SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        registerVanilla("generic.movement_speed", SharedMonsterAttributes.MOVEMENT_SPEED);
        registerVanilla("generic.luck", SharedMonsterAttributes.LUCK);

        registerCustom("armor_pierce", ALObjects.Attributes.ARMOR_PIERCE.get());
        registerCustom("armor_shred", ALObjects.Attributes.ARMOR_SHRED.get());
        registerCustom("arrow_damage", ALObjects.Attributes.ARROW_DAMAGE.get());
        registerCustom("arrow_velocity", ALObjects.Attributes.ARROW_VELOCITY.get());
        registerCustom("crit_chance", ALObjects.Attributes.CRIT_CHANCE.get());
        registerCustom("crit_damage", ALObjects.Attributes.CRIT_DAMAGE.get());
        registerCustom("current_hp_damage", ALObjects.Attributes.CURRENT_HP_DAMAGE.get());
        registerCustom("dodge_chance", ALObjects.Attributes.DODGE_CHANCE.get());
        registerCustom("draw_speed", ALObjects.Attributes.DRAW_SPEED.get());
        registerCustom("experience_gained", ALObjects.Attributes.EXPERIENCE_GAINED.get());
        registerCustom("ghost_health", ALObjects.Attributes.GHOST_HEALTH.get());
        registerCustom("healing_received", ALObjects.Attributes.HEALING_RECEIVED.get());
        registerCustom("life_steal", ALObjects.Attributes.LIFE_STEAL.get());
        registerCustom("mining_speed", ALObjects.Attributes.MINING_SPEED.get());
        registerCustom("overheal", ALObjects.Attributes.OVERHEAL.get());
    }

    public static List<ToolIntensifyConfig> loadToolIntensifyConfigFromDir(String dir) {
        TomlParser tomlParser = new TomlParser();
        List<ToolIntensifyConfig> toolIntensifyConfigs = new ArrayList<>();
        for (String configName : IntensifyConstants.TOOL_CONFIG_RESOURCE_NAMES) {
            String resourcePath =
                    "/assets/" + Intensify.MODID + "/" + dir + "/" + configName + ".toml";
            try (InputStream inputStream = ConfigLoader.class.getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    continue;
                }

                CommentedConfig tomlConfig = tomlParser.parse(inputStream);
                Set<Map.Entry<String, Object>> entries = tomlConfig.valueMap().entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    toolIntensifyConfigs.add(loadToolIntensifyConfig(tomlConfig, entry.getKey()));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error loading config " + resourcePath, e);
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
        IAttribute attribute = ATTRIBUTE_BY_NAME.get(type);
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

    private static void registerVanilla(String key, IAttribute attribute) {
        ATTRIBUTE_BY_NAME.put(key, attribute);
    }

    private static void registerCustom(String key, IAttribute attribute) {
        ATTRIBUTE_BY_NAME.put(key, attribute);
        ATTRIBUTE_BY_NAME.put("attributeslib." + key, attribute);
    }
}
