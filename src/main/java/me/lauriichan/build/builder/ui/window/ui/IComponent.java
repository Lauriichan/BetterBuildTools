package me.lauriichan.build.builder.ui.window.ui;

import me.lauriichan.build.builder.ui.window.event.EventProvider;
import me.lauriichan.build.builder.ui.window.event.IEvent;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public interface IComponent extends IBaseComponent {

    InputProvider getInput();
    
    EventProvider getEvent();

    default void callEvent(IEvent event) {
        EventProvider provider = getEvent();
        if(provider == null) {
            return;
        }
        provider.call(event);
    }

    default boolean isRoot() {
        return false;
    }

    default boolean isContainer() {
        return false;
    }

    IComponent getContainer();

    default boolean hasContainer() {
        return getContainer() != null;
    }

    IComponent getParent();

    default boolean hasParent() {
        return getParent() != null;
    }

    default IComponent getRoot() {
        if (isRoot()) {
            return this;
        }
        return hasParent() ? getParent().getRoot() : null;
    }

    default boolean hasRoot() {
        return getRoot() != null;
    }

    void setX(int x);

    int getX();

    void setY(int y);

    int getY();

    default void setPosition(final int x, final int y) {
        setX(x);
        setY(y);
    }

    void setWidth(int width);

    int getWidth();

    void setHeight(int height);

    int getHeight();

    default void setSize(final int width, final int height) {
        setWidth(width);
        setHeight(height);
    }

    void setMinWidth(int minWidth);

    int getMinWidth();

    void setMinHeight(int minHeight);

    int getMinHeight();

    default void setMinSize(final int width, final int height) {
        setMinWidth(width);
        setMinHeight(height);
    }

    default int getGlobalX() {
        return hasParent() ? getParent().getGlobalX() + getX() : getX();
    }

    default int getGlobalY() {
        return hasParent() ? getParent().getGlobalY() + getY() : getY();
    }

    default boolean isInside(final int x, final int y) {
        final int gx = getGlobalX();
        final int gy = getGlobalY();
        return gx <= x && gx + getWidth() >= x && gy <= y && gy + getHeight() >= y;
    }

}
