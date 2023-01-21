package me.lauriichan.build.builder.ui;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.BoxRenderers;
import me.lauriichan.build.builder.ui.util.ColorParser;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.ui.BasicPane;
import me.lauriichan.build.builder.ui.window.ui.Panel;
import me.lauriichan.build.builder.ui.window.ui.ResizeablePanel;
import me.lauriichan.build.builder.ui.window.ui.RootBar;
import me.lauriichan.build.builder.ui.window.ui.VSplitPane;
import me.lauriichan.build.builder.ui.window.ui.component.Label;
import me.lauriichan.build.builder.ui.window.ui.component.LogDisplay;
import me.lauriichan.build.builder.ui.window.ui.component.ScrollBar;
import me.lauriichan.build.builder.ui.window.ui.component.bar.BarBox;
import me.lauriichan.build.builder.ui.window.ui.component.geometry.Rectangle;

public final class ProgressUI {

    private final ResizeablePanel panel;

    private final LogDisplay display;
    private final BasicPane headerPane;

    private final Rectangle progressBar;
    private final Rectangle progressOuterBox;
    private final Rectangle progressInnerBox;

    private final Label progressLabel;

    private final int max;
    private volatile int current;

    public ProgressUI(final int max) {
        this.max = Math.max(max, 1);
        this.current = 0;
        VSplitPane pane = new VSplitPane();
        this.panel = new ResizeablePanel(pane);
        panel.setResizeRadius(2);
        panel.setMinSize(720, 560);
        panel.setTitle("Building: <none>");
        panel.center();
        panel.show();
        panel.setTargetFps(30);
        panel.setTargetTps(30);
        panel.getEvent().register(new ResizeAdapter(this::onResize));

        panel.setBarHeight(32);
        panel.getBar().setBackground(Color.DARK_GRAY);
        panel.setBackground(Color.GRAY);
        RootBar bar = panel.getBar();
        BarBox close = bar.createBox(BoxRenderers.CROSS);
        close.setIcon(Color.GRAY, ColorParser.parse("#F26161"));
        close.setIconFade(0.3, 0.15);
        close.setBox(Color.DARK_GRAY);
        close.setAction(MouseButton.LEFT, () -> close());

        pane.setRatio(0.1f);
        pane.setSpace(4);
        pane.setBorder(2);
        this.display = new LogDisplay();
        display.setHistorySize(250);
        ScrollBar scroll = display.getScrollBar();
        scroll.setScrollMaxSpeed(5000);
        scroll.setScrollSpeed(64);
        scroll.setScrollTime(2.25d);
        pane.setBottom(display);

        this.headerPane = new BasicPane();
        pane.setTop(headerPane);

        progressOuterBox = new Rectangle();
        progressOuterBox.setColor(ColorParser.parse("707070"));
        progressOuterBox.setWidth(headerPane.getWidth());
        progressOuterBox.setHeight(headerPane.getHeight());
        progressInnerBox = new Rectangle();
        progressInnerBox.setColor(ColorParser.parse("828282"));
        progressInnerBox.setWidth(headerPane.getWidth() - 8);
        progressInnerBox.setY(4);
        progressInnerBox.setX(4);
        progressInnerBox.setHeight(headerPane.getHeight() - 8);
        progressBar = new Rectangle();
        progressBar.setColor(ColorParser.parse("#E21FB4"));
        progressBar.setY(4);
        progressBar.setX(4);
        progressBar.setHeight(headerPane.getHeight() - 8);
        progressLabel = new Label();
        progressLabel.setFontColor(Color.WHITE);
        progressLabel.setText(current + " / " + max);
        progressLabel.setY(4);
        progressLabel.setX(16);
        progressLabel.setWidth(progressInnerBox.getWidth());
        progressLabel.setHeight(progressInnerBox.getHeight());
        progressLabel.setFontSize(progressInnerBox.getHeight() - 12);
        headerPane.addChild(progressOuterBox);
        headerPane.addChild(progressInnerBox);
        headerPane.addChild(progressBar);
        headerPane.addChild(progressLabel);
        update();
        display.getScrollBar().setForeground(progressBar.getColor());
    }

    private void onResize(Panel panel) {
        progressOuterBox.setWidth(headerPane.getWidth());
        progressOuterBox.setHeight(headerPane.getHeight());
        progressInnerBox.setWidth(headerPane.getWidth() - 8);
        progressInnerBox.setHeight(headerPane.getHeight() - 8);
        progressBar.setHeight(headerPane.getHeight() - 8);
        progressLabel.setWidth(progressInnerBox.getWidth());
        progressLabel.setHeight(progressInnerBox.getHeight());
        progressLabel.setFontSize(progressInnerBox.getHeight() - 12);
        update();
    }

    private void update() {
        double value = (headerPane.getWidth() - 8) * progress();
        if (value <= 0) {
            progressBar.setHidden(true);
            return;
        }
        progressBar.setHidden(false);
        progressBar.setWidth((int) Math.round(value));
    }

    public void setBuild(String name) {
        panel.setTitle("Building: " + name);
    }

    public synchronized void increment() {
        current++;
        update();
        progressLabel.setText(current + " / " + max);
    }
    
    public LogDisplay getDisplay() {
        return display;
    }
    
    public boolean isAlive() {
        return panel.isRunning();
    }

    public double progress() {
        return current / (double) max;
    }

    public void close() {
        panel.exit();
    }

    public void await() {
        while (panel.isRunning()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
