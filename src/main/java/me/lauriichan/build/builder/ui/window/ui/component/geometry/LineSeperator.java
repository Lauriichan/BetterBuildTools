package me.lauriichan.build.builder.ui.window.ui.component.geometry;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.render.Area;

public final class LineSeperator extends Seperator {

    private Color color;
    private int thickness = 2;
    private int offset = 3;

    public LineSeperator() {
        super();
    }

    public LineSeperator(boolean vertical) {
        super(vertical);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = (color == null) ? Color.BLACK : color;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void render(Area area) {
        if (isVertical()) {
            area.drawLine(area.getWidth() / 2, offset, area.getWidth() / 2, area.getHeight() - offset, thickness, color);
            return;
        }
        area.drawLine(offset, area.getHeight() / 2, area.getWidth() - offset, area.getHeight() / 2, thickness, color);
    }

}