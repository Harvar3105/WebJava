package app.api;

import app.helpers.IdPicker;
import app.helpers.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/orders/form")
public class OrdersFormServlet extends HttpServlet {

    private final IdPicker idPicker = new IdPicker();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = req.getParameter("orderNumber");

        Order order = new Order();
        order.setOrderNumber(number);
        order.setId(idPicker.getNewId());
        getServletContext().setAttribute(String.valueOf(order.getId()), order);

        if (req.getHeader("Accept").equals("application/json")) {
            resp.setContentType("application/json");
            resp.getWriter().print(mapper.writeValueAsString(order));
        } else {
            resp.setContentType("application/x-www-form-urlencoded");
            resp.getWriter().print("id=" + order.getId());
        }
    }
}
