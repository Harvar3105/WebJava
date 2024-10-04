package app.api.listener;

import app.dal.OrderRepository;
import app.helpers.connection.ConnectionPoolFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Создание пула соединений и репозитория один раз при старте приложения
        var pool = new ConnectionPoolFactory().createConnectionPool();
        sce.getServletContext().setAttribute("pool", pool);

        OrderRepository rep = new OrderRepository(pool);
        sce.getServletContext().setAttribute("rep", rep);
    }
}
