package me.lauriichan.build.builder.ui.window.ui;

import java.awt.Color;
import java.util.Iterator;

import me.lauriichan.build.builder.ui.util.ArrayIterator;
import me.lauriichan.build.builder.ui.window.event.EventProvider;
import me.lauriichan.build.builder.ui.window.event.IEvent;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public abstract class Bar<E extends IBaseComponent> implements IBaseComponent, Iterable<E> {

    private IBarComponent parent;

    private int height = 0;
    private InputProvider input;
    private EventProvider event;

    private boolean hidden = false;
    private boolean update = true;

    protected final void setParent(final IBarComponent component) {
        if (component == null) {
            parent = null;
            input.unregister(this);
            input = null;
            event.unregister(this);
            event = null;
            return;
        }
        if (component.getInput() == null) {
            throw new IllegalArgumentException("Component isn't initialised (no InputProvider)");
        }
        input = component.getInput();
        input.register(this);
        event = component.getEvent();
        event.register(this);
        parent = component;
    }

    public InputProvider getInput() {
        return input;
    }
    
    public EventProvider getEvent() {
        return event;
    }
    
    public void callEvent(IEvent event) {
        if(this.event == null) {
            return;
        }
        this.event.call(event);
    }

    @Override
    public boolean isUpdating() {
        return update;
    }

    @Override
    public void setUpdating(final boolean update) {
        this.update = update;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public IComponent getParent() {
        return parent;
    }

    public boolean hasContainer() {
        return hasParent() ? parent.hasContainer() : false;
    }

    public IComponent getContainer() {
        return hasParent() ? parent.getContainer() : null;
    }

    public boolean hasRoot() {
        return input != null;
    }

    public IComponent getRoot() {
        return input == null ? null : input.getPanel();
    }
    
    public int getGlobalX() {
        return hasParent() ? parent.getGlobalX() : 0;
    }
    
    public int getGlobalY() {
        return hasParent() ? parent.getGlobalY() : 0;
    }

    public boolean isInside(final int x, final int y) {
        final int gx = getGlobalX();
        final int gy = getGlobalY();
        return gx <= x && gx + getWidth() >= x && gy <= y && gy + getHeight() >= y;
    }

    public void setHeight(final int height) {
        this.height = height;
        if (hasParent()) {
            parent.updateBarHeight(height);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return hasParent() ? getParent().getWidth() : input == null ? 0 : input.getPanel().getWidth();
    }

    public abstract void setBackground(Color color);

    public abstract boolean add(E component);

    public abstract boolean remove(E component);

    public abstract int getCount();

    public abstract E get(int index);

    public abstract E[] getAll();

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(getAll());
    }

}
