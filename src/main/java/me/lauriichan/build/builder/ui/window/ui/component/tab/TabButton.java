package me.lauriichan.build.builder.ui.window.ui.component.tab;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.FontCache;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.util.render.TextRender;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.ui.ITriggerComponent;
import me.lauriichan.build.builder.ui.window.ui.animation.Animators;
import me.lauriichan.build.builder.ui.window.ui.animation.FadeAnimation;

public class TabButton implements ITriggerComponent {

    private final FadeAnimation<Color> hover = new FadeAnimation<>(Animators.COLOR);
    private final FadeAnimation<Color> hoverShadow = new FadeAnimation<>(Animators.COLOR);

    private String text = "";

    private int textWidth;
    private int textHeight;
    private String textLine;
    private TextRender textRender = null;

    private String fontName = "Open Sans";
    private int fontSize = 14;
    private int fontStyle = 0;
    private Color fontColor = Color.WHITE;

    private Color press = Color.GRAY;
    private Color shadow = Color.BLACK;
    private int shadowThickness = 2;

    private boolean pressed = false;
    private boolean hidden = false;
    private boolean update = true;

    private Runnable action;

    @Override
    public void render(Area area) {
        renderBackground(area);
        renderText(area);
    }

    private void renderText(Area area) {
        if (textRender == null) {
            textRender = area.createTextRender(FontCache.get(fontName, fontSize, fontStyle), text);
            textLine = textRender.getLines() == 0 ? "" : textRender.getLine(0);
            textWidth = textRender.getMetrics().widthOf(textLine);
            textHeight = textRender.getHeight();
        }
        area.drawText((area.getWidth() - textWidth) / 2, (area.getHeight() - textHeight / 2) / 2, textRender, fontColor);
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setUpdating(boolean update) {
        this.update = update;
    }

    @Override
    public boolean isUpdating() {
        return update;
    }

    private void renderBackground(Area area) {
        if (pressed) {
            area.fillShadow(press, shadowThickness, shadow);
            return;
        }
        area.fillShadow(hover.getValue(), shadowThickness, hoverShadow.getValue());
    }

    @Override
    public void update(long deltaTime) {
        hover.tick(deltaTime);
        hoverShadow.tick(deltaTime);
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
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public Color getShadow() {
        return shadow;
    }

    public void setShadow(Color shadow) {
        this.shadow = shadow;
    }

    public Color getPress() {
        return press;
    }

    public void setPress(Color press) {
        this.press = press;
    }

    public int getShadowThickness() {
        return shadowThickness;
    }

    public void setShadowThickness(int shadowThickness) {
        this.shadowThickness = shadowThickness;
    }

    public void setHover(Color color) {
        setHover(color, color);
    }

    public void setHover(Color start, Color end) {
        hover.setStart(start);
        hover.setEnd(end);
    }

    public void setHoverFade(double fadeIn, double fadeOut) {
        hover.setFade(fadeIn, fadeOut);
    }

    public void setHoverShadow(Color color) {
        setHoverShadow(color, color);
    }

    public void setHoverShadow(Color start, Color end) {
        hoverShadow.setStart(start);
        hoverShadow.setEnd(end);
    }

    public void setHoverShadowFade(double fadeIn, double fadeOut) {
        hoverShadow.setFade(fadeIn, fadeOut);
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public boolean hasAction() {
        return action != null;
    }

    /*
     * 
     */

    @Override
    public void setTriggered(boolean triggered) {
        hover.setTriggered(triggered);
        hoverShadow.setTriggered(triggered);
    }

    @Override
    public boolean isTriggered() {
        return hover.isTriggered();
    }

    public void press(MouseButton button) {
        if (button != MouseButton.LEFT) {
            return;
        }
        pressed = true;
    }

    public void release(MouseButton button) {
        if (!pressed || button != MouseButton.LEFT) {
            return;
        }
        pressed = false;
        if (action == null) {
            return;
        }
        action.run();
    }

}