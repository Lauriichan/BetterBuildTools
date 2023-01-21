package me.lauriichan.build.builder.ui.window.ui.component;

import java.awt.Color;
import java.awt.Font;

import me.lauriichan.build.builder.ui.util.FontCache;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.util.render.TextMetrics;
import me.lauriichan.build.builder.ui.util.render.TextRender;
import me.lauriichan.build.builder.ui.window.ui.Component;

public final class Label extends Component {

    private String text = "";

    private int textWidth;
    private int textHeight;
    private String textLine;
    private TextRender textRender;

    private String fontName = "Open Sans";
    private int fontSize = 12;
    private int fontStyle = 0;
    private Color fontColor = Color.WHITE;
    
    private int textOffset = 0;

    private boolean centerText = false;
    private boolean allowMultiline = false;

    @Override
    public void render(Area area) {
        if (allowMultiline) {
            renderMultiText(area);
            return;
        }
        renderSingleText(area);
    }

    private void renderMultiText(Area area) {
        if (textRender == null) {
            textRender = area.createTextRender(FontCache.get(fontName, fontSize, fontStyle), text);
            textHeight = textRender.getHeight();
        }
        int amount = textRender.getLines();
        if (amount == 0) {
            return;
        }
        TextMetrics metrics = textRender.getMetrics();
        Font font = FontCache.get(fontName, fontSize, fontStyle);
        for (int index = 0; index < textRender.getLines(); index++) {
            String line = textRender.getLine(index);
            int y = (textHeight + textOffset) * index;
            if (!centerText) {
                area.drawText(0, y, line, font, fontColor);
                continue;
            }
            area.drawText((area.getWidth() - metrics.widthOf(line)) / 2, y, line, font, fontColor);
        }
    }

    private void renderSingleText(Area area) {
        if (textRender == null) {
            textRender = area.createTextRender(FontCache.get(fontName, fontSize, fontStyle), text);
            textLine = textRender.getLines() == 0 ? "" : textRender.getLine(0);
            textWidth = textRender.getMetrics().widthOf(textLine);
            textHeight = textRender.getHeight();
        }
        Font font = FontCache.get(fontName, fontSize, fontStyle);
        if (!centerText) {
            area.drawText(0, 0, textLine, font, fontColor);
            return;
        }
        area.drawText((area.getWidth() - textWidth) / 2, (area.getHeight() - textHeight / 2) / 2, textLine, font, fontColor);
    }

    public void setTextOffset(int textOffset) {
        this.textOffset = textOffset;
    }

    public int getTextOffset() {
        return textOffset;
    }

    public boolean isMultilineAllowed() {
        return allowMultiline;
    }

    public void setMultilineAllowed(boolean allowMultiline) {
        this.allowMultiline = allowMultiline;
        textRender = null;
    }

    public void setTextCentered(boolean centered) {
        this.centerText = centered;
    }

    public boolean isTextCentered() {
        return centerText;
    }

    public String getShownText() {
        return textLine;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = (text == null ? "" : text);
        textRender = null;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        textRender = null;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
        textRender = null;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        textRender = null;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        textRender = null;
    }

}