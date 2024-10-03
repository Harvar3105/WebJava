package app.api;

import app.dal.OrderRepository;
import app.helpers.IdPicker;
import app.helpers.Order;
import app.helpers.connection.ConnectionPoolFactory;
import app.helpers.connection.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@WebServlet("/api/orders")
public class OrdersServlet extends HttpServlet {

    private final IdPicker idPicker = new IdPicker();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        var pool = new ConnectionPoolFactory().createConnectionPool();
        getServletContext().setAttribute("pool", pool);
        OrderRepository rep = new OrderRepository(pool);
        getServletContext().setAttribute("rep", rep);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            OrderRepository rep = (OrderRepository) getServletContext().getAttribute("rep");

            String result;
            if (req.getParameter("id") == null){
                result = mapper.writeValueAsString(rep.getAll(true));
            } else {
                result = mapper.writeValueAsString(rep.getById(Long.parseLong(req.getParameter("id")), true));
            }

            resp.getWriter().print(result);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getReader().lines().collect(Collectors.joining("\n"));

        resp.setContentType("application/json");

        try {
            OrderRepository rep = (OrderRepository) getServletContext().getAttribute("rep");

            Order order = mapper.readValue(data, Order.class);

            long id = rep.insertOrder(order);
            order.setId(id);
            String res = mapper.writeValueAsString(order);
            resp.getWriter().print(res);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void prepareScheme(DataSource ds) throws SQLException {
        try (Connection con = ds.getConnection();
             Statement st = con.createStatement()) {
            String sql = FileUtil.readFileFromClasspath("scheme.sql");

            st.executeUpdate(sql);

        }
    }
}
