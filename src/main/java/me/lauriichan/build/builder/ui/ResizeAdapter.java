package me.lauriichan.build.builder.ui;

import java.util.Objects;
import java.util.function.Consumer;

import me.lauriichan.build.builder.ui.window.Listener;
import me.lauriichan.build.builder.ui.window.ui.IBaseComponent;
import me.lauriichan.build.builder.ui.window.ui.Panel;

public class ResizeAdapter implements IBaseComponent {
    
    public final Consumer<Panel> onResize;
    
    public ResizeAdapter(final Consumer<Panel> onResize) {
        this.onResize = Objects.requireNonNull(onResize);
    }

    @Override
    public boolean isUpdating() {
        return true;
    }

    @Override
    public void setUpdating(boolean update) {}

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean hidden) {}
    
    @Listener
    public void onResize(Panel.ResizeEvent event) {
        onResize.accept(event.getPanel());
    }

}
