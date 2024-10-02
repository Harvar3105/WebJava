package app.api;

import app.helpers.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/parser")
public class ParserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getReader().lines().collect(Collectors.joining("\n"));
        resp.setContentType("application/json");
        Map<String, Object> result = JsonSerializer.fromJson(data);

        String built = buildAnswer(result);
        String output = '[' + built.substring(0, built.length() - 1) + ']';
        resp.getWriter().println(output);
    }


    private String buildAnswer(Map<String, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> part : data.entrySet()){
            if (part.getValue() instanceof Map<?, ?> map){
                if (map.keySet().stream().allMatch(k -> k instanceof String)) {
                    Map<String, Object> stringMap = (Map<String, Object>) map;
                    builder.append(buildAnswer(stringMap));
                    continue;
                }
                throw new RuntimeException("Key is not string? " + map);
            }
            builder.append("\"")
                    .append(part.getValue().toString()
                            .replaceAll("\"", ""))
                    .append("\",");
        }

        return builder.toString();
    }
}
