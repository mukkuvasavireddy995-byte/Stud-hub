package com.studenthub.config;

public final class DatabaseConfig {
    private DatabaseConfig() {}

    public static String url() {
        return env("DB_URL",
            "jdbc:mysql://localhost:3306/studenthub?useSSL=false&serverTimezone=UTC");
    }

    public static String user() {
        return env("DB_USER", "root");
    }

    public static String password() {
        return env("DB_PASSWORD", "root");
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
