package me.lauriichan.build.builder.ui.window.ui;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.TimeHelper;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseClick;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseScroll;
import me.lauriichan.build.builder.ui.window.ui.animation.Animators;

public class ListPane extends BasicPane {

    private int scrollY = 0;
    private int currentY = 0;
    private int componentHeight;
    private int componentOffset = 10;

    private double scrollVelocity = 0;
    private double scrollMaxSpeed = 600D;
    private double scrollSpeed = 8;
    private double scrollTime = 1.0;
    private double scrollDecay = 0;
    private double scroll = 0;

    private Color background = Color.BLACK;

    private int barWidth = 4;
    private int barHeight = 30;
    private Color barFill = Color.RED;
    private Color barBackground = Color.DARK_GRAY;

    @Override
    public boolean addChild(Component component) {
        if (!super.addChild(component)) {
            return false;
        }
        currentY += componentOffset;
        component.setY(currentY);
        component.setHeight(componentHeight);
        currentY += componentHeight;
        updateScrollY();
        return true;
    }

    @Override
    public boolean removeChild(Component component) {
        if (!super.removeChild(component)) {
            return false;
        }
        currentY -= (componentOffset + componentHeight);
        updateScrollY();
        return true;
    }

    private void updateScrollY() {
        scrollY = currentY - getHeight() + componentOffset;
        if(scroll > scrollY) {
            scroll = scrollY > 2 * componentOffset ? -scrollY - 2 * componentOffset : 0;
        }
    }

    @Override
    public void render(Area area) {
        if (isScrollEnabled()) {
            drawBar(area, Math.abs(scroll / scrollY));
            super.render(area.create(0, 0, area.getWidth() - barWidth, area.getHeight()));
            return;
        }
        super.render(area);
    }

    private void drawBar(Area area, double percentage) {
        int width = area.getWidth() - barWidth;
        area.drawRectangle(width, 0, barWidth, area.getHeight(), barBackground);
        area.drawRectangle(width, (int) (percentage * (area.getHeight() - barHeight)), barWidth, barHeight, barFill);
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            scrollVelocity = 0;
            scrollDecay = 0;
        }
    }

    @Override
    public void update(long deltaTime) {
        updateScroll(deltaTime);
        updateYPosition();
        super.update(deltaTime);
    }

    private void updateScroll(long deltaTime) {
        if (!isScrollEnabled() || scrollDecay == 0D) {
            return;
        }
        double second = TimeHelper.nanoAsSecond(deltaTime);
        scroll -= scrollVelocity * second;
        if (scroll > 0) {
            scroll = 0;
            scrollVelocity = 0;
            scrollDecay = 0;
            return;
        }
        if (scroll < -(scrollY)) {
            scroll = -scrollY;
            scrollVelocity = 0;
            scrollDecay = 0;
            return;
        }
        scrollDecay -= Math.min(scrollDecay, second);
        scrollVelocity = Animators.DOUBLE.update(scrollVelocity, 0D, 1 - (scrollDecay / scrollTime));
    }

    private void updateYPosition() {
        Component[] components = getChildren();
        int yPos = 0;
        for (Component component : components) {
            yPos += componentOffset;
            component.setY((int) (yPos + scroll));
            yPos += componentHeight;
        }
    }

    /*
     * Input Listener
     */

    @Listener
    public void onDrag(MouseDrag drag) {
        if (drag.isConsumed() || !isScrollEnabled()) {
            return;
        }
        int y = drag.getOldY();
        int by = getGlobalY();
        int height = getHeight();
        if (by > y || by + height < y) {
            return;
        }
        int x = drag.getOldX();
        int bx = getGlobalX() + getWidth();
        if (bx < x || bx - barWidth > x) {
            return;
        }
        drag.consume();
        scroll = -scrollY * Math.max(0, Math.min(1, (drag.getY() - by) / (double) (height - barHeight)));
    }

    @Listener
    public void onClick(MouseClick click) {
        if (click.isConsumed()) {
            return;
        }
        int gx = getGlobalX();
        int gy = getGlobalY();
        int x = click.getX();
        int y = click.getY();
        if (!(gx <= x && gx + getWidth() >= x && gy <= y && gy + getHeight() >= y)) {
            return;
        }
        int bx = gx + getWidth();
        if (isScrollEnabled() && (bx - barWidth <= x && bx >= x)) {
            click.consume();
            scroll = -scrollY * Math.max(0, Math.min(1, (click.getY() - gy) / (double) (getHeight() - barHeight)));
            return;
        }
    }

    @Listener
    public void onScroll(MouseScroll scroll) {
        if (scroll.isConsumed() || !isScrollEnabled() || !isInside(scroll.getX(), scroll.getY())) {
            return;
        }
        scroll.consume();
        scrollDecay = scrollTime;
        scrollVelocity = Math.max(-scrollMaxSpeed, Math.min(scrollMaxSpeed, scrollVelocity + (scroll.getScroll() * scrollSpeed)));
    }

    /*
     * Setter and Getter
     */

    public boolean isScrollEnabled() {
        return scrollY > 0;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getBackground() {
        return background;
    }

    public void setBarBackground(Color barBackground) {
        this.barBackground = barBackground;
    }

    public Color getBarBackground() {
        return barBackground;
    }

    public void setBarFill(Color barFill) {
        this.barFill = barFill;
    }

    public Color getBarFill() {
        return barFill;
    }

    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
    }

    public int getBarHeight() {
        return barHeight;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setComponentHeight(int componentHeight) {
        this.componentHeight = componentHeight;
    }

    public int getComponentHeight() {
        return componentHeight;
    }

    public void setComponentOffset(int componentOffset) {
        this.componentOffset = componentOffset;
    }

    public int getComponentOffset() {
        return componentOffset;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setScrollMaxSpeed(double scrollMaxSpeed) {
        this.scrollMaxSpeed = scrollMaxSpeed;
    }

    public double getScrollMaxSpeed() {
        return scrollMaxSpeed;
    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollTime(double scrollTime) {
        this.scrollTime = scrollTime;
    }

    public double getScrollTime() {
        return scrollTime;
    }

}