package me.lauriichan.build.builder;

public final class Version {

    private final String build;
    private final JavaVersion java;

    private int minClass = 52, maxClass = 52;

    public Version(final String build) {
        this.build = build;
        this.java = detectVersion();
    }

    private JavaVersion detectVersion() {
        final String content = Util.downloadAsString(Constant.getVersionUrl(build));
        if (content == null) {
            return null;
        }
        if (!content.contains("javaVersions")) {
            if (JavaVersion.JAVA_8.available()) {
                return JavaVersion.JAVA_8;
            }
            return null;
        }
        final String versions = content.split("javaVersions")[1];
        final int start = versions.indexOf('[');
        final int end = versions.indexOf("]");
        final String[] parts = versions.substring(start + 1, end).split(",");
        minClass = Integer.parseInt(parts[0].trim());
        maxClass = Integer.parseInt(parts[1].trim());
        for (int feature = maxClass; feature >= minClass; feature--) {
            final JavaVersion version = JavaVersion.getByClassVersion(feature);
            if (version == null || !version.available()) {
                continue;
            }
            return version;
        }
        return null;
    }

    public int getMinClass() {
        return minClass;
    }

    public int getMaxClass() {
        return maxClass;
    }

    public String getBuild() {
        return build;
    }

    public JavaVersion getJava() {
        return java;
    }

    public boolean isValid() {
        return java != null;
    }

}
