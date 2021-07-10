package com.github.codeboy.mcide;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Util {
    public static String readFile(File file){
        Path path = Paths.get(file.getPath());

        StringBuilder builder=new StringBuilder();
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(builder::append);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }
}
