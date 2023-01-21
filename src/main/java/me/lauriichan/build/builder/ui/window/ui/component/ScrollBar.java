package me.lauriichan.build.builder.ui.window.ui.component;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.TimeHelper;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseClick;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseScroll;
import me.lauriichan.build.builder.ui.window.ui.IBaseComponent;
import me.lauriichan.build.builder.ui.window.ui.IComponent;
import me.lauriichan.build.builder.ui.window.ui.animation.Animators;

public class ScrollBar implements IBaseComponent {

    private final IComponent parent;

    private int marginRight = 0, marginTop = 0, marginBottom = 0;

    private int width = 6;
    private int minHeight = 30;
    private int maxHeight = 0;

    private Color background = Color.DARK_GRAY;
    private Color foreground = Color.RED;

    private double scrollMaxSpeed = 600D;
    private double scrollSpeed = 8;
    private double scrollTime = 1.0;

    private double scrollVelocity;
    private double scrollDecay;
    private double scroll;
    private double maxScroll;

    private int barX;
    private int barHeight;
    private int barFillHeight;

    private boolean updating = true;

    /*
     * Setup
     */

    public ScrollBar(final IComponent parent) {
        this.parent = parent;
        setMaxHeight(parent.getHeight());
    }

    public void setup(boolean register) {
        if (register) {
            parent.getInput().register(this);
            return;
        }
        parent.getInput().unregister(this);
    }

    /*
     * Dimensions updates
     * 
     * Setter & Getter
     */

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = Math.max(minHeight, 4);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        maxHeight = Math.max(maxHeight, parent.getHeight() - marginTop - marginBottom);
        if (this.maxHeight == maxHeight) {
            return;
        }
        this.maxHeight = maxHeight;
        updateDimensions();
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = Math.max(marginBottom, 0);
        updateDimensions();
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = Math.max(marginRight, 0);
        updateDimensions();
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = Math.max(marginTop, 0);
        updateDimensions();
    }

    public void updateDimensions() {
        if (maxHeight == 0) {
            return;
        }
        barHeight = parent.getHeight() - marginTop - marginBottom;
        barX = parent.getWidth() - width - marginRight;
        maxScroll = maxHeight - barHeight;
        barFillHeight = Math.max((int) (barHeight / (double) maxHeight), minHeight);
        if (scroll > maxScroll) {
            scrollDecay = 0;
            scrollVelocity = 0;
            scroll = maxScroll;
        }
    }

    /*
     * Special Getter
     */

    public int getOffset() {
        if (maxScroll == 0d) {
            return 0;
        }
        return (int) Math.round(Math.min(scroll, maxScroll));
    }

    public double getProgress() {
        if (maxScroll == 0d || scroll <= 0d) {
            return 0d;
        }
        if (scroll >= maxScroll) {
            return 1d;
        }
        return scroll / maxScroll;
    }

    public boolean canScroll() {
        return maxScroll > 0d && isUpdating();
    }

    /*
     * Rendering & Updates
     */

    @Override
    public void render(Area area) {
        if (isHidden()) {
            return;
        }
        area.drawRectangle(barX, marginTop, width, barHeight, background);
        area.drawRectangle(barX, marginTop + (int) Math.round((1d - getProgress()) * (barHeight - barFillHeight)), width, barFillHeight,
            foreground);
    }

    @Override
    public void update(long deltaTime) {
        if (isHidden() || scrollDecay <= 0) {
            return;
        }
        double second = TimeHelper.nanoAsSecond(deltaTime);
        double step = scrollVelocity * second;
        double diff = scroll + step;
        if (diff < 0 || diff > maxScroll) {
            step = diff < 0 ? -scroll : maxScroll - scroll;
            scrollVelocity = 0;
            scrollDecay = 0;
        }
        scroll += step;
        if (scrollDecay <= 0) {
            return;
        }
        scrollDecay -= Math.min(scrollDecay, second);
        scrollVelocity = Animators.DOUBLE.update(scrollVelocity, 0d, 1d - (scrollDecay / scrollTime));
    }

    /*
     * Inputs
     */

    @Listener
    public void onDrag(MouseDrag drag) {
        if (drag.isConsumed() || !canScroll()) {
            return;
        }
        processScrollUpdate(drag.getOldX(), drag.getOldY(), drag.getY());
    }

    @Listener
    public void onClick(MouseClick click) {
        if (click.isConsumed() || !canScroll()) {
            return;
        }
        processScrollUpdate(click.getX(), click.getY(), click.getY());
    }

    @Listener
    public void onScroll(MouseScroll scroll) {
        if (scroll.isConsumed() || !canScroll() || !parent.isInside(scroll.getX(), scroll.getY())) {
            return;
        }
        scrollDecay = scrollTime;
        scrollVelocity = Math.max(Math.min(scrollVelocity - (scroll.getScroll() * scrollSpeed), scrollMaxSpeed), -scrollMaxSpeed);
    }

    private boolean processScrollUpdate(int mouseX, int mouseY, int updateY) {
        int y = parent.getGlobalY() + marginTop;
        if (y > mouseY || y + barHeight < mouseY) {
            return false;
        }
        int x = parent.getGlobalX() + barX;
        if (x > mouseX || x + width < mouseX) {
            return false;
        }
        scroll = maxScroll * (1d - Math.min(Math.max((updateY - y - (barFillHeight / 2d)) / (barHeight - barFillHeight), 0d), 1d));
        return true;
    }

    /*
     * Other Setter & Getter
     */

    @Override
    public boolean isUpdating() {
        return updating;
    }

    @Override
    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    @Override
    public boolean isHidden() {
        return !updating;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.updating = !hidden;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = Math.abs(scrollSpeed);
    }

    public double getScrollMaxSpeed() {
        return scrollMaxSpeed;
    }

    public void setScrollMaxSpeed(double scrollMaxSpeed) {
        this.scrollMaxSpeed = Math.abs(scrollMaxSpeed);
    }

    public double getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(double scrollTime) {
        this.scrollTime = Math.max(scrollTime, 0.00001d);
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background == null ? Color.DARK_GRAY : background;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground == null ? Color.RED : foreground;
    }

}
