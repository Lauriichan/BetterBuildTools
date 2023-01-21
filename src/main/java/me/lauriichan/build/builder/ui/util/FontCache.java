package me.lauriichan.build.builder.ui.util;

import java.awt.Font;
import java.util.HashMap;

public final class FontCache {

    public static final FontCache INSTANCE = new FontCache();

    public static Font get(final String name) {
        return get(name, 12);
    }

    public static Font get(final String name, final int size) {
        return get(name, size, 0);
    }

    public static Font get(final String name, final int size, final int style) {
        return INSTANCE.getOrBuild(name, size, style);
    }

    private final HashMap<String, Font> fonts = new HashMap<>();

    private FontCache() {}

    public Font getOrBuild(final String name, final int size, final int style) {
        final String combine = name + '#' + size + '$' + style;
        if (!fonts.containsKey(combine)) {
            final Font font = new Font(name, style, size);
            fonts.put(combine, font);
            return font;
        }
        return fonts.get(combine);
    }

    public void clear() {
        fonts.clear();
    }

}
