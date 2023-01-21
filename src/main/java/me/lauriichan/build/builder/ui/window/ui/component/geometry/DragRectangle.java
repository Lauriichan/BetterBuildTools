package me.lauriichan.build.builder.ui.window.ui.component.geometry;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseDrag;
import me.lauriichan.build.builder.ui.window.ui.Panel;

public final class DragRectangle extends Geometry {

    private Color color = Color.BLACK;

    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    @Override
    public void render(final Area area) {
        area.fill(color);
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
