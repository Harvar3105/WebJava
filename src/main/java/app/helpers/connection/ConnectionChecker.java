package app.helpers.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public final class ConnectionChecker {
    public static State printPoolInfo(DataSource dataSource) {
        if (!(dataSource instanceof BasicDataSource)) {
            throw new IllegalArgumentException("argument must be BasicDataSource");
        }

        BasicDataSource pool = (BasicDataSource) dataSource;

//        return String.format("inPool: %s, inUse: %s\n",
//                pool.getNumIdle(), pool.getNumActive());
        return new State(pool.getNumIdle(), pool.getNumActive());
    }

    public record State(int inPool, int inUse){

    }
}
