import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class YamlToTomlConverterTest {
    public static void main(String[] args) throws MalformedURLException {
        TomlParser tomlParser = new TomlParser();
        CommentedConfig parse = tomlParser.parse(
            new File("C:\\Users\\haung\\git\\opensource\\Intensify\\src\\main\\resources\\result\\axe.toml").toURI().toURL());
        System.out.println(parse);
    }
}
