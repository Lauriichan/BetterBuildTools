package me.lauriichan.build.builder.ui.util;

public final class IntList {

    private final int expand = 4;

    private int[] array = new int[8];
    private int index = 0;
    
    public IntList() {}
    
    public IntList(int... startValues) {
        for(int i : startValues) {
            add(i);
        }
    }

    public synchronized void add(final int value) {
        ensureSpace(1);
        array[index++] = value;
    }

    public synchronized boolean removeValue(final int cursorId) {
        final int index = indexOf(cursorId);
        if (index == -1) {
            return false;
        }
        return removeIndex(index);
    }

    public synchronized boolean removeIndex(final int index) {
        if (this.index <= index || index < 0) {
            return false;
        }
        final int max = this.index - 1;
        for (int i = index; i < max; i++) {
            array[i] = array[i + 1];
        }
        array[max] = 0;
        return true;
    }

    public synchronized int indexOf(final int value) {
        final int length = index;
        for (int i = 0; i < length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public synchronized boolean contains(final int value) {
        return indexOf(value) != -1;
    }

    public synchronized int[] toArray() {
        final int length = index;
        final int[] output = new int[length];
        System.arraycopy(array, 0, output, 0, length);
        return output;
    }

    private void ensureSpace(final int size) {
        if (index + size < array.length) {
            return;
        }
        int diff = (index + size) - array.length;
        if (diff > expand) {
            diff += expand;
        }
        final int[] newArray = new int[array.length + diff];
        System.arraycopy(array, 0, newArray, 0, array.length);
        array = newArray;
    }

}
