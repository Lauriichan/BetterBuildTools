package me.lauriichan.build.builder.ui.window.ui.component.bar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.lauriichan.build.builder.ui.util.IBoxRenderer;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseClick;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseHover;
import me.lauriichan.build.builder.ui.window.ui.Panel;
import me.lauriichan.build.builder.ui.window.ui.RootBar;

public final class SimpleRootBar extends RootBar {

    private int lineThickness = 2;
    private int boxOffset = 0;
    private int offset = 0;
    private int size = 0;

    private Color background = Color.DARK_GRAY;

    private final ArrayList<BarBox> boxes = new ArrayList<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock read = lock.readLock();
    private final Lock write = lock.writeLock();

    private boolean triggered = false;

    @Override
    public boolean add(final BarBox box) {
        read.lock();
        try {
            if (boxes.contains(box)) {
                return false;
            }
        } finally {
            read.unlock();
        }
        write.lock();
        try {
            return boxes.add(box);
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean remove(final BarBox box) {
        read.lock();
        try {
            if (!boxes.contains(box)) {
                return false;
            }
        } finally {
            read.unlock();
        }
        write.lock();
        try {
            return boxes.remove(box);
        } finally {
            write.unlock();
        }
    }

    @Override
    public int getCount() {
        read.lock();
        try {
            return boxes.size();
        } finally {
            read.unlock();
        }
    }

    @Override
    public BarBox get(final int index) {
        read.lock();
        try {
            return boxes.get(index);
        } finally {
            read.unlock();
        }
    }

    @Override
    public BarBox[] getAll() {
        read.lock();
        try {
            return boxes.toArray(new BarBox[boxes.size()]);
        } finally {
            read.unlock();
        }
    }

    @Override
    public void render(final Area area) {
        final Color color = area.getColor();
        area.setColor(background);
        area.fill(background);
        final BarBox[] boxes = getAll();
        final int next = boxOffset * 2 + size;
        final int distance = area.getWidth() - next;
        for (int index = 0; index < boxes.length; index++) {
            boxes[index].render(area.create(distance - next * index, boxOffset, size, size));
        }
        area.setColor(color);
    }

    @Override
    public void update(final long deltaTime) {
        final BarBox[] boxes = getAll();
        for (final BarBox box : boxes) {
            box.update(deltaTime);
        }
    }

    @Override
    public BarBox createBox(final IBoxRenderer renderer) {
        final BarBox box = new BarBox((area, color) -> renderer.render(area, color, size / 6 + offset, size / 3 * 2, lineThickness));
        add(box);
        return box;
    }

    @Override
    public void setHeight(final int height) {
        super.setHeight(height);
        final int tmp = height / 3;
        this.boxOffset = tmp / 2;
        this.size = height - tmp;
    }

    @Override
    public void setBackground(final Color background) {
        this.background = background;
    }

    public void setLineThickness(final int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Listener
    public void onClick(final MouseClick click) {
        if (!isInside(click.getX(), click.getY())) {
            return;
        }
        click.consume();
        if (((boxOffset > click.getY()) || (size + boxOffset < click.getY()))) {
            return;
        }
        final int count = getCount();
        final int next = boxOffset * 2 + size;
        final int distance = click.getProvider().getPanel().getWidth() - next;
        for (int index = 0; index < count; index++) {
            final int x = distance - next * index;
            final int endX = distance - next * index + size;
            if (((x > click.getX()) || (endX < click.getX()))) {
                continue;
            }
            final BarBox box = get(index);
            if (box == null) {
                break;
            }
            box.click(click.getButton());
            break;
        }
    }

    @Listener
    public void onHover(final MouseHover hover) {
        final boolean consumed = hover.isConsumed();
        if (isInside(hover.getX(), hover.getY())) {
            hover.consume();
        }
        if (((boxOffset > hover.getY()) || (size + boxOffset < hover.getY()))) {
            if (!triggered) {
                return;
            }
            triggered = false;
            final BarBox[] boxes = getAll();
            for (final BarBox box : boxes) {
                box.setTriggered(false);
            }
            return;
        }
        if (consumed) {
            return;
        }
        final int count = getCount();
        final int next = boxOffset * 2 + size;
        final int distance = hover.getProvider().getPanel().getWidth() - next;
        for (int index = 0; index < count; index++) {
            final int x = distance - next * index;
            final int endX = distance - next * index + size;
            if (((x > hover.getX()) || (endX < hover.getX()))) {
                setTriggered(index, false);
                continue;
            }
            setTriggered(index, true);
            triggered = true;
        }
    }

    private void setTriggered(final int index, final boolean triggered) {
        final BarBox box = get(index);
        if (box == null) {
            return;
        }
        box.setTriggered(triggered);
    }

    @Listener
    public void onDrag(final MouseDrag drag) {
        if (drag.isConsumed() || drag.getOldY() > getHeight() || drag.getButton() != MouseButton.LEFT) {
            return;
        }
        drag.consume();
        final Panel panel = drag.getProvider().getPanel();
        panel.setPosition(panel.getX() + drag.getX() - drag.getOldX(), panel.getY() + drag.getY() - drag.getOldY());
    }

}
