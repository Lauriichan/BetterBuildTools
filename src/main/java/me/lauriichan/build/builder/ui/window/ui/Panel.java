package me.lauriichan.build.builder.ui.window.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;

import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.util.tick.Ticker;
import me.lauriichan.build.builder.ui.window.event.EventProvider;
import me.lauriichan.build.builder.ui.window.event.IEvent;
import me.lauriichan.build.builder.ui.window.input.InputProvider;
import me.lauriichan.build.builder.ui.window.ui.component.bar.SimpleRootBar;

public class Panel extends Component implements IBarComponent {

    private static final AtomicInteger ID = new AtomicInteger(0);

    public static final class ResizeEvent implements IEvent {

        private final Panel panel;

        public ResizeEvent(final Panel panel) {
            this.panel = panel;
        }

        public Panel getPanel() {
            return panel;
        }

    }

    protected final TransferFrame frame;

    protected final RootBar bar;
    protected final Pane pane;

    protected final InputProvider input;
    protected final EventProvider event;

    protected final int id = ID.getAndIncrement();
    protected final Ticker renderTick = new Ticker("Render - " + id);
    protected final Ticker updateTick = new Ticker("Update - " + id);

    protected Color background = Color.BLACK;
    protected Font font = new Font("Open Sans", Font.PLAIN, 12);

    private boolean shouldExit = false;

    public Panel() {
        this(new BasicPane());
    }

    public Panel(final Pane pane) {
        this(new SimpleRootBar(), pane);
    }

    public Panel(final RootBar bar) {
        this(bar, new BasicPane());
    }

    public Panel(final RootBar bar, final Pane pane) {

        setHidden(true);
        setUpdating(true);

        final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
            .getDefaultConfiguration();
        frame = new TransferFrame(this, config);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        this.input = new InputProvider(this);
        input.register(this);
        this.event = new EventProvider(this);
        event.register(this);
        this.pane = pane;
        pane.setParent(this);
        this.bar = bar;
        bar.setParent(this);
        renderTick.add(this::render);
        updateTick.add(this::update);
    }

    @Override
    public final InputProvider getInput() {
        return input;
    }

    @Override
    public final EventProvider getEvent() {
        return event;
    }

    public void setCursor(final int cursor) {
        if (getCursor() == cursor) {
            return;
        }
        final Cursor obj = Cursor.getPredefinedCursor(cursor);
        if (obj == null) {
            setCursor(Cursor.DEFAULT_CURSOR);
            return;
        }
        frame.setCursor(obj);
    }

    public int getCursor() {
        return frame.getCursor().getType();
    }

    public Ticker getRenderTick() {
        return renderTick;
    }

    public Ticker getUpdateTick() {
        return updateTick;
    }

    @Override
    public final boolean isRoot() {
        return true;
    }

    public int getId() {
        return id;
    }

    public boolean isRunning() {
        return !renderTick.isStopped();
    }

    @Override
    public void exit() {
        bar.exit();
        pane.exit();
        shouldExit = true;
        updateTick.stop();
    }

    public boolean await() {
        while (isRunning()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
        return !isRunning();
    }

    @Override
    public void setX(final int x) {
        super.setX(x);
        frame.setLocation(x, getY());
    }

    @Override
    public void setY(final int y) {
        super.setY(y);
        frame.setLocation(getX(), y);
    }

    public void setBarHeight(final int height) {
        bar.setHeight(height);
    }

    public int getBarHeight() {
        return bar.getHeight();
    }

    @Override
    public void setHidden(final boolean hidden) {
        super.setHidden(hidden);
        if (frame != null) {
            frame.setVisible(!hidden);
        }
    }

    public Font getFont() {
        return font;
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(final Color background) {
        this.background = background;
    }

    public void setTargetFps(final int fps) {
        renderTick.setLength(Math.max(1, Math.floorDiv(1000, Math.max(1, fps))));
    }

    public void setTargetTps(final int tps) {
        updateTick.setLength(Math.max(1, Math.floorDiv(1000, Math.max(1, tps))));
    }

    public int getFps() {
        return renderTick.getTps();
    }

    public int getTps() {
        return updateTick.getTps();
    }

    public void show() {
        setHidden(false);
    }

    public void hide() {
        setHidden(true);
    }

    public final Frame getFrame() {
        return frame;
    }

    public final Pane getPane() {
        return pane;
    }

    public final RootBar getBar() {
        return bar;
    }

    private void render(final long deltaTime) {
        if (isHidden() || shouldExit) {
            if (shouldExit) {
                frame.dispose();
                renderTick.stop();
            }
            return;
        }
        frame.repaint();
    }

    @Override
    public void update(final long deltaTime) {
        if (!isUpdating()) {
            return;
        }
        bar.update(deltaTime);
        pane.update(deltaTime);
    }

    @Override
    public int getGlobalX() {
        return 0;
    }

    @Override
    public int getGlobalY() {
        return 0;
    }

    @Override
    public void render(final Area area) {
        bar.render(area.create(0, 0, area.getWidth(), bar.getHeight()));
        pane.render(area.create(0, bar.getHeight(), area.getWidth(), area.getHeight() - bar.getHeight()));
    }

    public void center() {
        center(0);
    }

    public void center(int screen) {
        final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screen < 0 || screen > devices.length) {
            screen = 0;
        }
        final Rectangle bounds = devices[screen].getDefaultConfiguration().getBounds();
        setPosition((bounds.width - getWidth()) / 2 + bounds.x, (bounds.height - getHeight()) / 2 + bounds.y);
    }

    public void minimize() {
        frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
    }

    public void maximize() {
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            frame.setExtendedState(Frame.NORMAL);
        } else {
            frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        }
        size.setX(frame.getWidth());
        size.setY(frame.getHeight());
        pane.setWidth(size.getX());
        pane.setHeight(size.getY() - bar.getHeight());
        frame.updateBuffer(size.getX(), size.getY());
    }

    public String getTitle() {
        return frame.getTitle();
    }

    public void setTitle(final String name) {
        frame.setTitle(name);
    }

    public Image getIcon() {
        return frame.getIconImage();
    }

    public void setIcon(final Image image) {
        frame.setIconImage(image);
    }

    @Override
    public void updateBarHeight(final int height) {
        pane.setY(height);
        pane.setHeight(getHeight() - height);
    }

    @Override
    protected void onHeightChange(int oldHeight, int newHeight) {
        pane.setHeight(newHeight - bar.getHeight());
        frame.setSize(getWidth(), newHeight);
        callEvent(new Panel.ResizeEvent(this));
    }

    @Override
    protected void onWidthChange(int oldWidth, int newWidth) {
        pane.setWidth(newWidth);
        frame.setSize(newWidth, getHeight());
        callEvent(new Panel.ResizeEvent(this));
    }

}
