package me.lauriichan.build.builder.ui.window.ui.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.lauriichan.build.builder.ui.util.TimeHelper;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseClick;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseScroll;
import me.lauriichan.build.builder.ui.window.ui.Component;
import me.lauriichan.build.builder.ui.window.ui.animation.Animators;

public class RadioList extends Component {

    protected final ArrayList<RadioButton> components = new ArrayList<>();

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Lock read = lock.readLock();
    protected final Lock write = lock.writeLock();

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

    private Color line = Color.BLACK;
    private Color background = Color.BLACK;
    private int lineThickness = 2;

    private int barWidth = 4;
    private int barHeight = 30;
    private Color barFill = Color.RED;
    private Color barBackground = Color.DARK_GRAY;

    private int current = -1;

    public boolean addChild(RadioButton component) {
        if (!addChildImpl(component)) {
            return false;
        }
        currentY += componentOffset;
        component.setY(currentY);
        component.setHeight(componentHeight);
        currentY += componentHeight;
        updateScrollY();
        return true;
    }

    private boolean addChildImpl(RadioButton component) {
        read.lock();
        try {
            if (component.isRoot() || components.contains(component)) {
                return false;
            }
        } finally {
            read.unlock();
        }
        component.setParent(this);
        write.lock();
        try {
            return components.add(component);
        } finally {
            write.unlock();
        }
    }

    public boolean removeChild(RadioButton component) {
        if (!removeChildImpl(component)) {
            return false;
        }
        currentY -= (componentOffset + componentHeight);
        updateScrollY();
        updateButtons();
        return true;
    }

    private void updateButtons() {
        RadioButton[] buttons = getChildren();
        for (int index = 0; index < buttons.length; index++) {
            RadioButton button = buttons[index];
            button.setY(componentOffset + (index * (componentOffset + componentHeight)));
            button.setHeight(componentHeight);
        }
    }

    private boolean removeChildImpl(RadioButton component) {
        read.lock();
        try {
            if (component.isRoot() || !components.contains(component)) {
                return false;
            }
        } finally {
            read.unlock();
        }
        component.setParent(null);
        int idx = components.indexOf(component);
        if (idx <= current) {
            current = -1;
        }
        write.lock();
        try {
            return components.remove(component);
        } finally {
            write.unlock();
        }
    }

    public int getChildrenCount() {
        read.lock();
        try {
            return components.size();
        } finally {
            read.unlock();
        }
    }

    public RadioButton getChild(int index) {
        read.lock();
        try {
            return components.get(index);
        } finally {
            read.unlock();
        }
    }

    public RadioButton[] getChildren() {
        read.lock();
        try {
            return components.toArray(new RadioButton[components.size()]);
        } finally {
            read.unlock();
        }
    }

    public void clear() {
        RadioButton[] components = getChildren();
        for (RadioButton component : components) {
            removeChild(component);
        }
        currentY = 0;
        updateScrollY();
    }

    private void updateScrollY() {
        int offset = componentHeight - ((componentHeight / 2) + componentOffset * 2) + (lineThickness * 2);
        scrollY = currentY - getHeight() + offset;
        if (scroll > scrollY) {
            scroll = scrollY > 2 * offset ? -scrollY - 2 * offset : 0;
        }
    }

    @Override
    public void render(Area area) {
        area.fillOutline(background, lineThickness, line);
        if (getChildrenCount() == 0) {
            return;
        }
        drawBar(area, Math.abs(scroll / scrollY));
        Component[] children = getChildren();
        for (Component component : children) {
            if (component.isHidden()) {
                continue;
            }
            component.render(area.create(component.getX(), component.getY(), component.getWidth(), component.getHeight()));
        }
    }

    private void drawBar(Area area, double percentage) {
        if (!isScrollEnabled()) {
            return;
        }
        int width = area.getWidth() - barWidth - lineThickness;
        area.drawRectangle(width, lineThickness, barWidth, area.getHeight(), barBackground);
        area.drawRectangle(width, (int) (percentage * (area.getHeight() - barHeight - lineThickness)), barWidth, barHeight, barFill);
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            scrollVelocity = 0;
            scrollDecay = 0;
        }
        RadioButton[] buttons = getChildren();
        for (RadioButton button : buttons) {
            button.setHidden(hidden);
        }
    }

    @Override
    public void setUpdating(boolean update) {
        super.setUpdating(update);
        RadioButton[] buttons = getChildren();
        for (RadioButton button : buttons) {
            button.setUpdating(update);
        }
    }

    @Override
    public void update(long deltaTime) {
        updateScroll(deltaTime);
        updateYPosition();
        if (getChildrenCount() == 0) {
            return;
        }
        Component[] children = getChildren();
        for (Component component : children) {
            if (!component.isUpdating()) {
                continue;
            }
            component.update(deltaTime);
        }
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
        int by = getGlobalY() + lineThickness;
        int height = getHeight();
        if (by > y || by + height < y) {
            return;
        }
        int x = drag.getOldX();
        int bx = getGlobalX() - lineThickness + getWidth();
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
        if (!(gx - lineThickness <= x && gx + getWidth() >= x && gy <= y && gy + getHeight() >= y)) {
            return;
        }
        int bx = gx + getWidth();
        if (isScrollEnabled() && (bx - barWidth <= x && bx >= x && gy + lineThickness <= y && gy + getHeight() - lineThickness >= y)) {
            click.consume();
            scroll = -scrollY
                * Math.max(0, Math.min(1, (click.getY() - gy - lineThickness) / (double) (getHeight() - barHeight - lineThickness)));
            return;
        }
        RadioButton[] buttons = getChildren();
        for (int index = 0; index < buttons.length; index++) {
            RadioButton button = buttons[index];
            if (!button.isInside(x, y)) {
                continue;
            }
            click.consume();
            if (index == current) {
                break;
            }
            if (current < buttons.length && current >= 0) {
                RadioButton previous = buttons[current];
                if (previous.isPressed()) {
                    previous.press();
                }
            }
            button.press();
            current = index;
            break;
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

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (this.current == current) {
            return;
        }
        if (!(this.current >= components.size() || this.current < 0)) {
            RadioButton button = components.get(this.current);
            if (button.isPressed()) {
                button.press();
            }
        }
        this.current = Math.min(components.size(), Math.max(-1, current));
        if (this.current == components.size() || this.current == -1) {
            return;
        }
        RadioButton button = components.get(this.current);
        if (!button.isPressed()) {
            button.press();
        }
    }

    public boolean isScrollEnabled() {
        return scrollY > 0;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLine(Color line) {
        this.line = line;
    }

    public Color getLine() {
        return line;
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
        updateButtons();
    }

    public int getComponentHeight() {
        return componentHeight;
    }

    public void setComponentOffset(int componentOffset) {
        this.componentOffset = componentOffset;
        updateButtons();
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