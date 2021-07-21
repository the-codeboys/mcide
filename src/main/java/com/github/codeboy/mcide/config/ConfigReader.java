package com.github.codeboy.mcide.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class ConfigReader {

    public static void readConfig(Class<?> saveTo, File file) {
        System.out.println("Reading config " + file.getName());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        boolean changed = false;

        for (Field field : saveTo.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                field.setAccessible(true);

                String path = field.getAnnotation(ConfigValue.class).key();
                if (path.equals(""))
                    path = field.getName();

                try {
                    if (config.contains(path))
                        field.set(null, getValue(config, path));
                    else {
                        config.set(path, field.get(null));
                        changed = true;
                    }
                    System.out.println(field.getName() + ": " + field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (changed) {
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> T getValue(FileConfiguration config, String path) {
        return (T) config.get(path);
    }
}
