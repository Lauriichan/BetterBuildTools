package me.lauriichan.build.builder.ui.util.render;

public final class TextRender {

    private final String[] lines;
    private final int[] indices;
    private final TextMetrics metrics;

    public TextRender(final String[] lines, final int[] indices, final TextMetrics metrics) {
        this.lines = lines;
        this.metrics = metrics;
        this.indices = indices;
    }

    public TextMetrics getMetrics() {
        return metrics;
    }

    public int getHeight() {
        return metrics.getHeight();
    }

    public int getLines() {
        return lines.length;
    }

    public int getLineId(final int index) {
        if (index <= 0) {
            return 0;
        }
        for (int idx = 0; idx < indices.length; idx++) {
            if (indices[idx] < index) {
                continue;
            }
            return idx;
        }
        return indices.length - 1;
    }

    public int getLineIndex(final int line) {
        return line >= indices.length || line < 0 ? -1 : indices[line];
    }

    public String getLine(final int line) {
        return line >= lines.length || line < 0 ? null : lines[line];
    }

}
