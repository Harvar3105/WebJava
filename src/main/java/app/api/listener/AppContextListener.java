package app.api.listener;

import app.MvcConfig;
import app.dal.OrderRepository;
import app.helpers.connection.ConnectionPoolFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.sql.DataSource;

@WebListener
public class AppContextListener implements ServletContextListener {

    private AnnotationConfigWebApplicationContext ctx;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(MvcConfig.class);
        ctx.setServletContext(sce.getServletContext());
        ctx.refresh();
        ctx.getBeansWithAnnotation(RestController.class);

        populateDatabase(ctx.getBean(DataSource.class));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (ctx != null) {
            ctx.close();
        }
    }

    private void populateDatabase(DataSource ds){
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("scheme.sql"));
        populator.execute(ds);
    }
}
