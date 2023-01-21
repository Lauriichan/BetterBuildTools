package me.lauriichan.build.builder.ui.window.ui;

import java.util.Iterator;

import me.lauriichan.build.builder.ui.util.ArrayIterator;

public abstract class ContainerPane extends Pane implements IBarComponent {

    public abstract boolean addChild(Component component);

    public abstract boolean removeChild(Component component);

    public void clear() {
        final Component[] components = getChildren();
        for (final Component component : components) {
            removeChild(component);
        }
    }

    public abstract boolean hasBar();

    public abstract void setBar(Bar<?> bar);

    public abstract Bar<?> getBar();

    public int getAddition() {
        return 0;
    }

    @Override
    public Iterator<Component> iterator() {
        return new ArrayIterator<>(getChildren());
    }

}
