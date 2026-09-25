package org.ge.vernova.config;

public final class EnvironmentConfig {

    private static final String DEFAULT_ENV = "qa";

    private EnvironmentConfig() {
    }

    public static String getEnvironment() {
        return System.getProperty("env", DEFAULT_ENV);
    }
}
