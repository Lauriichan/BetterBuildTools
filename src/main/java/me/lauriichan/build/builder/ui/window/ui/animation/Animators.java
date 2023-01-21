package me.lauriichan.build.builder.ui.window.ui.animation;

import java.awt.Color;

public final class Animators {

    private Animators() {}

    public static final IAnimator<Integer> INTEGER = (start, end, percentage) -> start + (int) Math.floor((end - start) * percentage);
    public static final IAnimator<Double> DOUBLE = (start, end, percentage) -> start + (end - start) * percentage;

    public static final IAnimator<Color> COLOR = (start, end, percentage) -> new Color(
        INTEGER.update(start.getRed(), end.getRed(), percentage), INTEGER.update(start.getGreen(), end.getGreen(), percentage),
        INTEGER.update(start.getBlue(), end.getBlue(), percentage), INTEGER.update(start.getAlpha(), end.getAlpha(), percentage));

}
