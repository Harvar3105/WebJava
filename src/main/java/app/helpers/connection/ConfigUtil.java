package app.helpers.connection;

import app.helpers.connection.ConnectionInfo;

import java.util.Properties;

public final class ConfigUtil {

    public static ConnectionInfo readConnectionInfo(String type) {
        String url;
        if (type.equals("postgres")) {
            url = "dbUrl";
        } else if (type.equals("hsql")) {
            url = "hsqlDBUrl";
        } else {
            throw new RuntimeException("No such type: " + type);
        }

        Properties properties = PropertyLoader.loadApplicationProperties();
        return new ConnectionInfo(
                properties.getProperty(url),
                properties.getProperty("dbUser"),
                properties.getProperty("dbPassword"));
    }

}
