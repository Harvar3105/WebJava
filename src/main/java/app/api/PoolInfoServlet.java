package app.api;

import app.dal.OrderRepository;
import app.helpers.connection.ConnectionChecker;
import app.helpers.connection.ConnectionPoolFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/pool/info")
public class PoolInfoServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        if (getServletContext().getAttribute("rep") == null){
//            DataSource pool;
//            if (getServletContext().getAttribute("pool") == null){
//                pool = new ConnectionPoolFactory().createConnectionPool();
//                getServletContext().setAttribute("pool", pool);
//            } else {
//                pool = (DataSource) getServletContext().getAttribute("pool");
//            }
//
//            OrderRepository rep = new OrderRepository(pool);
//            getServletContext().setAttribute("rep", rep);
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        DataSource pool = (DataSource) getServletContext().getAttribute("pool");

        ConnectionChecker.State res = ConnectionChecker.printPoolInfo(pool);
        resp.getWriter().print(mapper.writeValueAsString(res));
    }
}
