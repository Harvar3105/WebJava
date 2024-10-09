package app.api;

import app.dal.OrderRepository;
import app.helpers.IdPicker;
import app.helpers.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/orders/bulk")
public class OrdersBulkServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getReader().lines().collect(Collectors.joining("\n"));
        resp.setContentType("application/json");

        try {
            OrderRepository rep = (OrderRepository) getServletContext().getAttribute("rep");

            List<Order> order = mapper.readValue(data, new TypeReference<List<Order>>() {});

            order = rep.insertOrderBulk(order);
            String res = mapper.writeValueAsString(order);
            resp.getWriter().print(res);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
