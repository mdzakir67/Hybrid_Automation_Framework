package org.ge.vernova.config;

public final class ApiConfig {

    private ApiConfig(){}

    public static String getBaseUrl() {
        return ConfigReader.get("api.base.url");
    }

}