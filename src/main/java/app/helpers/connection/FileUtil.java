package app.helpers.connection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class FileUtil {

    public static String readFileFromClasspath(String pathOnClasspath) {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(pathOnClasspath)) {

            if (is == null) {
                throw new IllegalStateException("can't load file: " + pathOnClasspath);
            }

            return readStream(is);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readStream(InputStream is) {
        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

}
