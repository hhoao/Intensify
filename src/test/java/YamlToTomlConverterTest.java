import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.File;
import java.net.MalformedURLException;

public class YamlToTomlConverterTest {
    public static void main(String[] args) throws MalformedURLException {
        TomlParser tomlParser = new TomlParser();
        CommentedConfig parse =
                tomlParser.parse(
                        new File(
                                        "C:\\Users\\haung\\git\\opensource\\Intensify\\src\\main\\resources\\result\\axe.toml")
                                .toURI()
                                .toURL());
        System.out.println(parse);
    }
}
