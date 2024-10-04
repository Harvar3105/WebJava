package app.helpers.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public final class ConnectionChecker {
    private static void printPoolInfo(DataSource dataSource) {
        if (!(dataSource instanceof BasicDataSource)) {
            throw new IllegalArgumentException("argument must be BasicDataSource");
        }

        BasicDataSource pool = (BasicDataSource) dataSource;

        System.out.printf("active: %s; idle: %s\n",
                pool.getNumActive(), pool.getNumIdle());
    }

}
