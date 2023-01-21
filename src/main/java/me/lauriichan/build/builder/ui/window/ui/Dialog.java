package me.lauriichan.build.builder.ui.window.ui;

import java.util.function.BiConsumer;

public final class Dialog {

    private final Panel panel = new Panel();

    private BiConsumer<Boolean, Object> action;

    public Dialog(final BiConsumer<Dialog, Panel> builder) {
        builder.accept(this, panel);
    }

    public Panel getPanel() {
        return panel;
    }

    public void fail(final Object object) {
        if (panel.isHidden()) {
            return;
        }
        action.accept(false, object);
        hide();
    }

    public void success(final Object object) {
        if (panel.isHidden()) {
            return;
        }
        action.accept(true, object);
        hide();
    }

    private void hide() {
        panel.hide();
        panel.setUpdating(false);
        panel.setTargetFps(1);
        panel.setTargetTps(1);
        action = null;
    }

    public void show(final BiConsumer<Boolean, Object> action) {
        if (!panel.isHidden()) {
            return;
        }
        if (action == null) {
            throw new IllegalStateException("Action can't be null!");
        }
        this.action = action;
        panel.setTargetFps(20);
        panel.setTargetTps(20);
        panel.setUpdating(true);
        panel.center();
        panel.show();
    }

    public void exit() {
        panel.exit();
    }

}
