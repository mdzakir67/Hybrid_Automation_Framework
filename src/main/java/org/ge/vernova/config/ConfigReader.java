package org.ge.vernova.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    private static Properties loadProperties() {

        String environment = EnvironmentConfig.getEnvironment();
        String fileName = "config/" + environment + ".properties";

        Properties properties = new Properties();

        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException(
                        "Configuration file not found: " + fileName
                );
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load configuration: " + fileName,
                    e
            );
        }
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null) {
            throw new RuntimeException(
                    "Configuration key not found: " + key
            );
        }

        return value;
    }
}