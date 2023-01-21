package me.lauriichan.build.builder.ui.util.source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Objects;

import me.lauriichan.build.builder.ui.util.Ref;

public final class PathSource extends DataSource {

    private static final Ref<Path> ROOT = Ref.of();

    private final Path path;

    public PathSource(final Path path) {
        this.path = path;
    }

    @Override
    public Path getSource() {
        return path;
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    public static PathSource ofResource(final String rawPath) {
        try {
            return new PathSource(Objects.requireNonNull(getClasspath().resolveSibling(rawPath)));
        } catch (final Exception exp) {
            System.err.println("Failed to load Resource '" + rawPath + "'!");
            System.err.println(exp.getMessage());
        }
        return null;
    }

    public static Path getClasspath() {
        if (ROOT.isPresent()) {
            return ROOT.get();
        }
        try {
            final URI uri = PathSource.class.getResource("/").toURI();
            final Path path = "jar".equals(uri.getScheme()) ? FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath("/")
                : Paths.get(uri).resolve("classes");
            ROOT.set(path).lock();
            return ROOT.get();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("Failed to retrieve classpath", e);
        }
    }

}