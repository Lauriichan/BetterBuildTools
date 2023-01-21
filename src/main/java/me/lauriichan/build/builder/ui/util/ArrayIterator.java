package me.lauriichan.build.builder.ui.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator<E> implements Iterator<E> {

    private final E[] array;
    private int index = 0;

    public ArrayIterator(final E[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index != array.length;
    }

    @Override
    public E next() {
        if (index == array.length) {
            throw new NoSuchElementException("There are no elemtns left");
        }
        return array[index++];
    }

    public void reset() {
        index = 0;
    }

}
