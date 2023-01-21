package me.lauriichan.build.builder.ui.window.ui;

import java.awt.Cursor;

import me.lauriichan.build.builder.ui.util.IntList;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseHover;
import me.lauriichan.build.builder.ui.window.input.mouse.MousePress;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseRelease;

public final class ResizeablePanel extends Panel {

    private final IntList cursorBlacklist = new IntList(Cursor.NW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR);

    private int resizeRadius = 5;

    private int pressedCursor = 0;
    private int startX, startY;
    private int pX, pY, pW, pH;

    public ResizeablePanel() {}

    public ResizeablePanel(final Pane pane) {
        super(pane);
    }

    public ResizeablePanel(final RootBar bar) {
        super(bar);
    }

    public ResizeablePanel(final RootBar bar, final Pane pane) {
        super(bar, pane);
    }

    public void setResizeRadius(final int resizeRadius) {
        this.resizeRadius = Math.max(resizeRadius, 1);
    }

    public int getResizeRadius() {
        return resizeRadius;
    }

    public boolean isCursorEnabled(final int cursorId) {
        return !cursorBlacklist.contains(cursorId);
    }

    public void setCursorEnabled(final int cursorId, final boolean state) {
        if (state) {
            cursorBlacklist.removeValue(cursorId);
            return;
        }
        if (cursorBlacklist.contains(cursorId)) {
            return;
        }
        cursorBlacklist.add(cursorId);
    }

    @Listener
    public void onHover(final MouseHover hover) {
        if (hover.isConsumed()) {
            return;
        }
        final int cursor = findCursor(hover.getX(), hover.getY());
        if (isCursorEnabled(cursor)) {
            setCursor(cursor);
        }
    }

    @Listener
    public void onPress(final MousePress press) {
        if (press.isConsumed() || press.getButton() != MouseButton.LEFT) {
            return;
        }
        int cursor = findCursor(press.getX(), press.getY());
        if(!isCursorEnabled(cursor)) {
            return;
        }
        press.consume();
        pressedCursor = cursor;
        startX = press.getScreenX();
        startY = press.getScreenY();
        pX = getX();
        pY = getY();
        pW = getWidth();
        pH = getHeight();
    }

    @Listener
    public void onRelease(final MouseRelease release) {
        if (release.isConsumed() || release.getButton() != MouseButton.LEFT || pressedCursor == -1) {
            return;
        }
        release.consume();
        pressedCursor = -1;
        startX = startY = 0;
        pX = pY = pW = pH = 0;
    }

    @Listener
    public void onDrag(final MouseDrag drag) {
        if (drag.isConsumed() || pressedCursor == -1) {
            return;
        }
        drag.consume();
        final int diffX = startX - drag.getScreenX();
        final int diffY = startY - drag.getScreenY();
        switch (pressedCursor) {
        case Cursor.N_RESIZE_CURSOR:
            resizeTop(diffX, diffY);
            break;
        case Cursor.S_RESIZE_CURSOR:
            resizeBottom(diffX, diffY);
            break;
        case Cursor.E_RESIZE_CURSOR:
            resizeRight(diffX, diffY);
            break;
        case Cursor.W_RESIZE_CURSOR:
            resizeLeft(diffX, diffY);
            break;
        case Cursor.NW_RESIZE_CURSOR:
            resizeLeft(diffX, diffY);
            resizeTop(diffX, diffY);
            break;
        case Cursor.NE_RESIZE_CURSOR:
            resizeRight(diffX, diffY);
            resizeTop(diffX, diffY);
            break;
        case Cursor.SW_RESIZE_CURSOR:
            resizeLeft(diffX, diffY);
            resizeBottom(diffX, diffY);
            break;
        case Cursor.SE_RESIZE_CURSOR:
            resizeRight(diffX, diffY);
            resizeBottom(diffX, diffY);
            break;
        }
    }

    private void resizeRight(final int x, final int y) {
        setWidth(pW - x);
    }

    private void resizeLeft(final int x, final int y) {
        setWidth(pW + x);
        final int diff = pW - getWidth();
        if (diff == 0) {
            return;
        }
        setX(pX + diff);
    }

    private void resizeBottom(final int x, final int y) {
        setHeight(pH - y);
    }

    private void resizeTop(final int x, final int y) {
        setHeight(pH + y);
        final int diff = pH - getHeight();
        if (diff == 0) {
            return;
        }
        setY(pY + diff);
    }

    private int findCursor(final int x, final int y) {
        if (x <= resizeRadius) {
            if (y <= resizeRadius) {
                return Cursor.NW_RESIZE_CURSOR;
            } else if (y >= getHeight() - resizeRadius) {
                return Cursor.SW_RESIZE_CURSOR;
            }
            return Cursor.W_RESIZE_CURSOR;
        }
        if (x >= getWidth() - resizeRadius) {
            if (y <= resizeRadius) {
                return Cursor.NE_RESIZE_CURSOR;
            } else if (y >= getHeight() - resizeRadius) {
                return Cursor.SE_RESIZE_CURSOR;
            }
            return Cursor.E_RESIZE_CURSOR;
        } else if (y <= resizeRadius) {
            return Cursor.N_RESIZE_CURSOR;
        } else if (y >= getHeight() - resizeRadius) {
            return Cursor.S_RESIZE_CURSOR;
        }
        return Cursor.DEFAULT_CURSOR;
    }

}
