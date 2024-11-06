package app.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableWebMvc
@ComponentScan( basePackages = {"app"})
@PropertySource("classpath:/application.properties")
//@Import({HsqlDataSource.class, PostgresDataSource.class})
@EnableTransactionManagement
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/**");
    }

    @Bean
    public ObjectMapper objectMapper() {return new ObjectMapper();}

    @Bean
    public EntityManagerFactory entityManagerFactory(
//            @Qualifier("HsqlDataSource") DataSource dataSource,
            @Qualifier("PostgresDataSource") DataSource dataSource,
//            @Qualifier("HSQLdialect") String  dialect
            @Qualifier("PostgreSQLdialect") String  dialect
    ) {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceProviderClass(
                HibernatePersistenceProvider.class);
        factory.setPackagesToScan("app.entities");
        factory.setDataSource(dataSource);
        factory.setJpaProperties(additionalProperties(dialect));
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }

    private Properties additionalProperties(String dialect) {
        Properties properties = new Properties();
//        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.hbm2ddl.auto", "none"); //Causes invalid scheme generation
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);

        return properties;
    }
}
