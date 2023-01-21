package me.lauriichan.build.builder.ui.util;

public final class BoxRenderers {

    private BoxRenderers() {}

    public static final IBoxRenderer CROSS = (area, color, offset, size, thickness) -> {
        area.drawLine(offset, offset, size + offset, size + offset, thickness, color);
        area.drawLine(offset, size + offset, size + offset, offset, thickness, color);
    };

    public static final IBoxRenderer UNDERSCORE = (area, color, offset, size, thickness) -> {
        area.drawLine(offset, size + offset, size + offset, size + offset, thickness, color);
    };

    public static final IBoxRenderer CHECKMARK = (area, color, offset, size, thickness) -> {
        size -= 2;
        offset += 1;
        area.drawLine(size / 2 + offset, size + offset, size + offset, offset, thickness, color);
        area.drawLine(offset, size / 2 + offset, size / 2 + offset, size + offset, thickness, color);
    };

    public static final IBoxRenderer WINDOW = (area, color, offset, size, thickness) -> {
        area.drawRectangle(offset, offset, size + offset, size + offset, color);
        area.drawRectangle(offset + thickness, offset + thickness, size + offset - thickness * 2, size + offset - thickness * 2);
    };

}
