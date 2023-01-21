package me.lauriichan.build.builder.ui.window.ui;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.lauriichan.build.builder.ui.util.render.Area;

public class BasicPane extends ContainerPane {

    protected final ArrayList<Component> components = new ArrayList<>();

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Lock read = lock.readLock();
    protected final Lock write = lock.writeLock();

    private Bar<?> bar;
    private int previous = 0;

    @Override
    public boolean addChild(final Component component) {
        read.lock();
        try {
            if (component.isRoot() || components.contains(component)) {
                return false;
            }
        } finally {
            read.unlock();
        }
        component.setY(component.getY() + previous);
        component.setHeight(component.getHeight() - previous);
        component.setParent(this);
        write.lock();
        try {
            return components.add(component);
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean removeChild(final Component component) {
        read.lock();
        try {
            if (component.isRoot() || !components.contains(component)) {
                return false;
            }
        } finally {
            read.unlock();
        }
        component.setY(component.getY() - previous);
        component.setHeight(component.getHeight() + previous);
        component.setParent(null);
        write.lock();
        try {
            return components.remove(component);
        } finally {
            write.unlock();
        }
    }

    @Override
    public int getChildrenCount() {
        read.lock();
        try {
            return components.size();
        } finally {
            read.unlock();
        }
    }

    @Override
    public Component getChild(final int index) {
        read.lock();
        try {
            return components.get(index);
        } finally {
            read.unlock();
        }
    }

    @Override
    public Component[] getChildren() {
        read.lock();
        try {
            return components.toArray(new Component[components.size()]);
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean hasBar() {
        return bar != null;
    }

    @Override
    public void setBar(final Bar<?> bar) {
        if (this.bar != null) {
            this.bar.setParent(null);
            updateBarHeight(0);
        }
        this.bar = bar;
        if (bar != null) {
            bar.setParent(this);
            updateBarHeight(bar.getHeight());
        }
    }

    @Override
    public Bar<?> getBar() {
        return bar;
    }

    @Override
    public int getAddition() {
        return previous;
    }

    @Override
    public void updateBarHeight(final int height) {
        final int diff = height - previous;
        previous = height;
        final Component[] children = getChildren();
        for (final Component child : children) {
            child.setY(child.getY() - diff);
            child.setHeight(child.getHeight() + diff);
        }
    }

    @Override
    public void render(final Area area) {
        if (bar != null) {
            bar.render(area.create(0, 0, area.getWidth(), bar.getHeight()));
        }
        if (getChildrenCount() == 0) {
            return;
        }
        final Component[] children = getChildren();
        for (final Component component : children) {
            if (component.isHidden()) {
                continue;
            }
            component.render(area.create(component.getX(), component.getY(), component.getWidth(), component.getHeight()));
        }
    }

    @Override
    public void update(final long deltaTime) {
        if (bar != null) {
            bar.update(deltaTime);
        }
        if (getChildrenCount() == 0) {
            return;
        }
        final Component[] children = getChildren();
        for (final Component component : children) {
            if (!component.isUpdating()) {
                continue;
            }
            component.update(deltaTime);
        }
    }

    @Override
    public void exit() {
        final Component[] children = getChildren();
        for (final Component child : children) {
            child.exit();
        }
        components.clear();
    }

}
