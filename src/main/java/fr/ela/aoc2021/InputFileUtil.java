package fr.ela.aoc2021;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InputFileUtil {

    private static String getDirectoryName(Class clazz) {
        return clazz.getSimpleName();
    }

    private static String getFileName(String name) {
        String fileName = name;
        if (Boolean.parseBoolean(System.getProperty("test", "false"))) {
            fileName = fileName.concat("-test");
        }
        return fileName.concat(".txt");
    }

    public static Path getPath(Class clazz, String name) {
        return Paths.get("target", "classes", getDirectoryName(clazz), getFileName(name));
    }

    public static Path getInputPath(Class clazz) {
        return getPath(clazz, "input");
    }


}
