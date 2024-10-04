package app.helpers.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolFactory {

    public DataSource createConnectionPool() {
        ConnectionInfo connectionInfo = ConfigUtil.readConnectionInfo();

        int maxSize = 2;

        BasicDataSource pool = new BasicDataSource();
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setUrl(connectionInfo.getUrl());
        pool.setUsername(connectionInfo.getUser());
        pool.setPassword(connectionInfo.getPass());
        pool.setMaxTotal(maxSize);
        pool.setInitialSize(1);

        try {
            Connection[] initialize = new Connection[maxSize];
            for (int i = 0; i < maxSize; i++){
                initialize[i] = pool.getConnection();
            }
            for (Connection c : initialize){
                c.close();
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }


//        try {
//            // has the side effect of initializing the connection pool
//            PrintWriter writer = pool.getLogWriter();
//            writer.close();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        return pool;
    }
}

