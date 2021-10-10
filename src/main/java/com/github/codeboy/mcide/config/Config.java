package com.github.codeboy.mcide.config;

import ml.codeboy.bukkitbootstrap.config.ConfigValue;

public class Config {
    @ConfigValue
    public static String language = "english";

    @ConfigValue(key = "piston.endpoint")
    public static String pistonEndPoint = "https://emkc.org/api/v2/piston";
    @ConfigValue(key = "piston.apiKey")
    public static String pistonApiKey = "";
}
