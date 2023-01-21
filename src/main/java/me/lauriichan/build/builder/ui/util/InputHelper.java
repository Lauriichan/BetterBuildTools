package me.lauriichan.build.builder.ui.util;

import me.lauriichan.build.builder.ui.window.input.mouse.MouseHover;
import me.lauriichan.build.builder.ui.window.ui.Component;
import me.lauriichan.build.builder.ui.window.ui.animation.FadeAnimation;

public final class InputHelper {

    private InputHelper() {}

    public static void hover(final MouseHover hover, final Component component, final FadeAnimation<?> animation) {
        if (!component.isInside(hover.getX(), hover.getY()) || hover.isConsumed()) {
            animation.setTriggered(false);
            return;
        }
        hover.consume();
        animation.setTriggered(true);
    }

    public static void hover(final MouseHover hover, final int x, final int y, final int width, final int height,
        final FadeAnimation<?> animation) {
        if (((x > hover.getX()) || (x + width < hover.getX()) || (y > hover.getY()) || (y + height < hover.getY())) || hover.isConsumed()) {
            animation.setTriggered(false);
            return;
        }
        hover.consume();
        animation.setTriggered(true);
    }

}
