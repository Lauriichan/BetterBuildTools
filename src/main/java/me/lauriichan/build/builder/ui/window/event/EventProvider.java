package me.lauriichan.build.builder.ui.window.event;

import java.util.ArrayList;
import java.util.HashMap;

import me.lauriichan.build.builder.ui.window.ui.IBaseComponent;
import me.lauriichan.build.builder.ui.window.ui.Panel;

public final class EventProvider {

    private final HashMap<Class<?>, ArrayList<EventReceiver<?>>> listeners = new HashMap<>();
    private final Panel panel;

    public EventProvider(final Panel panel) {
        this.panel = panel;
    }

    public Panel getPanel() {
        return panel;
    }

    public void register(final IBaseComponent component) {
        final EventReceiver<?>[] receivers = EventReceiver.find(component);
        for (final EventReceiver<?> receiver : receivers) {
            listeners.computeIfAbsent(receiver.getType(), clz -> new ArrayList<>()).add(receiver);
        }
    }

    public void unregister(final IBaseComponent component) {
        for (final Class<?> clazz : listeners.keySet()) {
            final ArrayList<EventReceiver<?>> list = listeners.get(clazz);
            for (int index = 0; index < list.size(); index++) {
                final EventReceiver<?> receiver = list.get(index);
                if (receiver.getInstance() != component) {
                    continue;
                }
                list.remove(index--);
            }
        }
    }

    public void call(final IEvent event) {
        final ArrayList<EventReceiver<?>> list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) {
            return;
        }
        final EventReceiver<?>[] receivers = list.toArray(new EventReceiver<?>[list.size()]);
        for (int i = receivers.length - 1; i >= 0; i--) {
            if (!receivers[i].canReceive()) {
                continue;
            }
            receivers[i].accept(event);
        }
    }

}
