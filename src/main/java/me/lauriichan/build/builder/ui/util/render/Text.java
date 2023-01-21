package me.lauriichan.build.builder.ui.util.render;

import java.awt.Color;
import java.awt.Font;

import me.lauriichan.build.builder.ui.util.FontCache;

public final class Text {

    private String string = "";
    private Color color = Color.WHITE;
    private Font font = FontCache.get("Open Sans");

    private int renderedWidth = 0;
    private TextRender render;
    
    public void calculate(Area area, int width) {
        if (render == null) {
            render = area.getMetrics(font).calculate(string, width);
        }
    }

    public void render(Area area) {
        render(area, 0, 0, area.getWidth());
    }

    public void render(Area area, int x, int y) {
        render(area, x, y, area.getWidth());
    }

    public void render(Area area, int x, int y, int width) {
        if (renderedWidth != width) {
            renderedWidth = width;
            render = null;
        }
        calculate(area, width);
        area.drawText(x, y, render, color);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color == null ? Color.WHITE : color;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        if(this.string.equals(string) || string == null) {
            return;
        }
        this.string = string;
        render = null;
    }

    public void setFontName(String name) {
        if (font.getName().equals(name)) {
            return;
        }
        this.font = FontCache.get(name, font.getSize(), font.getStyle());
        render = null;
    }

    public String getFontName() {
        return font.getName();
    }

    public void setFontSize(int size) {
        if (font.getSize() == size) {
            return;
        }
        this.font = FontCache.get(font.getName(), size, font.getStyle());
        render = null;
    }

    public int getFontSize() {
        return font.getSize();
    }

    public void setFontStyle(int style) {
        if (font.getStyle() == style) {
            return;
        }
        this.font = FontCache.get(font.getName(), font.getSize(), style);
        render = null;
    }

    public int getFontStyle() {
        return font.getStyle();
    }

    public Font getFont() {
        return font;
    }

    public TextRender getRender() {
        return render;
    }

    public boolean hasRender() {
        return render != null;
    }

}
