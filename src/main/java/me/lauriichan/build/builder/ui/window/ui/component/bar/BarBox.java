package me.lauriichan.build.builder.ui.window.ui.component.bar;

import java.awt.Color;
import java.util.EnumMap;
import java.util.function.BiConsumer;

import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseButton;
import me.lauriichan.build.builder.ui.window.ui.ITriggerComponent;
import me.lauriichan.build.builder.ui.window.ui.animation.Animators;
import me.lauriichan.build.builder.ui.window.ui.animation.FadeAnimation;

public final class BarBox implements ITriggerComponent {

    private final FadeAnimation<Color> iconColor = new FadeAnimation<>(Animators.COLOR);
    private final FadeAnimation<Color> boxColor = new FadeAnimation<>(Animators.COLOR);

    private final BiConsumer<Area, Color> iconDrawer;
    private final EnumMap<MouseButton, Runnable> actions = new EnumMap<>(MouseButton.class);

    private boolean hidden = false;
    private boolean update = true;

    public BarBox(final BiConsumer<Area, Color> iconDrawer) {
        this.iconDrawer = iconDrawer;
    }

    @Override
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isUpdating() {
        return update;
    }

    @Override
    public void setUpdating(final boolean update) {
        this.update = update;
    }

    @Override
    public void render(final Area area) {
        area.fill(boxColor.getValue());
        iconDrawer.accept(area, iconColor.getValue());
    }

    @Override
    public void update(final long deltaTime) {
        iconColor.tick(deltaTime);
        boxColor.tick(deltaTime);
    }

    public void click(final MouseButton button) {
        final Runnable action = getAction(button);
        if (action == null) {
            return;
        }
        action.run();
    }

    public Runnable getAction(final MouseButton button) {
        return actions.get(button);
    }

    public void setAction(final MouseButton button, final Runnable action) {
        if (action == null) {
            actions.remove(button);
            return;
        }
        actions.put(button, action);
    }

    public void setIcon(final Color color) {
        setIcon(color, color);
    }

    public void setIcon(final Color start, final Color end) {
        setIconStart(start);
        setIconEnd(end);
    }

    public void setIconStart(final Color color) {
        iconColor.setStart(color);
    }

    public void setIconEnd(final Color color) {
        iconColor.setEnd(color);
    }

    public void setIconFade(final double fadeIn, final double fadeOut) {
        iconColor.setFade(fadeIn, fadeOut);
    }

    public void setBox(final Color color) {
        setBox(color, color);
    }

    public void setBox(final Color start, final Color end) {
        setBoxStart(start);
        setBoxEnd(end);
    }

    public void setBoxStart(final Color color) {
        boxColor.setStart(color);
    }

    public void setBoxEnd(final Color color) {
        boxColor.setEnd(color);
    }

    public void setBoxFade(final double fadeIn, final double fadeOut) {
        boxColor.setFade(fadeIn, fadeOut);
    }

    @Override
    public void setTriggered(final boolean triggered) {
        iconColor.setTriggered(triggered);
        boxColor.setTriggered(triggered);
    }

    @Override
    public boolean isTriggered() {
        return iconColor.isTriggered();
    }

}
