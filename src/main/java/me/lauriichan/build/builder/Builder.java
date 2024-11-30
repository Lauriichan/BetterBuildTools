package me.lauriichan.build.builder;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import me.lauriichan.build.builder.ui.Dialogs;
import me.lauriichan.build.builder.ui.ProgressUI;
import me.lauriichan.build.builder.ui.Dialogs.ITask;
import me.lauriichan.build.builder.ui.util.ColorParser;
import me.lauriichan.build.builder.ui.util.Ref;
import me.lauriichan.build.builder.ui.window.ui.component.LogDisplay;

public final class Builder {

    private Builder() {}

    private static final class Printer implements Consumer<String> {

        private final LogDisplay display;
        private final Color color;

        public Printer(final LogDisplay display, final String color) {
            this.display = display;
            this.color = ColorParser.parse(color);
        }

        @Override
        public void accept(String string) {
            display.log(color, string);
        }

    }

    public static void main(final String[] args) {
        final SimpleProperties builder = loadProperties("builder");
        loadJava();
        final Version[] versions = loadVersions(builder, args);
        if (versions.length == 0) {
            System.out.println("ERROR: No versions available to build");
            return;
        }
        final String ram = builder.getOrDefault("ram", "4G");
        final File errorDir = createDir(new File(builder.getOrDefault("error-directory", "Error")));
        final File workDir = createDir(new File(builder.getOrDefault("buildtools-directory", "Build")));
        final File outputDir = createDir(new File(builder.getOrDefault("output-directory", "Jars")));

        final File buildToolsFile = ensureBuildTools(builder.getOrDefault("update-buildstools", true), workDir);
        if (!buildToolsFile.exists()) {
            System.err.println("ERROR: Couldn't find BuildTools.jar at '" + buildToolsFile.getAbsolutePath() + "'!");
            return;
        }
        final String path = buildToolsFile.getAbsolutePath();

        final String outputDirPath = outputDir.getAbsolutePath();
        final boolean clean = ensureSafeClean(builder.getOrDefault("clean-directory", false), workDir, new File(""));

        String[] arguments = buildArgumentBase(builder);

        ProgressUI progressUi = new ProgressUI(versions.length);
        StreamReader infoReader = new StreamReader(new Printer(progressUi.getDisplay(), "E1E1E1"));
        StreamReader errorReader = new StreamReader(new Printer(progressUi.getDisplay(), "FF5A11"));
        infoReader.start();
        errorReader.start();

        Ref<Integer> failed = Ref.of();
        Ref<Process> currentProcess = Ref.of();
        final Thread thread = new Thread(() -> {
            int fails = 0;
            for (final Version version : versions) {
                if (!progressUi.isAlive()) {
                    break;
                }
                HashMap<String, String> map = new HashMap<>(System.getenv());
                map.put("JAVA_HOME", version.getJava().homePath());
                String[] processEnv = map.entrySet().stream().map(entry -> entry.getKey() + '=' + entry.getValue()).toArray(String[]::new);
                String[] baseArgs = new String[] {
                    version.getJava().path(),
                    "-Xmx" + ram,
                    "-jar",
                    path,
                    "--rev",
                    version.getBuild(),
                    "--remapped",
                    "--output-dir",
                    outputDirPath
                };
                String[] processArgs = new String[arguments.length + baseArgs.length];
                System.arraycopy(baseArgs, 0, processArgs, 0, baseArgs.length);
                System.arraycopy(arguments, 0, processArgs, baseArgs.length, arguments.length);
                baseArgs = null;
                progressUi.setBuild(version.getBuild() + " (" + version.getJava().toString() + ")");
                Process process;
                try {
                    process = Runtime.getRuntime().exec(processArgs, processEnv, workDir);
                    currentProcess.set(process);
                } catch (final IOException e) {
                    System.out.println("WARNING: Failed to build version '" + version.getBuild() + "'!");
                    System.out.println("WARNING: " + e.getMessage());
                    progressUi.increment();
                    continue;
                }
                processEnv = null;
                infoReader.setStream(process.getInputStream());
                errorReader.setStream(process.getErrorStream());
                process.onExit().join();
                progressUi.increment();
                infoReader.await();
                errorReader.await();
                try {
                    if (process.exitValue() == 0) {
                        System.out.println("INFO: Build of version '" + version.getBuild() + "' was successful!");
                        continue;
                    }
                    fails++;
                    System.out.println("WARNING: Build of version '" + version.getBuild() + "' was unsuccessful!");
                    File buildToolsLog = findLogFile(workDir);
                    if (buildToolsLog == null) {
                        System.out.println("WARNING: Couldn't find log file!");
                        continue;
                    }
                    File errorLocation = new File(errorDir, version.getBuild() + '-' + buildToolsLog.getName());
                    if (errorLocation.exists()) {
                        errorLocation.delete();
                    } else if (errorLocation.getParent() != null) {
                        errorLocation.getParentFile().mkdirs();
                    }
                    try {
                        errorLocation.createNewFile();
                    } catch (IOException e) {
                        System.out
                            .println("WARNING: Failed to create log file in error location '" + errorLocation.getAbsolutePath() + "'!");
                        continue;
                    }
                    try {
                        Files.copy(buildToolsLog.toPath(), errorLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("WARNING: Failed to copy log file to error location '" + errorLocation.getAbsolutePath() + "'!");
                        continue;
                    }
                    System.out.println("WARNING: Copied log file to error location '" + errorLocation.getAbsolutePath() + "'!");
                } finally {
                    if (clean) {
                        cleanWorkDir(workDir);
                    }
                }
            }
            failed.set(fails);
            infoReader.close();
            errorReader.close();
            progressUi.close();
        }, "Build thread");
        thread.start();
        progressUi.await();
        if (thread.isAlive() && currentProcess.isPresent()) {
            thread.interrupt();
            infoReader.close();
            errorReader.close();
            currentProcess.get().destroyForcibly();
            System.exit(0);
            return;
        }
        Dialogs.openInfoDialog("All tasks completed!", new String[] {
            "Amount:  " + versions.length,
            "Success: " + (versions.length - failed.get()),
            "Failed:  " + failed.get()
        });
    }

    private static boolean ensureSafeClean(boolean clean, File selfDir, File workDir) {
        if (clean && selfDir.getAbsolutePath().equals(workDir.getAbsolutePath())) {
            System.out.println("WARNING: Unsafe clean-directory detected, disabling it.");
            System.out.println(
                "WARNING: Please use another directory than the main work directory as BuildTools directory otherwise clean-directory won't work");
            return false; // Unsafe clean, don't allow it
        }
        return clean;
    }

    private static void cleanWorkDir(File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (!file.isDirectory() || file.getName().startsWith("apache-maven") || file.getName().startsWith("PortableGit")) {
                continue;
            }
            deleteFile(file);
        }
    }

    private static File createDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static void deleteFile(File root) {
        if (!root.exists()) {
            return;
        } else if (root.isFile()) {
            root.delete();
            return;
        }
        ArrayList<File> queue = new ArrayList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int index = queue.size() - 1;
            File file = queue.get(index);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    if (!file.delete()) {
                        break;
                    }
                    queue.remove(index);
                    continue;
                }
                Collections.addAll(queue, files);
                continue;
            }
            if (!file.delete()) {
                break;
            }
            queue.remove(index);
        }
    }

    private static File ensureBuildTools(boolean update, File workDir) {
        File file = new File(workDir, "BuildTools.jar");
        if (!update) {
            return file;
        }
        if (!file.exists()) {
            System.out.println("INFO: No BuildTools found, downloading new one!");
            downloadNewJar(file);
            return file;
        }
        String jarVersion = readVersionFromJar(file);
        if (jarVersion.isEmpty()) {
            System.out.println("WARNING: Couldn't identify BuildTools.jar version!");
            System.out.println("WARNING: Downloading new BuildTools.jar");
            downloadNewJar(file);
            return file;
        }
        String jenkinsVersion;
        if (jarVersion.equals(jenkinsVersion = readVersionFromJenkins())) {
            System.out.println("INFO: BuildTools (#" + jarVersion + ") is up2date!");
            return file;
        }
        System.out.println("INFO: BuildTools (#" + jarVersion + ") is outdated, updating to " + jenkinsVersion + "!");
        downloadNewJar(file);
        return file;
    }

    private static void downloadNewJar(File file) {
        File backupFile = new File(file.getParentFile(), file.getName() + ".bak");
        if (file.exists()) {
            try {
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("WARNING: Failed to backup old BuildTools!");
            }
        }
        file.delete();
        try {
            if (!downloadFile(new URL(Constant.SPIGOT_JENKINS_JAR), file)) {
                downloadJarFailed(file, backupFile);
            }
        } catch (IOException e0) {
            downloadJarFailed(file, backupFile);
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }
    }

    private static void downloadJarFailed(File file, File backupFile) {
        System.out.println("WARNING: Failed to download new BuildTools!");
        if (backupFile.exists()) {
            System.out.println("WARNING: Restoring backup BuildTools");
            try {
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e1) {
                System.out.println("WARNING: Failed to restore backup BuildTools!");
            }
        }
    }

    private static boolean downloadFile(URL url, File file) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        httpConnection.connect();
        return Dialogs.openTaskDialog("Downloading BuildTools", 100, (progress) -> {
            if (!file.exists()) {
                file.createNewFile();
            }
            ITask task = progress.begin("Downloading BuildTools.jar", 100);
            try (BufferedInputStream input = new BufferedInputStream(httpConnection.getInputStream())) {
                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file), 1024)) {
                    byte[] data = new byte[1024];
                    int amount = 0;
                    while ((amount = input.read(data, 0, data.length)) >= 0) {
                        output.write(data, 0, amount);
                    }
                }
            }
            task.end();
        }, false);
    }

    private static String readVersionFromJenkins() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(Constant.SPIGOT_JENKINS_BUILD).openStream()))) {
            String string = reader.lines().collect(Collectors.joining(""));
            int start = string.indexOf("\"id\":\"") + 6;
            int end = string.indexOf("\"", start);
            return string.substring(start, end);
        } catch (IOException e) {
            System.out.println("WARNING: Couldn't read version from jenkins");
            System.out.println("WARNING: " + e.getMessage());
            return "";
        }
    }

    private static String readVersionFromJar(File file) {
        try (JarFile jar = new JarFile(file)) {
            ZipEntry entry = jar.getEntry("META-INF/MANIFEST.MF");
            if (entry == null) {
                return "";
            }
            Attributes attributes = new Manifest(jar.getInputStream(entry)).getMainAttributes();
            String version = attributes.getValue(Name.IMPLEMENTATION_VERSION);
            if (version == null || !version.contains("BuildTools")) {
                return "";
            }
            String[] parts = version.split("-");
            return parts[parts.length - 1];
        } catch (IOException e) {
            return "";
        }
    }

    private static String[] buildArgumentBase(SimpleProperties properties) {
        ArrayList<String> list = new ArrayList<>();
        readArguments(list, properties);
        return list.toArray(String[]::new);
    }

    private static void readArguments(ArrayList<String> list, SimpleProperties properties) {
        if (!properties.getOrDefault("compile", true)) {
            list.add("--skip-compile");
        }
        if (properties.getOrDefault("compile-only-changed", false) && !list.contains("--skip-compile")) {
            list.add("--compile-if-changed");
        }
        if (properties.has("output-directory")) {
            list.add("--output-dir");
            list.add(properties.get("output-directory"));
        }
    }

    private static File findLogFile(File workDir) {
        File[] files = workDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            if (file.getName().endsWith("log.txt")) {
                return file;
            }
        }
        return null;
    }

    private static Version[] loadVersions(final SimpleProperties properties, final String[] args) {
        final ArrayList<String> versions = new ArrayList<>();
        if (properties.has("versions")) {
            final String versionsRaw = properties.get("versions");
            if (versionsRaw.contains(";")) {
                Collections.addAll(versions, versionsRaw.split(";"));
            } else {
                versions.add(versionsRaw);
            }
        }
        for (final String arg : args) {
            if (versions.contains(arg)) {
                continue;
            }
            versions.add(arg);
        }
        File file = new File("versions.txt");
        if (file.exists()) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                fileReader.lines().forEach(string -> {
                    if (string.startsWith("#") || string.isBlank() || versions.contains(string)) {
                        return;
                    }
                    versions.add(string);
                });
            } catch (IOException e) {
                System.out.println("WARNING: Couldn't read versions from file");
                System.out.println("WARNING: " + e.getMessage());
            }
        }
        for (int index = 0; index < versions.size(); index++) {
            final String version = versions.get(index).toLowerCase();
            if ("latest".equals(version) || Util.isVersion(version)) {
                versions.set(index, version);
                continue;
            }
            versions.remove(index--);
        }
        versions.sort(Util::sortVersion);
        return versions.stream().map(Version::new).filter(version -> {
            if (version.isValid()) {
                return true;
            }
            final JavaVersion min = JavaVersion.getByClassVersion(version.getMinClass());
            if (version.getMinClass() == version.getMaxClass()) {
                if (min == null) {
                    System.out.println("WARNING: Couldn't build '" + version.getBuild()
                        + "' because no java version found for class version " + version.getMinClass());
                    return false;
                }
                System.out.println("WARNING: Couldn't build '" + version.getBuild() + "' because java version " + min.featureVersion()
                    + " was not specified");
                return false;
            }
            final JavaVersion max = JavaVersion.getByClassVersion(version.getMaxClass());
            if (min == null || max == null) {
                System.out
                    .println("WARNING: Couldn't build '" + version.getBuild() + "' because no java version found for class version between "
                        + version.getMinClass() + " and " + version.getMaxClass());
                return false;
            }
            System.out.println("WARNING: Couldn't build '" + version.getBuild() + "' because no java version specified between "
                + min.featureVersion() + " and " + max.featureVersion());
            return false;
        }).toArray(Version[]::new);
    }

    private static void loadJava() {
        final Properties java = loadProperties("java").properties();
        final Enumeration<Object> keys = java.keys();
        while (keys.hasMoreElements()) {
            final String string = (String) keys.nextElement();
            final JavaVersion version = JavaVersion.getByString(string);
            if (version == null) {
                continue;
            }
            final String path = java.getProperty(string);
            if (!version.path(path)) {
                System.out.println("WARNING: Couldn't load java version " + version.featureVersion() + " from path '" + path + "'!");
                continue;
            }
            System.out.println("INFO: Loaded java version " + version.featureVersion() + " from path '" + path + "'!");
        }
    }

    private static SimpleProperties loadProperties(final String name) {
        final File file = new File(name + ".properties");
        final Properties properties = new Properties();
        if (!file.exists()) {
            return new SimpleProperties(properties);
        }
        try (FileInputStream stream = new FileInputStream(file)) {
            properties.load(stream);
        } catch (final IOException e) {
        }
        return new SimpleProperties(properties);
    }

}
