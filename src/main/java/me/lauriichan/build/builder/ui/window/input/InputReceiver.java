package me.lauriichan.build.builder.ui.window.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.ui.IBaseComponent;

final class InputReceiver<I extends Input> {

    private static final InputReceiver<?>[] EMPTY = {};

    private final Class<I> type;
    private final IBaseComponent instance;
    private final Method method;

    private InputReceiver(final Class<I> type, final IBaseComponent instance, final Method method) {
        this.type = type;
        this.method = method;
        this.instance = instance;
    }

    public Class<I> getType() {
        return type;
    }

    public IBaseComponent getInstance() {
        return instance;
    }

    public boolean canReceive() {
        return !instance.isHidden();
    }

    public Method getMethod() {
        return method;
    }

    @SuppressWarnings("deprecation")
    public void accept(final Input input) {
        if (!type.isAssignableFrom(input.getClass())) {
            return;
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
            try {
                method.invoke(instance, input);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
                // Ignore
            } finally {
                method.setAccessible(false);
            }
            return;
        }
        try {
            method.invoke(instance, input);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
            // Ignore
        }
    }

    public static InputReceiver<?>[] find(final IBaseComponent instance) {
        if (instance == null) {
            return EMPTY;
        }
        final Class<?> clazz = instance.getClass();
        final ArrayList<InputReceiver<?>> receivers = new ArrayList<>();
        final ArrayList<Method> visited = new ArrayList<>();
        find(receivers, visited, instance, clazz.getMethods());
        find(receivers, visited, instance, clazz.getDeclaredMethods());
        visited.clear();
        return receivers.toArray(new InputReceiver<?>[receivers.size()]);
    }

    private static void find(final ArrayList<InputReceiver<?>> receivers, final ArrayList<Method> visited, final IBaseComponent instance,
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
            if (Modifier.isAbstract(type.getModifiers()) || !Input.class.isAssignableFrom(type)) {
                continue;
            }
            receivers.add(new InputReceiver<>(type.asSubclass(Input.class), instance, method));
        }
    }

}
