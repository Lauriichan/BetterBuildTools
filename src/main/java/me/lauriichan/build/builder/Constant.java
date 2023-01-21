package me.lauriichan.build.builder;

import java.net.MalformedURLException;
import java.net.URL;

public final class Constant {

    private Constant() {
        throw new UnsupportedOperationException();
    }

    public static final String SPIGOT_VERSION_URL = "https://hub.spigotmc.org/versions/%s.json";
    public static final String SPIGOT_JENKINS_BUILD = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/api/json";
    public static final String SPIGOT_JENKINS_JAR = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";

    public static URL getVersionUrl(final String version) {
        try {
            return new URL(String.format(SPIGOT_VERSION_URL, version));
        } catch (final MalformedURLException e) {
            return null;
        }
    }

}
