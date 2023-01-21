package me.lauriichan.build.builder.ui.window.ui.animation;

public interface IAnimation<E> {

    IAnimator<E> getAnimator();

    void tick(long deltaTime);

    E getValue();

    void setStart(E value);

    void setEnd(E value);

    void setTriggered(boolean triggered);

    boolean isTriggered();

}
