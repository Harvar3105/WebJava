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
        String output = '[' + built + ']';
        resp.getWriter().println(output);
    }


    private String buildAnswer(Map<String, Object> data) {
        StringBuilder builder = new StringBuilder();
        Queue<Map.Entry<Map<String, Object>, Integer>> queue = new LinkedList<>();
        queue.add(Map.entry(data, 1));

        while (!queue.isEmpty()) {
            Map.Entry<Map<String, Object>, Integer> current = queue.poll();
            Map<String, Object> currentData = current.getKey();
            int depth = current.getValue();

            for (Map.Entry<String, Object> part : currentData.entrySet()) {
                Object value = part.getValue();
                if (value instanceof Map<?, ?> map) {
                    if (map.keySet().stream().allMatch(k -> k instanceof String)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> stringMap = (Map<String, Object>) map;
                        queue.add(Map.entry(stringMap, depth + 1));
                    } else {
                        throw new RuntimeException("Key is not string? " + map);
                    }
                } else if (value instanceof Number number) {
                    builder.append(number.intValue() * depth).append(", ");
                } else {
                    builder.append("\"")
                            .append(value.toString()
                                    .replaceAll("\"", ""))
                            .append("\", ");
                }
            }
        }

        builder.delete(builder.length() - 2, builder.length());

        return builder.toString();
    }
}
