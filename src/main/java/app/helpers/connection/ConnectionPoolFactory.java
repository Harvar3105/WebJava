package app.helpers.connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

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

        populateDatabase(pool);

        return pool;
    }

    public DataSource createConnectionPool(ConnectionInfo ci, String driver, int maxSize, int initialSize){
        BasicDataSource pool = new BasicDataSource();

        pool.setDriverClassName(driver);
        pool.setUrl(ci.getUrl());

        if (ci.getUser() != null) {
            pool.setUsername(ci.getUser());
        }

        if (ci.getPass() != null) {
            pool.setPassword(ci.getPass());
        }

        pool.setMaxTotal(maxSize);
        pool.setInitialSize(initialSize);

        populateDatabase(pool);

        return pool;
    }

    private void populateDatabase(DataSource ds){
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("scheme.sql"));
        populator.execute(ds);
    }
}

