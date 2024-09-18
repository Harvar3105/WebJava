package app.helpers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class JsonSerializer {

    public static Map<String, String> fromJson(String json){
        Map<String, String> result = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()){
            String key = matcher.group(1);
            String value = matcher.group(2);
            result.put(key, value);
        }

        return result;
    }

    public static String toJson(Map<String, String> data){
//        StringBuilder builder = new StringBuilder();
//
//        builder.append('{');
//        for (Map.Entry<String, String> entry : data.entrySet()){
//            builder.append('"')
//                    .append(entry.getKey())
//                    .append("\": \"")
//                    .append(entry.getValue())
//                    .append("\", ");
//        }
//        builder.append('}');
//
//        return builder.toString();

        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, String> entry : data.entrySet()){
            parts.add('"' + entry.getKey() + "\": \"" + entry.getValue() + '"');
        }

        return "{ " + String.join(", ", parts) + " }";
    }
}
