package me.lauriichan.build.builder.ui.window.ui;

import java.util.ArrayList;

import me.lauriichan.build.builder.ui.util.render.Area;

public class HSplitPane extends Pane {

    protected final ArrayList<Component> components = new ArrayList<>();

    protected Component left;
    protected Component right;

    private int count = 0;

    private int space = 10;
    private int border = 2;
    private float ratio = 0.5f;

    private int leftWidth;
    private int rightWidth;

    @Override
    public void render(final Area area) {
        renderComponent(left, area);
        renderComponent(right, area);
    }

    private void renderComponent(Component component, Area area) {
        if (component == null || component.isHidden()) {
            return;
        }
        component.render(area.create(component.getX(), component.getY(), component.getWidth(), component.getHeight()));
    }

    @Override
    public void update(final long deltaTime) {
        updateComponent(left, deltaTime);
        updateComponent(right, deltaTime);
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

    public Component getLeft() {
        return left;
    }

    public void setRight(Component right) {
        if (this.right != null) {
            this.right.setParent(null);
        }
        this.right = right;
        if (right != null) {
            right.setParent(this);
        }
        updateChildren();
    }

    public Component getRight() {
        return right;
    }

    private void updateChildren() {
        if (left == null && right == null) {
            return;
        }
        if (left == null) {
            right.setX(border);
            right.setY(border);
            right.setWidth(getWidth() - border * 2);
            right.setHeight(getHeight() - border * 2);
            return;
        }
        leftWidth = getWidth() - border * 2;
        if (right != null) {
            leftWidth -= space;
            leftWidth = Math.round(leftWidth * ratio);
            rightWidth = getWidth() - leftWidth;
            right.setX(leftWidth + space + border);
            right.setY(border);
            right.setWidth(rightWidth - space - (border * 2));
            right.setHeight(getHeight() - border * 2);
        }
        left.setX(border);
        left.setY(border);
        left.setWidth(leftWidth);
        left.setHeight(getHeight() - border * 2);
    }

    public void setLeft(Component left) {
        if (this.left != null) {
            this.left.setParent(null);
        }
        this.left = left;
        if (left != null) {
            left.setParent(this);
        }
        updateChildren();
    }

    @Override
    public int getChildrenCount() {
        return count;
    }

    @Override
    public Component getChild(int index) {
        if (index == 0) {
            return left;
        } else if (index == 1) {
            return right;
        }
        return null;
    }

    @Override
    public Component[] getChildren() {
        return new Component[] {
            left,
            right
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
