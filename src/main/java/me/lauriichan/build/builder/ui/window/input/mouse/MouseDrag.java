package me.lauriichan.build.builder.ui.window.input.mouse;

import me.lauriichan.build.builder.ui.util.Point;
import me.lauriichan.build.builder.ui.window.input.Input;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public class MouseDrag extends Input {

    protected final Point previous;
    protected final Point position;
    protected final Point screenPosition;
    protected final MouseButton button;

    public MouseDrag(final InputProvider provider, final int oldX, final int oldY, final int x, final int y, final int screenX,
        final int screenY, final int button) {
        super(provider);
        this.previous = new Point(oldX, oldY);
        this.button = MouseButton.values()[button - 1];
        this.position = new Point(x, y);
        this.screenPosition = new Point(screenX, screenY);
    }

    public MouseButton getButton() {
        return button;
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

    public int getOldX() {
        return previous.getX();
    }

    public int getOldY() {
        return previous.getY();
    }

}
