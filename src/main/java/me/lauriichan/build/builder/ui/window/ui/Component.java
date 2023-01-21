package me.lauriichan.build.builder.ui.window.ui;

import me.lauriichan.build.builder.ui.util.Point;
import me.lauriichan.build.builder.ui.window.event.EventProvider;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public abstract class Component implements IComponent {

    protected final Point position = new Point(0, 0);
    protected final Point minSize = new Point(0, 0);
    protected final Point size = new Point(0, 0);

    protected boolean hidden = false;
    protected boolean update = true;

    private Component parent;
    private InputProvider input;
    private EventProvider event;

    public final void setParent(final Component component) {
        if (isRoot()) {
            throw new IllegalStateException("Can't set InputProvider to root component!");
        }
        if (component == null) {
            input.unregister(this);
            event.unregister(this);
            onRemove();
            input = null;
            event = null;
            parent = null;
            return;
        }
        if (component.getInput() == null) {
            throw new IllegalArgumentException("Component isn't initialised (no InputProvider)");
        }
        parent = component;
        input = component.getInput();
        input.register(this);
        event = component.getEvent();
        event.register(this);
        onAdd();
    }

    protected void onAdd() {}

    protected void onRemove() {}

    @Override
    public InputProvider getInput() {
        return input;
    }

    @Override
    public EventProvider getEvent() {
        return event;
    }

    @Override
    public Component getContainer() {
        if (isContainer()) {
            return this;
        }
        return hasParent() ? parent.getContainer() : null;
    }

    @Override
    public Component getParent() {
        return parent;
    }

    @Override
    public Component getRoot() {
        if (isRoot()) {
            return this;
        }
        return hasParent() ? parent.getRoot() : null;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isUpdating() {
        return update;
    }

    @Override
    public void setUpdating(final boolean update) {
        this.update = update;
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public void setX(final int x) {
        position.setX(x);
    }

    @Override
    public int getY() {
        return position.getY();
    }

    @Override
    public void setY(final int y) {
        position.setY(y);
    }

    @Override
    public int getWidth() {
        return size.getX();
    }

    @Override
    public void setWidth(int width) {
        final int oldWidth = size.getX();
        size.setX(Math.max(width, minSize.getX()));
        if (oldWidth != size.getX()) {
            onWidthChange(oldWidth, size.getX());
        }
    }

    @Override
    public int getHeight() {
        return size.getY();
    }

    @Override
    public void setHeight(int height) {
        final int oldHeight = size.getY();
        size.setY(Math.max(height, minSize.getY()));
        if (oldHeight != size.getY()) {
            onHeightChange(oldHeight, size.getY());
        }
    }

    @Override
    public int getMinWidth() {
        return minSize.getX();
    }

    @Override
    public void setMinWidth(final int width) {
        minSize.setX(width);
        if (size.getX() < width) {
            setWidth(width);
        }
    }

    @Override
    public int getMinHeight() {
        return minSize.getY();
    }

    @Override
    public void setMinHeight(final int height) {
        minSize.setY(height);
        if (size.getY() < height) {
            setHeight(height);
        }
    }

    protected void onHeightChange(final int oldHeight, final int newHeight) {}

    protected void onWidthChange(final int oldWidth, final int newWidth) {}

}
