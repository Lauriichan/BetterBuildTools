package me.lauriichan.build.builder.ui.util;

public enum Rotation {

    NORTH,
    EAST,
    SOUTH,
    WEST;

    public Rotation left() {
        return Rotation.values()[(ordinal() + 3) % 4];
    }

    public Rotation right() {
        return Rotation.values()[(ordinal() + 1) % 4];
    }

}
