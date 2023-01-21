package me.lauriichan.build.builder.ui.window.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.ui.IBaseComponent;

final class EventReceiver<E extends IEvent> {

    private static final EventReceiver<?>[] EMPTY = {};

    private final Class<E> type;
    private final IBaseComponent instance;
    private final Method method;

    private EventReceiver(final Class<E> type, final IBaseComponent instance, final Method method) {
        this.type = type;
        this.method = method;
        this.instance = instance;
    }

    public Class<E> getType() {
        return type;
    }

    public IBaseComponent getInstance() {
        return instance;
    }

    public boolean canReceive() {
        return instance.isUpdating();
    }

    public Method getMethod() {
        return method;
    }

    @SuppressWarnings("deprecation")
    public void accept(final IEvent event) {
        if (!type.isAssignableFrom(event.getClass())) {
            return;
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
            try {
                method.invoke(instance, event);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
                // Ignore
            } finally {
                method.setAccessible(false);
            }
            return;
        }
        try {
            method.invoke(instance, event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
            // Ignore
        }
    }

    public static EventReceiver<?>[] find(final IBaseComponent instance) {
        if (instance == null) {
            return EMPTY;
        }
        final Class<?> clazz = instance.getClass();
        final ArrayList<EventReceiver<?>> receivers = new ArrayList<>();
        final ArrayList<Method> visited = new ArrayList<>();
        find(receivers, visited, instance, clazz.getMethods());
        find(receivers, visited, instance, clazz.getDeclaredMethods());
        visited.clear();
        return receivers.toArray(new EventReceiver<?>[receivers.size()]);
    }

    private static void find(final ArrayList<EventReceiver<?>> receivers, final ArrayList<Method> visited, final IBaseComponent instance,
        final Method[] methods) {
        for (final Method method : methods) {
            if (visited.contains(method)) {
                continue;
            }
            visited.add(method);
            if (method.getAnnotation(Listener.class) == null || Modifier.isStatic(method.getModifiers())
                || method.getParameterCount() != 1) {
                continue;
            }
            final Class<?> type = method.getParameterTypes()[0];
            if (Modifier.isAbstract(type.getModifiers()) || !IEvent.class.isAssignableFrom(type)) {
                continue;
            }
            receivers.add(new EventReceiver<>(type.asSubclass(IEvent.class), instance, method));
        }
    }

}
