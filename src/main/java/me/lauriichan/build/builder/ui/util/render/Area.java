package me.lauriichan.build.builder.ui.util.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import me.lauriichan.build.builder.ui.util.FontCache;
import me.lauriichan.build.builder.ui.util.Point;

public final class Area {

    private final Graphics2D graphics;

    private final Point size;
    private Color color;

    private Font font = FontCache.get("Open Sans");
    private TextMetrics metrics = null;
    private Color fontColor = Color.WHITE;

    public Area(final Graphics2D graphics, final Color color, final int x, final int y, final int width, final int height) {
        this.graphics = x == -1 && y == -1 ? graphics : (Graphics2D) graphics.create(Math.max(x, 0), Math.max(y, 0), width, height);
        this.graphics.setBackground(color);
        this.graphics.setColor(color);
        this.color = color;
        this.size = new Point(width, height);
    }

    public void setColor(final Color color) {
        graphics.setBackground(color);
        graphics.setColor(color);
        this.color = color;
    }

    public TextMetrics getMetrics() {
        if (metrics != null) {
            return metrics;
        }
        return metrics = getMetrics(font);
    }

    public TextMetrics getMetrics(Font font) {
        return new TextMetrics(graphics.getFontMetrics(font));
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return size.getX();
    }

    public int getHeight() {
        return size.getY();
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public void setFontName(final String fontName) {
        if (font.getName().equals(fontName)) {
            return;
        }
        font = FontCache.get(fontName, font.getSize(), font.getStyle());
        metrics = null;
        graphics.setFont(font);
    }

    public String getFontName() {
        return font.getName();
    }

    public void setFontSize(final int fontSize) {
        if (font.getSize() == fontSize) {
            return;
        }
        font = FontCache.get(font.getName(), fontSize, font.getStyle());
        metrics = null;
        graphics.setFont(font);
    }

    public int getFontSize() {
        return font.getSize();
    }

    public void setFontStyle(final int fontStyle) {
        if (font.getStyle() == fontStyle) {
            return;
        }
        font = FontCache.get(font.getName(), font.getSize(), fontStyle);
        metrics = null;
        graphics.setFont(font);
    }

    public int getFontStyle() {
        return font.getStyle();
    }

    public void setFontColor(final Color color) {
        this.fontColor = color;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public Area create(final int x, final int y, final int width, final int height) {
        return new Area(graphics, color, x, y, width, height);
    }

    /*
     * Rendering 
     */

    public void clear() {
        graphics.clearRect(0, 0, getWidth(), getHeight());
    }

    public void fill(final Color color) {
        drawRectangle(0, 0, getWidth(), getHeight(), color);
    }

    public void fillOutline(final Color color, final int thickness, final Color outline) {
        drawRectangle(0, 0, getWidth(), getHeight(), outline);
        drawRectangle(thickness, thickness, getWidth() - thickness * 2, getHeight() - thickness * 2, color);
    }

    public void fillShadow(final Color color, final int thickness, final Color shadow) {
        final int half = thickness / 2;
        drawRectangle(0, 0, getWidth(), getHeight(), shadow);
        drawRectangle(half, half, getWidth() - thickness - half, getHeight() - thickness - half, color);
    }

    public void drawLine(final int x1, final int y1, final int x2, final int y2, final float thickness) {
        final Stroke prev = graphics.getStroke();
        graphics.setStroke(new BasicStroke(thickness));
        graphics.drawLine(x1, y1, x2, y2);
        graphics.setStroke(prev);
    }

    public void drawLine(final int x1, final int y1, final int x2, final int y2, final float thickness, final Color color) {
        graphics.setPaint(color);
        drawLine(x1, y1, x2, y2, thickness);
        graphics.setPaint(this.color);
    }

    public void drawRectangle(final int x, final int y, final int width, final int height) {
        graphics.fillRect(x, y, width, height);
    }

    public void drawRectangle(final int x, final int y, final int width, final int height, final Color color) {
        graphics.setPaint(color);
        graphics.fillRect(x, y, width, height);
        graphics.setPaint(this.color);
    }

    public void drawRectangleOutline(final int x, final int y, final int width, final int height, int thickness) {
        final Stroke prev = graphics.getStroke();
        graphics.setStroke(new BasicStroke(thickness));
        graphics.drawRect(x, y, width, height);
        graphics.setStroke(prev);
    }

    public void drawRectangleOutline(final int x, final int y, final int width, final int height, int thickness, final Color color) {
        final Stroke prev = graphics.getStroke();
        graphics.setStroke(new BasicStroke(thickness));
        graphics.setPaint(color);
        graphics.drawRect(x, y, width, height);
        graphics.setPaint(this.color);
        graphics.setStroke(prev);
    }

    /*
     * Text rendering
     */

    public void drawText(int x, int y, String text) {
        drawText(x, y, text, font, fontColor);
    }

    public void drawText(int x, int y, String text, Color color) {
        drawText(x, y, text, font, color);
    }

    public void drawText(int x, int y, String text, Font font) {
        drawText(x, y, text, font, fontColor);
    }

    public void drawText(int x, int y, String text, Font font, Color color) {
        String[] lines = text.split("\n");
        TextMetrics metrics = getMetrics(font);
        int height = metrics.getHeight();
        int base = metrics.getDrawHeight() + y;
        graphics.setPaint(color);
        graphics.setFont(font);
        for (int index = 0; index < lines.length; index++) {
            graphics.drawString(lines[index], x, base + (height * (index)));
        }
        graphics.setFont(this.font);
        graphics.setPaint(this.color);
    }

    public void drawText(int x, int y, TextRender render) {
        drawText(x, y, render, fontColor);
    }

    public void drawText(int x, int y, TextRender render, Color color) {
        int amount = render.getLines();
        int height = render.getHeight();
        int base = render.getMetrics().getDrawHeight() + y;
        graphics.setPaint(color);
        graphics.setFont(render.getMetrics().getFont());
        for (int index = 0; index < amount; index++) {
            graphics.drawString(render.getLine(index), x, base + (height * index));
        }
        graphics.setFont(font);
        graphics.setPaint(this.color);
    }

    /*
     * Text creation
     */

    public TextRender createTextRender(String text) {
        return createTextRender(getMetrics(), text, getWidth());
    }

    public TextRender createTextRender(String text, int maxWidth) {
        return createTextRender(getMetrics(), text, maxWidth);
    }

    public TextRender createTextRender(Font font, String text) {
        return createTextRender(getMetrics(font), text, getWidth());
    }

    public TextRender createTextRender(Font font, String text, int maxWidth) {
        return createTextRender(getMetrics(font), text, maxWidth);
    }

    private TextRender createTextRender(TextMetrics metrics, String text, int maxWidth) {
        return metrics.calculate(text, maxWidth);
    }

}
