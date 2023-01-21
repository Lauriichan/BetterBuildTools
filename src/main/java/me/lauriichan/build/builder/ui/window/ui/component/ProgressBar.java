package me.lauriichan.build.builder.ui.window.ui.component;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.TimeHelper;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.ui.Component;
import me.lauriichan.build.builder.ui.window.ui.animation.Animators;

public class ProgressBar extends Component {

    private static final Color DEFAULT_INNER = Color.GRAY;
    private static final Color DEFAULT_OUTER = Color.DARK_GRAY;
    private static final Color DEFAULT_FILL = Color.RED;

    private int border = 2;

    private boolean smoothProgress = true;
    private double smoothSpeed = 1d;

    private Color innerColor = DEFAULT_INNER;
    private Color outerColor = DEFAULT_OUTER;
    private Color fillColor = DEFAULT_FILL;

    private double progress = 0d;

    private double targetProgress;

    @Override
    public void render(Area area) {
        int width = getWidth() - border * 2;
        int height = getHeight() - border * 2;
        int fillWidth = (int) Math.round(progress * width);
        area.drawRectangle(border, border, fillWidth, height, fillColor);
        area.drawRectangle(border + fillWidth, border, width - fillWidth, height, innerColor);
        area.drawRectangleOutline(border, border, width, height, border, outerColor);
    }

    @Override
    public void update(long deltaTime) {
        if (!isSmoothProgress()) {
            return;
        }
        double speed = smoothSpeed;
        if(targetProgress < progress) {
            speed *= 6;
        }
        progress = Animators.DOUBLE.update(progress, targetProgress, TimeHelper.nanoAsSecond(deltaTime) * speed);
    }

    public double getProgress() {
        if (smoothProgress) {
            return targetProgress;
        }
        return progress;
    }

    public void setProgress(double progress) {
        this.targetProgress = Math.max(Math.min(progress, 1d), 0d);
        if (smoothProgress) {
            return;
        }
        this.progress = targetProgress;
    }

    public boolean isSmoothProgress() {
        return smoothProgress;
    }

    public void setSmoothProgress(boolean smoothProgress) {
        this.smoothProgress = smoothProgress;
    }
    
    public double getSmoothSpeed() {
        return smoothSpeed;
    }

    public void setSmoothSpeed(double smoothSpeed) {
        this.smoothSpeed = Math.max(smoothSpeed, 0.00001d);
    }
    
    public int getBorder() {
        return border;
    }
    
    public void setBorder(int border) {
        this.border = Math.max(border, 1);
    }

    public Color getInnerColor() {
        return innerColor;
    }

    public void setInnerColor(Color innerColor) {
        this.innerColor = innerColor == null ? DEFAULT_INNER : innerColor;
    }

    public Color getOuterColor() {
        return outerColor;
    }

    public void setOuterColor(Color outerColor) {
        this.outerColor = outerColor == null ? DEFAULT_OUTER : outerColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor == null ? DEFAULT_FILL : fillColor;
    }

}
