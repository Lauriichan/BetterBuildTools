package me.lauriichan.build.builder.ui.util;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.util.render.Text;
import me.lauriichan.build.builder.ui.util.render.TextRender;

public final class LogEntry {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final String text;

    private final Text renderer = new Text();

    public LogEntry(final Color color, final String text, final boolean time) {
        this.text = text;
        renderer.setColor(color);
        renderer.setString((time ? "[" + FORMAT.format(Calendar.getInstance().getTime()) + "] " : "") + text);
    }

    public Color getColor() {
        return renderer.getColor();
    }

    public String getText() {
        return text;
    }
    
    public int getHeight() {
        return renderer.getRender().getLines() * renderer.getRender().getHeight();
    }

    public void setFont(String fontName, int fontSize, int fontStyle) {
        renderer.setFontName(fontName);
        renderer.setFontSize(fontSize);
        renderer.setFontStyle(fontStyle);
    }
    
    public LogEntry calculate(Area area) {
        renderer.calculate(area, area.getWidth());
        return this;
    }

    public LogEntry render(Area area, int x, int y) {
        renderer.render(area, x, y);
        return this;
    }
    
    public TextRender getRender() {
        return renderer.getRender();
    }

    @Override
    public String toString() {
        return renderer.getString();
    }

}
