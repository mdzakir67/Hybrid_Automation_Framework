package org.ge.vernova.config;

public final class UIConfig {

    private UIConfig() {
    }

    public static String getBaseUrl() {
        return ConfigReader.get("ui.base.url");
    }

    public static String getBrowser() {
        return ConfigReader.get("browser");
    }
}