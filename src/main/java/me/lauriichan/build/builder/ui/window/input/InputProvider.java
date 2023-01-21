package me.lauriichan.build.builder.ui.window.input;

import java.awt.Frame;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;

import me.lauriichan.build.builder.ui.util.Ref;
import me.lauriichan.build.builder.ui.window.input.keyboard.KeyboardListener;
import me.lauriichan.build.builder.ui.window.input.mouse.MouseListener;
import me.lauriichan.build.builder.ui.window.ui.IBaseComponent;
import me.lauriichan.build.builder.ui.window.ui.Panel;

public final class InputProvider {

    private final Ref<InputEvent> last = Ref.of();

    private final HashMap<Class<?>, ArrayList<InputReceiver<?>>> listeners = new HashMap<>();
    private final Panel panel;

    public InputProvider(final Panel panel) {
        this.panel = panel;
        final Frame frame = panel.getFrame();
        final MouseListener listener = new MouseListener(this);
        frame.addMouseListener(listener);
        frame.addMouseMotionListener(listener);
        frame.addMouseWheelListener(listener);
        frame.addKeyListener(new KeyboardListener(this));
    }

    public Panel getPanel() {
        return panel;
    }

    public void register(final IBaseComponent component) {
        final InputReceiver<?>[] receivers = InputReceiver.find(component);
        for (final InputReceiver<?> receiver : receivers) {
            listeners.computeIfAbsent(receiver.getType(), clz -> new ArrayList<>()).add(receiver);
        }
    }

    public void unregister(final IBaseComponent component) {
        for (final Class<?> clazz : listeners.keySet()) {
            final ArrayList<InputReceiver<?>> list = listeners.get(clazz);
            for (int index = 0; index < list.size(); index++) {
                final InputReceiver<?> receiver = list.get(index);
                if (receiver.getInstance() != component) {
                    continue;
                }
                list.remove(index--);
            }
        }
    }

    public boolean isAltDown() {
        return last.isPresent() ? last.get().isAltDown() : false;
    }

    public boolean isShiftDown() {
        return last.isPresent() ? last.get().isShiftDown() : false;
    }

    public boolean isControlDown() {
        return last.isPresent() ? last.get().isControlDown() : false;
    }

    public InputEvent getLast() {
        return last.get();
    }

    public boolean hasLast() {
        return last.isPresent();
    }

    public void receive(final Input input, final InputEvent event) {
        if (event != null) {
            last.set(event);
        }
        final ArrayList<InputReceiver<?>> list = listeners.get(input.getClass());
        if (list == null || list.isEmpty()) {
            return;
        }
        final InputReceiver<?>[] receivers = list.toArray(new InputReceiver<?>[list.size()]);
        for (int i = receivers.length - 1; i >= 0; i--) {
            if (!receivers[i].canReceive()) {
                continue;
            }
            receivers[i].accept(input);
        }
    }

}
