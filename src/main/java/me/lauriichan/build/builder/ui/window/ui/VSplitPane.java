package me.lauriichan.build.builder.ui.window.ui;

import java.util.ArrayList;

import me.lauriichan.build.builder.ui.util.render.Area;

public class VSplitPane extends Pane {

    protected final ArrayList<Component> components = new ArrayList<>();

    protected Component top;
    protected Component bottom;

    private int count = 0;

    private int space = 10;
    private int border = 2;
    private float ratio = 0.5f;

    private int topHeight;
    private int bottomHeight;

    @Override
    public void render(final Area area) {
        renderComponent(top, area);
        renderComponent(bottom, area);
    }

    private void renderComponent(Component component, Area area) {
        if (component == null || component.isHidden()) {
            return;
        }
        component.render(area.create(component.getX(), component.getY(), component.getWidth(), component.getHeight()));
    }

    @Override
    public void update(final long deltaTime) {
        updateComponent(top, deltaTime);
        updateComponent(bottom, deltaTime);
    }

    private void updateComponent(Component component, long deltaTime) {
        if (component == null || !component.isUpdating()) {
            return;
        }
        component.update(deltaTime);
    }

    @Override
    public void exit() {
        final Component[] children = getChildren();
        for (final Component child : children) {
            child.exit();
        }
        components.clear();
    }

    public void setRatio(float ratio) {
        this.ratio = Math.min(Math.max(ratio, 0f), 1f);
        updateChildren();
    }

    public float getRatio() {
        return ratio;
    }

    public void setSpace(int space) {
        this.space = space;
        updateChildren();
    }

    public int getSpace() {
        return space;
    }

    public void setBorder(int border) {
        this.border = border;
        updateChildren();
    }

    public int getBorder() {
        return border;
    }

    public void setTop(Component top) {
        if (this.top != null) {
            this.top.setParent(null);
        }
        this.top = top;
        if (top != null) {
            top.setParent(this);
        }
        updateChildren();
    }

    public Component getTop() {
        return top;
    }

    public void setBottom(Component bottom) {
        if (this.bottom != null) {
            this.bottom.setParent(null);
        }
        this.bottom = bottom;
        if (bottom != null) {
            bottom.setParent(this);
        }
        updateChildren();
    }

    public Component getBottom() {
        return bottom;
    }

    private void updateChildren() {
        if (top == null && bottom == null) {
            return;
        }
        if (top == null) {
            bottom.setX(border);
            bottom.setY(border);
            bottom.setWidth(getWidth() - border * 2);
            bottom.setHeight(getHeight() - border * 2);
            return;
        }
        topHeight = getHeight() - border * 2;
        if (bottom != null) {
            topHeight -= space;
            topHeight = Math.round(topHeight * ratio);
            bottomHeight = getHeight() - topHeight;
            bottom.setX(border);
            bottom.setY(topHeight + space + border);
            bottom.setWidth(getWidth() - border * 2);
            bottom.setHeight(bottomHeight - space - (border * 2));
        }
        top.setX(border);
        top.setY(border);
        top.setWidth(getWidth() - border * 2);
        top.setHeight(topHeight);
    }

    @Override
    public int getChildrenCount() {
        return count;
    }

    @Override
    public Component getChild(int index) {
        if (index == 0) {
            return top;
        } else if (index == 1) {
            return bottom;
        }
        return null;
    }

    @Override
    public Component[] getChildren() {
        return new Component[] {
            top,
            bottom
        };
    }

    @Override
    protected void onWidthChange(int oldWidth, int newWidth) {
        updateChildren();
    }

    @Override
    protected void onHeightChange(int oldHeight, int newHeight) {
        updateChildren();
    }

}
