package me.lauriichan.build.builder;

import java.io.File;

public enum JavaVersion {

    JAVA_8(52),
    JAVA_9(53),
    JAVA_10(54),
    JAVA_11(55),
    JAVA_12(56),
    JAVA_13(57),
    JAVA_14(58),
    JAVA_15(59),
    JAVA_16(60),
    JAVA_17(61),
    JAVA_18(62),
    JAVA_19(63);

    private static final JavaVersion[] versions = values();
    private static final JavaVersion current = getByFeatureVersion(Runtime.version().feature());

    private final String name;
    private final int classVersion;
    private final int featureVersion;

    private String path;
    private String homePath;

    JavaVersion(final int classVersion) {
        final String[] parts = super.name().split("_");
        this.name = parts[0].charAt(0) + parts[0].substring(1).toLowerCase() + ' ' + parts[1];
        this.classVersion = classVersion;
        this.featureVersion = Integer.parseInt(parts[1]);
    }

    public boolean path(String path) {
        File file = new File(path, "bin/java.exe");
        if (!file.exists()) {
            file = new File(path, "bin/java");
            if (!file.exists()) {
                return false;
            }
        }
        String homePath = path;
        path = file.getPath();
        try {
            Runtime.getRuntime().exec(new String[] {
                path,
                "-version"
            });
        } catch (final Exception e) {
            return false;
        }
        this.path = path;
        this.homePath = homePath;
        return true;
    }

    public String path() {
        return path;
    }
    
    public String homePath() {
        return homePath;
    }

    public boolean available() {
        return path != null;
    }

    public int classVersion() {
        return classVersion;
    }

    public int featureVersion() {
        return featureVersion;
    }

    @Override
    public String toString() {
        return name;
    }

    public static JavaVersion getCurrent() {
        return current;
    }

    public static JavaVersion getByFeatureVersion(final int featureVersion) {
        for (final JavaVersion version : versions) {
            if (version.featureVersion == featureVersion) {
                return version;
            }
        }
        return null;
    }

    public static JavaVersion getByClassVersion(final int classVersion) {
        for (final JavaVersion version : versions) {
            if (version.classVersion == classVersion) {
                return version;
            }
        }
        return null;
    }

    public static JavaVersion getByString(final String name) {
        try {
            return JavaVersion.valueOf(name.toUpperCase());
        } catch (final IllegalArgumentException iae) {
            return null;
        }
    }

}