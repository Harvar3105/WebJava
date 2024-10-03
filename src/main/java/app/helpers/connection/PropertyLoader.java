package app.helpers.connection;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public final class PropertyLoader {

    public static Properties loadApplicationProperties() {

        String contents = FileUtil.readFileFromClasspath("application.properties");

        Properties properties = new Properties();

        try {

            properties.load(new StringReader(contents));

            return properties;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
