package me.lauriichan.build.builder.ui.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.lauriichan.build.builder.ui.util.source.DataSource;
import me.lauriichan.build.builder.ui.util.source.PathSource;

public final class ImageCache {

    public static final ImageCache INSTANCE = new ImageCache();

    public static BufferedImage get(final String name) {
        return INSTANCE.getOrLoad(name, null);
    }

    public static BufferedImage resource(final String name, final String path) {
        return INSTANCE.getOrLoad(name, PathSource.ofResource(path));
    }

    private final HashMap<String, BufferedImage> images = new HashMap<>();

    private ImageCache() {}

    public BufferedImage getOrLoad(final String name, final DataSource source) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        if (source == null) {
            return null;
        }
        try {
            final BufferedImage image = ImageIO.read(source.openStream());
            images.put(name, image);
            return image;
        } catch (final IOException e) {
            return null;
        }
    }

    public void clear() {
        images.clear();
    }

}
