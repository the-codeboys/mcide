package com.github.codeboy.mcide.config;

import ml.codeboy.bukkitbootstrap.config.ConfigScope;
import ml.codeboy.bukkitbootstrap.config.ConfigValue;
import ml.codeboy.bukkitbootstrap.config.Configurable;

@Configurable(name = "config.yml",comments = "Edit these values however you like\n" +
        "They should be self explanatory",scope = ConfigScope.PUBLIC)
public class Config {
    public static String language = "english";

    @ConfigValue(key = "piston.endpoint")
    public static String pistonEndPoint = "https://emkc.org/api/v2/piston";
    @ConfigValue(key = "piston.apiKey")
    public static String pistonApiKey = "";
}
