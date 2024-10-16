package app;

import app.helpers.connection.ConnectionPoolFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@ComponentScan( basePackages = {"app"})
public class MvcConfig {
    @Bean
    public DataSource dataSource() {
        return new ConnectionPoolFactory().createConnectionPool("hsql");
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ObjectMapper objectMapper() {return new ObjectMapper();}
}
