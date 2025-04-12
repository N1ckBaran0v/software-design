package traintickets.control.configuration;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.IOException;

public final class ConfigParser {
    public static AppConfig parseFile(String fileName) {
        try (var input = new FileInputStream(fileName)) {
            var parser = new Yaml(new Constructor(AppConfig.class, new LoaderOptions()));
            return parser.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
