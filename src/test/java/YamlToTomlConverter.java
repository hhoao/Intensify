import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.apache.commons.io.FileUtils;
import org.hhoa.mc.intensify.Intensify;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class YamlToTomlConverter {
    public static void main(String[] args) throws URISyntaxException, IOException {
        File resourceDir = new File("src/main/resources");
        File configTemplates = new File(resourceDir, "config_templates");
        File dist = new File(resourceDir, String.format("assets/%s/config/%s", Intensify.MODID, Intensify.MODID));
        FileUtils.deleteDirectory(dist);
        dist.mkdirs();
        for (File listFile : FileUtils.listFiles(configTemplates, null, true)) {
            Map<String, Object> stringObjectMap =
                loadYamlConfig(listFile.getAbsolutePath());
            CommentedConfig commentedConfig = convertMapToConfig(stringObjectMap);
            TomlWriter writer = new TomlWriter();
            String[] split = listFile.getName().split("\\.");
            writer.write(commentedConfig,
                new File(dist.getPath(), split[0] + ".toml"),
                WritingMode.REPLACE);
        }
    }

    public static Map<String, Object> loadYamlConfig(String yamlFilePath) {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(yamlFilePath)) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CommentedConfig convertMapToConfig(Map<String, ?> map) {
        CommentedConfig commentedConfig = CommentedConfig.inMemory();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                CommentedConfig convertMapToConfig = convertMapToConfig((Map<String, ?>) value);
                commentedConfig.set(key, convertMapToConfig);
            } else if (value instanceof List<?>){
                List<?> list = (List<?>) value;
                if (!list.isEmpty() && list.get(0) instanceof Map<?, ?>) {
                    list = list.stream().map(o1 -> convertMapToConfig((Map<String, ?>) o1)).toList();
                }
                commentedConfig.set(key, list);
            } else {
                commentedConfig.set(key, value);
            }
        }
        return commentedConfig;
    }
}
