package me.lauriichan.build.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamReader extends Thread {

    private static volatile int ID = 0;

    private boolean running = true;

    private final Consumer<String> printer;
    private volatile BufferedReader reader;

    public StreamReader(final Consumer<String> printer) {
        this.printer = printer;
        setDaemon(true);
        setName("Stream Reader - " + (ID++));
    }

    public void close() {
        running = false;
    }

    public void await() {
        while (this.reader != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public void setStream(InputStream stream) {
        await();
        this.reader = new BufferedReader(new InputStreamReader(stream));
    }

    @Override
    public void run() {
        while (running) {
            BufferedReader reader = this.reader;
            if (reader == null) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                }
                continue;
            }
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    printer.accept(line);
                }
            } catch (IOException e) {
            }
            try {
                reader.close();
            } catch (IOException e) {
            }
            this.reader = null;
        }
    }

}
