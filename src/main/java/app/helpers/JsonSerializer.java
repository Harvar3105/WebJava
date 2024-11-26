package app.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonSerializer {

    public static Map<String, Object> fromJson(String json) {
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
            isClosed = checkClosing(c, isClosed);

            if (c == ':' && !isValue && isClosed) {
                isValue = true;
            } else if (isValue && isClosed && (c == '{' || c == '}')) {
                level += checkLevelChange(c);
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

    private static int checkLevelChange(char c){
        if (c == '{') {
            return 1;
        }
        return -1;
    }

    private static boolean checkClosing(char c, boolean cState){
        if (c == '"') {
            cState = !cState;
        }
        return cState;
    }

    public static String toJson(Map<String, Object> data){
        String unedited = buildAnswer(data);
        StringBuilder builder = new StringBuilder(unedited);

        builder.deleteCharAt(builder.lastIndexOf(","));

        return builder.toString();
    }

    public static String buildAnswer(Map<String, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> part : data.entrySet()){
            if (part.getValue() instanceof Map<?, ?> map){
                if (map.keySet().stream().allMatch(k -> k instanceof String)) {
                    Map<String, Object> stringMap = (Map<String, Object>) map;
                    builder.append("\"").append(part.getKey()).append("\":{\n");
                    builder.append(toJson(stringMap)).append("},\n");
                    continue;
                }
                throw new RuntimeException("Key is not string? " + map);
            }
            builder.append("\"").append(part.getKey()).append("\":").append(part.getValue().toString()).append(", \n");
        }

        return builder.substring(0, builder.length() - 1);
    }
}
