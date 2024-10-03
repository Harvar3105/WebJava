package app.helpers.connection;

import app.helpers.connection.ConnectionInfo;

import java.util.Properties;

public final class ConfigUtil {

    public static ConnectionInfo readConnectionInfo() {
        Properties properties = PropertyLoader.loadApplicationProperties();

        return new ConnectionInfo(
                properties.getProperty("dbUrl"),
                properties.getProperty("dbUser"),
                properties.getProperty("dbPassword"));
    }

}
