package me.lauriichan.build.builder.ui.window.ui.component.geometry;

public abstract class Seperator extends Geometry {

    protected final boolean vertical;

    public Seperator() {
        this(true);
    }

    public Seperator(boolean vertical) {
        this.vertical = vertical;
    }

    public final boolean isVertical() {
        return vertical;
    }

}