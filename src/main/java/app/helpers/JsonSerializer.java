package app.helpers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class JsonSerializer {

    public static List<String> fromJson(String json){
        List<String> result = new ArrayList<>();

        System.out.println("Input data: " + json);

        List<String> data = levelSeparator(json);

        for (String str : data){
//            System.out.println(str);
            String[] parts = str.split(":");
            if (parts[1].startsWith("{")){

            }
        }

        return result;
    }

    private static List<Object> digDeeper(List<Object> list){
        List<Object> result = new ArrayList<>();

        for (Object obj : list){
            String str = (String) obj;
            String[] parts = str.split(":", 1);
            if (parts[1].startsWith("{") && parts[1].endsWith("}")){
                List<String> data = digDeeper(levelSeparator(parts[1]));
            }
        }

        return result;
    }

    private static List<String> levelSeparator(String str){
        List<String> result = new ArrayList<>();
        str = str.substring(1, str.length()-1);
        System.out.println("Substringed: " + str);
        boolean isClosed = true;
        boolean isValue = false;
        int level = 0;
        int position = 0;
        int startPosition = 0;

        for (char c : str.toCharArray()){
            if (c == '"') isClosed = !isClosed;

            if (c == ':' && !isValue && isClosed) {
                isValue = true;
            }
            else if (isValue && isClosed && c == '{') {
                ++level;
            }
            else if (isValue && isClosed && c == '}'){
                --level;
            }
            else if (c == ',' && isValue && level == 0 && isClosed){
                result.add(str.substring(startPosition, position));
                startPosition = position + 1;
                isValue = false;
            }

            position++;
        }
        result.add(str.substring(startPosition));
        System.out.println();
        return result;
    }

    public static String toJson(Map<String, String> data){
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, String> entry : data.entrySet()){
            parts.add('"' + entry.getKey() + "\": \"" + entry.getValue() + '"');
        }

        return "{ " + String.join(", ", parts) + " }";
    }
}
