package me.lauriichan.build.builder.ui.window.input.mouse;

import me.lauriichan.build.builder.ui.util.Point;
import me.lauriichan.build.builder.ui.window.input.Input;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public class MouseHover extends Input {

    protected final Point previous;
    protected final Point position;
    protected final Point screenPosition;

    public MouseHover(final InputProvider provider, final int oldX, final int oldY, final int x, final int y, final int screenX,
        final int screenY) {
        super(provider);
        this.previous = new Point(oldX, oldX);
        this.position = new Point(x, y);
        this.screenPosition = new Point(screenX, screenY);
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getOldX() {
        return previous.getX();
    }

    public int getOldY() {
        return previous.getY();
    }

    public int getScreenX() {
        return screenPosition.getX();
    }

    public int getScreenY() {
        return screenPosition.getY();
    }

}
