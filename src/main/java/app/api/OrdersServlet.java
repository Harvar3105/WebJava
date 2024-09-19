package app.api;

import app.helpers.IdPicker;
import app.helpers.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/orders")
public class OrdersServlet extends HttpServlet {

    private final IdPicker idPicker = new IdPicker();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getReader().lines().collect(Collectors.joining("\n"));

        Map<String, String> parts = new LinkedHashMap<>(JsonSerializer.fromJson(data)) ;
        parts.put("id", String.valueOf(idPicker.getNewId()));
        String answer = JsonSerializer.toJson(parts);

        resp.setContentType("application/json");
        resp.getWriter().print(answer);
    }
}
