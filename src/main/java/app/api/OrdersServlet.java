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
//        System.out.println("Data: " + data);

        LinkedHashMap<String, String> parts = JsonSerializer.fromJson(data);
//        System.out.println("Parts before: " + parts);
        parts.putFirst("id", String.valueOf(idPicker.getNewId()));
//        System.out.println(parts);
        String answer = JsonSerializer.toJson(parts);
//        System.out.println(answer);

//        System.out.println("Request: ");
//        System.out.println(req);
//        System.out.println(req.getContentType());
//        System.out.println(req.getMethod());
//        resp.setContentType("application/json");
        resp.getWriter().print(answer);
    }
}
