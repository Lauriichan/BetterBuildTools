package me.lauriichan.build.builder.ui.util.tick;

@FunctionalInterface
public interface ITickReceiver {

    void onTick(long deltaTime);

}
