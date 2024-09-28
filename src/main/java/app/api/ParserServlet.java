package app.api;

import app.helpers.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/parser")
public class ParserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getReader().lines().collect(Collectors.joining("\n"));

        Map<String, Object> result = JsonSerializer.fromJson(data);

        System.out.println("----------Result----------");
        System.out.println(result);
//
//        StringBuilder builder = new StringBuilder();
//        for (Map.Entry<String, Object> part : result.entrySet()){
//            System.out.println(part);
//            builder.append("\"").append(part.getKey()).append("\":\"").append(part.getValue()).append("\", \n");
//        }

        resp.getWriter().println(JsonSerializer.toJson(result));
    }
}
