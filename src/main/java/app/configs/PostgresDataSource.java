package app.configs;

import app.helpers.connection.ConnectionInfo;
import app.helpers.connection.ConnectionPoolFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:/application.properties")
public class PostgresDataSource {

    @Bean(name = "PostgresDataSource")
    public DataSource dataSource(Environment env) {
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName("org.postgresql.Driver");
//        ds.setUsername(env.getProperty("postgres.user"));
//        ds.setPassword(env.getProperty("postgres.pass"));
//        ds.setUrl(env.getProperty("postgres.url"));

        var ci = new ConnectionInfo(env.getProperty("hsqlDBUrl"), env.getProperty("dbUser"), env.getProperty("dbPassword"));
        return new ConnectionPoolFactory().createConnectionPool(ci, "org.hsqldb.jdbcDriver", 1, 1);
    }

    @Bean("dialect")
    public String dialect() {
        return "org.hibernate.dialect.PostgreSQLDialect";
    }
}
