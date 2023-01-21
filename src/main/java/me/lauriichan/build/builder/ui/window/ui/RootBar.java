package me.lauriichan.build.builder.ui.window.ui;

import me.lauriichan.build.builder.ui.util.IBoxRenderer;
import me.lauriichan.build.builder.ui.window.ui.component.bar.BarBox;

public abstract class RootBar extends TriggerBar<BarBox> {

    public abstract BarBox createBox(IBoxRenderer renderer);

}
