package me.lauriichan.build.builder.ui.window.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.lauriichan.build.builder.ui.util.DynamicArray;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.input.mouse.MousePress;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseScroll;

public class DragPane extends ContainerPane {

    private int currentY = 0;
    private int currentX = 0;

    private int contentSizeX = 1;
    private int contentSizeY = 1;

    private float zoom = 1f;
    private final float zoomSpeed = 0.01f;

    private final float horizontalMoveSpeed = 1f;
    private final float verticalMoveSpeed = 1f;

    private final int borderBuffer = 50;

    private final Color background = Color.BLACK;

    private final int barWidth = 4;
    private final int barLength = 30;
    private final Color barFill = Color.RED;
    private final Color barBackground = Color.DARK_GRAY;

    private BufferedImage buffer;
    private Area bufferArea;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    private final DynamicArray<Component> components = new DynamicArray<>();

    public DragPane() {
        updateBuffer();
    }

    @Override
    public boolean addChild(final Component component) {
        if (components.indexOf(component) != -1) {
            return false;
        }
        components.add(component);
        return true;
    }

    public boolean addChildAt(final int i, final Component component) {
        if (components.indexOf(component) != -1) {
            return false;
        }
        components.addAt(i, component);
        return true;
    }

    @Override
    public boolean removeChild(final Component component) {
        final int index = components.indexOf(component);
        if (index == -1) {
            return false;
        }
        components.remove(index);
        return true;
    }

    @Override
    public int getChildrenCount() {
        return components.length();
    }

    @Override
    public Component getChild(final int index) {
        return components.get(index);
    }

    @Override
    public Component[] getChildren() {
        return components.asArray(Component[]::new);
    }

    public int getContentSizeX() {
        return contentSizeX;
    }

    public int getContentSizeY() {
        return contentSizeY;
    }

    public void setContentSizeX(final int contentSizeX) {
        this.contentSizeX = Math.max(contentSizeX, 1);
        updateBuffer();
    }

    public void setContentSizeY(final int contentSizeY) {
        this.contentSizeY = Math.max(contentSizeY, 1);
        updateBuffer();
    }

    public void center() {
        currentX = (int) Math.max((contentSizeX * zoom - size.getX()) / 2, 0);
        currentY = (int) Math.max((contentSizeY * zoom - size.getY()) / 2, 0);
    }

    public void centerZoom() {
        final float tmpX = size.getX() / (float) (contentSizeX + borderBuffer);
        final float tmpY = size.getY() / (float) (contentSizeY + borderBuffer);
        zoom = Math.min(tmpX, tmpY);
        center();
    }

    private void updateBuffer() {
        writeLock.lock();
        try {
            if (buffer != null) {
                buffer.flush();
            }
            buffer = new BufferedImage(contentSizeX, contentSizeY, BufferedImage.TYPE_INT_ARGB);
            bufferArea = new Area((Graphics2D) buffer.getGraphics(), background, -1, -1, contentSizeX, contentSizeY);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void render(final Area area) {
        Area outputArea;
        BufferedImage output;
        readLock.lock();
        try {
            output = buffer;
            outputArea = bufferArea;
            renderChildren(bufferArea);
        } finally {
            readLock.unlock();
        }
        drawBuffer(area, output);
        outputArea.clear();
        drawBars(area);
    }

    private void renderChildren(final Area area) {
        final Component[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            final Component component = children[i];
            if (component.isHidden()) {
                continue;
            }
            component.render(area.create(component.getX(), component.getY(), component.getWidth(), component.getHeight()));
        }
    }

    @Override
    public void update(final long deltaTime) {
        final Component[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            final Component component = children[i];
            if (!component.isUpdating()) {
                continue;
            }
            component.update(deltaTime);
        }
    }

    private void drawBuffer(final Area area, final BufferedImage buffer) {
        int width = area.getWidth();
        if (size.getY() < contentSizeY * zoom) {
            width -= barWidth;
        }
        int height = area.getHeight();
        if (size.getX() < contentSizeX * zoom) {
            height -= barWidth;
        }
        Image image = buffer;
        int iWidth = buffer.getWidth();
        int iHeight = buffer.getHeight();
        if (zoom != 1) {
            iWidth *= zoom;
            iHeight *= zoom;
            image = buffer.getScaledInstance((int) (buffer.getWidth() * zoom), (int) (buffer.getHeight() * zoom), Image.SCALE_FAST);
        }
        final int ix = size.getX() >= contentSizeX * zoom ? (int) ((area.getWidth() - iWidth) / 2) : 0;
        final int iy = size.getY() >= contentSizeY * zoom ? (int) ((area.getHeight() - iHeight) / 2) : 0;
        area.getGraphics().drawImage(image, ix, iy, ix + width, iy + height, currentX, currentY, currentX + width, currentY + height, null);
    }

    private void drawBars(final Area area) {
        if (size.getX() < contentSizeX * zoom) {
            drawHorizontalBar(area);
        }
        if (size.getY() < contentSizeY * zoom) {
            drawVerticalBar(area);
        }
    }

    private void drawHorizontalBar(final Area area) {
        final float max = (float) Math.floor(contentSizeX * zoom - size.getX());
        area.drawRectangle(0, size.getY() - barWidth, size.getX(), barWidth, barBackground);
        final float progress = currentX / max;
        final int extra = size.getY() < contentSizeY * zoom ? barWidth : 0;
        area.drawRectangle((int) (progress * (area.getWidth() - barLength - extra)), size.getY() - barWidth, barLength, barWidth, barFill);
    }

    private void drawVerticalBar(final Area area) {
        final float max = (float) Math.floor(contentSizeY * zoom - size.getY());
        area.drawRectangle(size.getX() - barWidth, 0, barWidth, size.getY(), barBackground);
        final float progress = currentY / max;
        final int extra = size.getX() < contentSizeX * zoom ? barWidth : 0;
        area.drawRectangle(size.getX() - barWidth, (int) (progress * (area.getHeight() - barLength - extra)), barWidth, barLength, barFill);
    }

    private int moveX;
    private int moveY;

    @Listener
    public void onScroll(final MouseScroll scroll) {
        if (scroll.isConsumed() || !isInside(scroll.getX(), scroll.getY())) {
            return;
        }
        float zoomBuf = zoom;
        zoomBuf -= scroll.getRotation() * zoomSpeed;
        final int widthBuf = (int) (buffer.getWidth() * zoomBuf);
        final int heightBuf = (int) (buffer.getHeight() * zoomBuf);
        if (widthBuf < 1 || widthBuf > contentSizeX || heightBuf < 1 || heightBuf > contentSizeY) {
            return;
        }
        final float buf = zoom;
        zoom = zoomBuf;
        scroll.consume();
        if (buf > zoomBuf) {
            int tmp = (int) Math.floor(contentSizeX * zoom) - size.getX();
            if (tmp > 0 && currentX * 2f > tmp) {
                currentX = tmp;
            } else if (tmp <= 0) {
                currentX = 0;
            }
            tmp = (int) Math.floor(contentSizeY * zoom) - size.getY();
            if (tmp > 0 && currentY * 2f > tmp) {
                currentY = tmp;
            } else if (tmp <= 0) {
                currentY = 0;
            }
        }
    }

    @Listener
    public void onDrag(final MouseDrag drag) {
        if (drag.isConsumed() || drag.getButton() != MouseButton.MIDDLE || moveX == -1 && moveY == -1) {
            return;
        }
        if (size.getX() < contentSizeX * zoom) {
            final int max = (int) Math.floor(contentSizeX * zoom - size.getX());
            final int move = moveX - drag.getX();
            int x = currentX + (int) (move * horizontalMoveSpeed);
            if (x < 0) {
                x = 0;
            } else if (x > max) {
                x = max;
            }
            currentX = x;
            moveX = drag.getX();
        }
        if (size.getY() < contentSizeY * zoom) {
            final int max = (int) Math.floor(contentSizeY * zoom - size.getY());
            final int move = moveY - drag.getY();
            int y = currentY + (int) (move * verticalMoveSpeed);
            if (y < 0) {
                y = 0;
            } else if (y > max) {
                y = max;
            }
            currentY = y;
            moveY = drag.getY();
        }
        drag.consume();
    }

    @Listener
    public void onPress(final MousePress press) {
        if (!isInside(press.getX(), press.getY())) {
            moveX = -1;
            moveY = -1;
            return;
        }
        moveX = press.getX();
        moveY = press.getY();
    }

    @Override
    public void setBar(final Bar<?> bar) {}

    @Override
    public Bar<?> getBar() {
        return null;
    }

    @Override
    public boolean hasBar() {
        return false;
    }

    @Override
    public void updateBarHeight(final int difference) {}

}
