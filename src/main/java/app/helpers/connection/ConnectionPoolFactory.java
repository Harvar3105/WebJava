package app.helpers.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolFactory {

    public DataSource createConnectionPool(String type) {
        BasicDataSource pool = new BasicDataSource();
        if (type.equals("postgres")) {
            pool.setDriverClassName("org.postgresql.Driver");
        } else if (type.equals("hsql")) {
            pool.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        } else {
            throw new RuntimeException("No such type: " + type);
        }

        int maxSize = 1;
        ConnectionInfo connectionInfo = ConfigUtil.readConnectionInfo(type);
        pool.setUrl(connectionInfo.getUrl());
        pool.setUsername(connectionInfo.getUser());
        pool.setPassword(connectionInfo.getPass());
        pool.setMaxTotal(maxSize);
        pool.setInitialSize(1);

        return pool;
    }
}

