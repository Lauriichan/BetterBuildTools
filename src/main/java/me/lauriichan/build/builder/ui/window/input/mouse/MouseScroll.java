package me.lauriichan.build.builder.ui.window.input.mouse;

import me.lauriichan.build.builder.ui.util.Point;
import me.lauriichan.build.builder.ui.window.input.Input;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public final class MouseScroll extends Input {

    protected final Point position;
    protected final Point screenPosition;

    protected final int scroll;
    protected final double rotation;

    public MouseScroll(final InputProvider provider, final int x, final int y, final int screenX, final int screenY, final int scroll,
        final double rotation) {
        super(provider);
        this.position = new Point(x, y);
        this.screenPosition = new Point(screenX, screenY);
        this.scroll = scroll;
        this.rotation = rotation;
    }

    public int getX() {
        return position.getX();
    }

    public int getScreenX() {
        return screenPosition.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getScreenY() {
        return screenPosition.getY();
    }

    public int getScroll() {
        return scroll;
    }

    public double getRotation() {
        return rotation;
    }

}
