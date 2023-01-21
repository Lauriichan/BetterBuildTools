package me.lauriichan.build.builder;

import java.util.Properties;

public final class SimpleProperties {

    private final Properties properties;

    public SimpleProperties(final Properties properties) {
        this.properties = properties;
    }

    public Properties properties() {
        return properties;
    }

    public boolean has(final String key) {
        return properties.containsKey(key);
    }

    public String get(final String key) {
        return properties.getProperty(key);
    }

    public String getOrDefault(final String key, final String fallback) {
        return properties.getProperty(key, fallback);
    }

    public int getOrDefault(final String key, final int fallback) {
        final String string = properties.getProperty(key);
        if (string == null || string.isEmpty() || !Util.isNumeric(string)) {
            return fallback;
        }
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException nfe) {
            return fallback;
        }
    }

    public boolean getOrDefault(final String key, final boolean fallback) {
        String string = properties.getProperty(key);
        if (string == null || (string = string.toLowerCase()).isEmpty() || (!string.equals("false") && !string.equals("true"))) {
            return fallback;
        }
        return string.equals("true");
    }

}
