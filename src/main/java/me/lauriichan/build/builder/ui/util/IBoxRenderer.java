package me.lauriichan.build.builder.ui.util;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.render.Area;

@FunctionalInterface
public interface IBoxRenderer {

    void render(Area area, Color color, int offset, int size, int thickness);

}