package me.lauriichan.build.builder.ui.window.input.keyboard;

import java.awt.event.KeyEvent;

import me.lauriichan.build.builder.ui.window.input.Input;
import me.lauriichan.build.builder.ui.window.input.InputProvider;

public class KeyboardPress extends Input {

    private final int code;
    private final char character;

    public KeyboardPress(final InputProvider provider, final int code, final char character) {
        super(provider);
        this.code = code;
        this.character = character;
    }

    public int getCode() {
        return code;
    }

    public boolean hasChar() {
        return character != KeyEvent.CHAR_UNDEFINED;
    }

    public char getChar() {
        return character;
    }

}
