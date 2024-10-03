package app.helpers.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ConnectionPoolFactory {

    public DataSource createConnectionPool() {
        ConnectionInfo connectionInfo = ConfigUtil.readConnectionInfo();

        BasicDataSource pool = new BasicDataSource();
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setUrl(connectionInfo.getUrl());
        pool.setUsername(connectionInfo.getUser());
        pool.setPassword(connectionInfo.getPass());
        pool.setMaxTotal(2);
        pool.setInitialSize(1);

        try {
            // has the side effect of initializing the connection pool
            pool.getLogWriter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pool;
    }
}

