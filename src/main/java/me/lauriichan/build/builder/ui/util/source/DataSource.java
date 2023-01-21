package me.lauriichan.build.builder.ui.util.source;

import java.io.IOException;
import java.io.InputStream;

public abstract class DataSource {

    public abstract Object getSource();

    public abstract InputStream openStream() throws IOException;

}