package me.lauriichan.build.builder.ui.window.ui.component.tab;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseHover;
import me.lauriichan.build.builder.ui.window.input.mouse.MousePress;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseRelease;
import me.lauriichan.build.builder.ui.window.ui.TabBar;
import me.lauriichan.build.builder.ui.window.ui.component.geometry.LineSeperator;
import me.lauriichan.build.builder.ui.window.ui.component.geometry.Seperator;

public class SimpleTabBar extends TabBar {

    private Seperator seperator = new LineSeperator();
    private int size = 0;

    private Color background = Color.DARK_GRAY;

    private final ArrayList<TabButton> boxes = new ArrayList<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock read = lock.readLock();
    private final Lock write = lock.writeLock();

    private boolean triggered = false;
    private boolean selectEnabled = false;
    private int selected = -1;

    public SimpleTabBar() {
        LineSeperator seperator = new LineSeperator();
        seperator.setColor(Color.GRAY);
        seperator.setWidth(12);
        seperator.setThickness(2);
        this.seperator = seperator;
    }

    @Override
    public boolean add(TabButton box) {
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
    public boolean remove(TabButton box) {
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
    public TabButton get(int index) {
        read.lock();
        try {
            return boxes.get(index);
        } finally {
            read.unlock();
        }
    }

    @Override
    public TabButton[] getAll() {
        read.lock();
        try {
            return boxes.toArray(new TabButton[boxes.size()]);
        } finally {
            read.unlock();
        }
    }

    @Override
    public void render(Area area) {
        area.fill(background);
        TabButton[] boxes = getAll();
        int next = seperator.getWidth() + size;
        for (int index = 0; index < boxes.length; index++) {
            boxes[index].render(area.create(next * index, 0, size, getHeight()));
            if (index + 1 != boxes.length) {
                seperator.render(area.create(next * index + size, 0, seperator.getWidth(), getHeight()));
            }
        }
    }

    @Override
    public void update(long deltaTime) {
        TabButton[] boxes = getAll();
        for (TabButton box : boxes) {
            box.update(deltaTime);
        }
    }

    public void setSelectEnabled(boolean selectEnabled) {
        this.selectEnabled = selectEnabled;
        if (selected != -1) {
            int count = getCount();
            if (selected >= count) {
                return;
            }
            get(selected).setTriggered(!selectEnabled);
        }
    }

    public boolean isSelectEnabled() {
        return selectEnabled;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        if (!selectEnabled) {
            return;
        }
        int count = getCount();
        if(this.selected < count && this.selected >= 0) {
            get(this.selected).setTriggered(false);
        }
        this.selected = Math.min(count - 1, Math.max(0, selected));
        if (count != 0) {
            get(this.selected).setTriggered(true);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Seperator getSeperator() {
        return seperator;
    }

    public void setSeperator(Seperator seperator) {
        this.seperator = seperator;
    }

    @Listener
    public void onPress(MousePress press) {
        int gy = getGlobalY();
        if (!(gy <= press.getY() && (getHeight() + gy) >= press.getY())) {
            return;
        }
        press.consume();
        int count = getCount();
        int next = seperator.getWidth() + size;
        for (int index = 0; index < count; index++) {
            int x = (next * index);
            int endX = (next * index) + size;
            if (!(x <= press.getX() && endX >= press.getX())) {
                continue;
            }
            TabButton box = get(index);
            if (box == null) {
                break;
            }
            if (selectEnabled && selected == index) {
                break;
            }
            box.press(press.getButton());
            if (selected != index && selected != -1 && selected < count) {
                if (selectEnabled) {
                    get(selected).setTriggered(false);
                    box.setTriggered(true);
                }
                selected = index;
            }
            break;
        }
    }

    @Listener
    public void onRelease(MouseRelease release) {
        TabButton[] children = getAll();
        for (TabButton button : children) {
            button.release(release.getButton());
        }
    }

    @Listener
    public void onHover(MouseHover hover) {
        boolean consumed = hover.isConsumed();
        if (isInside(hover.getX(), hover.getY())) {
            hover.consume();
        }
        int gy = getGlobalY();
        if (!(gy <= hover.getY() && (getHeight() + gy) >= hover.getY())) {
            if (!triggered) {
                return;
            }
            triggered = false;
            TabButton[] boxes = getAll();
            for (int index = 0; index < boxes.length; index++) {
                if (selected == index && selectEnabled) {
                    continue;
                }
                boxes[index].setTriggered(false);
            }
            return;
        }
        if (consumed) {
            return;
        }
        int count = getCount();
        int next = seperator.getWidth() + size;
        for (int index = 0; index < count; index++) {
            int x = (next * index);
            int endX = (next * index) + size;
            if (!(x <= hover.getX() && endX >= hover.getX())) {
                if (selected == index && selectEnabled) {
                    continue;
                }
                setTriggered(index, false);
                continue;
            }
            setTriggered(index, true);
            triggered = true;
        }
    }

    private void setTriggered(int index, boolean triggered) {
        TabButton box = get(index);
        if (box == null) {
            return;
        }
        box.setTriggered(triggered);
    }

}