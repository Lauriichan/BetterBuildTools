package me.lauriichan.build.builder.ui.window.ui.component.geometry;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.render.Area;

public final class Rectangle extends Geometry {

    private Color color = Color.BLACK;

    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    @Override
    public void render(final Area area) {
        area.fill(color);
    }

}
