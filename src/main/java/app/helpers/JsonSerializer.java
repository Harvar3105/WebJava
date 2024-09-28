package app.helpers;

import java.util.*;

public final class JsonSerializer {

    public static Map<String, Object> fromJson(String json) {
        System.out.println("Input data: " + json);
        return parseObject(json.substring(1, json.length() - 1));
    }

    private static Map<String, Object> parseObject(String str) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> data = levelSeparator(str);

        for (String entry : data) {
            String[] parts = entry.split(":", 2);
            String key = parts[0].trim().replace("\"", "");
            String value = parts[1].trim();

            if (value.startsWith("{")) {
                result.put(key, parseObject(value.substring(1, value.length() - 1)));
            } else {
                result.put(key, parseValue(value));
            }
        }

        return result;
    }

    private static Object parseValue(String value) {
        value = value.trim();
        if (value.matches("-?\\d+(\\.\\d+)?")) {
            // Если это число
            return Integer.parseInt(value);
        }
        return value;
    }

    private static List<String> levelSeparator(String str) {
        List<String> result = new ArrayList<>();
        boolean isClosed = true;
        boolean isValue = false;
        int level = 0;
        int position = 0;
        int startPosition = 0;

        for (char c : str.toCharArray()) {
            if (c == '"') isClosed = !isClosed;

            if (c == ':' && !isValue && isClosed) {
                isValue = true;
            } else if (isValue && isClosed && c == '{') {
                ++level;
            } else if (isValue && isClosed && c == '}') {
                --level;
            } else if (c == ',' && isValue && level == 0 && isClosed) {
                result.add(str.substring(startPosition, position));
                startPosition = position + 1;
                isValue = false;
            }
            position++;
        }
        result.add(str.substring(startPosition));
        return result;
    }

    public static String toJson(Map<String, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> part : data.entrySet()){
            if (part.getValue() instanceof Map<?, ?> map){
                if (map.keySet().stream().allMatch(k -> k instanceof String)) {
                    Map<String, Object> stringMap = (Map<String, Object>) map;
                    builder.append(toJson(stringMap));
                    continue;
                }
                throw new RuntimeException("Key is not string? " + map);
            }
            System.out.println(part);
            builder.append("\"").append(part.getKey()).append("\":\"").append(part.getValue()).append("\", \n");
        }

        return builder.toString();
    }
}
