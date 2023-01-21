package me.lauriichan.build.builder.ui.util.render;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;

import me.lauriichan.build.builder.ui.util.IntList;

public final class TextMetrics {

    private static final String[] EMPTY_LINES = new String[0];
    private static final int[] EMPTY_INDICES = new int[0];

    private final FontMetrics metrics;

    public TextMetrics(final FontMetrics metrics) {
        this.metrics = metrics;
    }

    public FontMetrics getMetrics() {
        return metrics;
    }

    public Font getFont() {
        return metrics.getFont();
    }

    public TextRender calculate(String string, int maxWidth) {
        if (string == null || string.isEmpty()) {
            return new TextRender(EMPTY_LINES, EMPTY_INDICES, this);
        }
        ArrayList<String> list = new ArrayList<>();
        IntList indices = new IntList();
        String[] lines = string.split("\n");
        int lineIndex = 0;
        for (int index = 0; index < lines.length; index++) {
            final String untrimmed = lines[index];
            String line = untrimmed.trim();
            if (metrics.stringWidth(line) < maxWidth) {
                list.add(line);
                indices.add(lineIndex);
                lineIndex += untrimmed.length() + (index + 1 == lines.length ? 0 : 1);
                continue;
            }
            wrapIntoSize(list, indices, lineIndex, line, maxWidth);
            lineIndex += untrimmed.length() + (index + 1 == lines.length ? 0 : 1);
        }
        return new TextRender(list.toArray(String[]::new), indices.toArray(), this);
    }

    public int widthOf(String string) {
        return metrics.stringWidth(string);
    }

    public int widthOf(char character) {
        return metrics.charWidth(character);
    }

    private void wrapIntoSize(ArrayList<String> list, IntList indices, int baseIndex, String string, int maxWidth) {
        String[] lines = splitIntoWordLines(string, maxWidth);
        int lineIndex = baseIndex;
        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (metrics.stringWidth(line) < maxWidth) {
                list.add(line);
                indices.add(lineIndex);
                lineIndex += line.length() + 1;
                continue;
            }
            int lineLength = lineIndex + line.length();
            String lastValid = "";
            int lastIndex = 0;
            int split = lineIndex;
            for (int i = lineIndex; i < lineLength; i++) {
                String valid = string.substring(split, i);
                if (metrics.stringWidth(valid) < maxWidth) {
                    lastValid = valid;
                    lastIndex = i;
                    continue;
                }
                list.add(lastValid);
                indices.add(lineIndex);
                lineIndex += lastValid.length();
                lastValid = "";
                split = lastIndex;
                lastIndex = 0;
            }
            if (!lastValid.isEmpty() && lastIndex != 0) {
                list.add(lastValid);
                indices.add(lineIndex);
                lineIndex += lastValid.length();
            }
            lineIndex++;
        }
    }

    private String[] splitIntoWordLines(String string, int maxWidth) {
        if (string.isEmpty()) {
            return EMPTY_LINES;
        }
        ArrayList<String> lines = new ArrayList<>();
        String lastValid = "";
        int lastIndex = 0;
        int split = 0;
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char character = string.charAt(i);
            if (!Character.isSpaceChar(character)) {
                if (i + 1 != length) {
                    continue;
                }
                i++;
            }
            String valid = string.substring(split, i);
            if (metrics.stringWidth(valid) < maxWidth) {
                lastValid = valid;
                lastIndex = i;
                continue;
            }
            if (lastValid.isEmpty()) {
                break;
            }
            lines.add(lastValid);
            lastValid = "";
            split = lastIndex + 1;
            lastIndex = 0;
        }
        if (!lastValid.isEmpty()) {
            lines.add(lastValid);
            split = lastIndex + 1;
        }
        if (split < length - 1) {
            lines.add(string.substring(split));
        }
        return lines.toArray(String[]::new);
    }

    public int getHeight() {
        return metrics.getHeight();
    }

    public int getDrawHeight() {
        return metrics.getAscent() + metrics.getLeading();
    }

}
