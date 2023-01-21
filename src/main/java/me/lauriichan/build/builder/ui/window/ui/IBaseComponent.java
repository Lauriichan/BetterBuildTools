package me.lauriichan.build.builder.ui.window.ui;

import me.lauriichan.build.builder.ui.util.render.Area;

public interface IBaseComponent {

    boolean isUpdating();

    void setUpdating(boolean update);

    boolean isHidden();

    void setHidden(boolean hidden);

    default void render(Area area) {};

    default void update(long deltaTime) {};

    default void exit() {}

}
