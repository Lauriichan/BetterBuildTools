package me.lauriichan.build.builder;

import java.io.File;
import java.util.HashMap;

public final class JavaVersion {

    public static final JavaVersion JAVA_8 = new JavaVersion(8, 52);

    private static final HashMap<Integer, JavaVersion> CLASS_TO_VERSION = new HashMap<>();
    private static final JavaVersion current = getByFeatureVersion(Runtime.version().feature());

    static {
        CLASS_TO_VERSION.put(JAVA_8.classVersion(), JAVA_8);
    }

    public static final JavaVersion getByClassVersion(int classVersion) {
        JavaVersion version = CLASS_TO_VERSION.get(classVersion);
        if (version != null) {
            return version;
        }
        int difference = classVersion - JAVA_8.classVersion();
        if (difference < 0) {
            return null;
        }
        CLASS_TO_VERSION.put(classVersion, version = new JavaVersion(JAVA_8.featureVersion() + difference, classVersion));
        return version;
    }

    public static final JavaVersion getByFeatureVersion(int featureVersion) {
        int difference = featureVersion - JAVA_8.featureVersion();
        if (difference < 0) {
            return null;
        }
        return getByClassVersion(JAVA_8.classVersion() + difference);
    }

    public static JavaVersion getByString(final String name) {
        String[] parts = name.split("_");
        if (parts.length != 2) {
            return null;
        }
        try {
            return getByFeatureVersion(Integer.parseInt(parts[1]));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static JavaVersion getCurrent() {
        return current;
    }

    private final String name;
    private final int classVersion;
    private final int featureVersion;

    private String path;
    private String homePath;

    JavaVersion(final int majorVersion, final int classVersion) {
        this.name = "Java " + majorVersion;
        this.classVersion = classVersion;
        this.featureVersion = majorVersion;
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

}