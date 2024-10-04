package app.api;

import app.dal.OrderRepository;
import app.helpers.IdPicker;
import app.helpers.connection.ConnectionPoolFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/orders/slow")
public class OrdersSlowServlet extends HttpServlet {

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

            Thread.sleep(1000);

            resp.getWriter().print(result);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
