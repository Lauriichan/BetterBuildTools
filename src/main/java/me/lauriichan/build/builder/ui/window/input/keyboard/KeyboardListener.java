package me.lauriichan.build.builder.ui.window.input.keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import me.lauriichan.build.builder.ui.window.input.InputProvider;

public final class KeyboardListener extends KeyAdapter {

    private final InputProvider provider;

    public KeyboardListener(final InputProvider provider) {
        this.provider = provider;
    }

    public InputProvider getProvider() {
        return provider;
    }

    @Override
    public void keyPressed(final KeyEvent event) {
        event.consume();
        provider.receive(new KeyboardPress(provider, event.getKeyCode(), event.getKeyChar()), event);
    }

}
