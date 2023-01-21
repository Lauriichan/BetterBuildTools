package me.lauriichan.build.builder.ui.window.ui;

import java.util.Iterator;

import me.lauriichan.build.builder.ui.util.ArrayIterator;

public abstract class Pane extends Component implements Iterable<Component> {

    public abstract int getChildrenCount();

    public abstract Component getChild(int index);

    public abstract Component[] getChildren();

    public void applyChildren() {
        final Component[] components = getChildren();
        for (final Component component : components) {
            component.setHidden(isHidden());
            component.setUpdating(isUpdating());
            if (!(component instanceof Pane)) {
                continue;
            }
            ((Pane) component).applyChildren();
        }
    }

    @Override
    public Iterator<Component> iterator() {
        return new ArrayIterator<>(getChildren());
    }

}
