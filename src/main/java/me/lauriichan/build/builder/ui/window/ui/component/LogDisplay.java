package me.lauriichan.build.builder.ui.window.ui.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import me.lauriichan.build.builder.ui.util.LogEntry;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.ui.Component;

public class LogDisplay extends Component {

    private final List<LogEntry> history = Collections.synchronizedList(new ArrayList<>());
    private final ScrollBar scrollBar = new ScrollBar(this);

    private String fontName = "Lucida Console";
    private int fontSize = 12;
    private int fontStyle = 0;

    private Color infoColor = Color.WHITE;
    private Color warnColor = Color.YELLOW;
    private Color errorColor = Color.RED;
    private Color commandColor = Color.GREEN;

    private int historySize = 200;

    private int lineThickness = 0;
    private Color background = Color.BLACK;
    private Color line = Color.WHITE;
    
    private Consumer<LogEntry> listener;

    @Override
    public void render(Area area) {
        area.fillOutline(background, lineThickness, line);
        LogEntry[] entries = getHistory();
        if (entries.length == 0) {
            if (!scrollBar.isUpdating()) {
                return;
            }
            scrollBar.setUpdating(false);;
            return;
        }
        Area historyArea = area.create(6, 6, area.getWidth() - scrollBar.getWidth() * 2 - 6, area.getHeight() - 12);
        int total = 0;
        for (int index = 0; index < entries.length; index++) {
            total += entries[index].calculate(historyArea).getHeight();
        }
        boolean tmp;
        int height = historyArea.getHeight();
        int start = 0;
        int end = total;
        if (tmp = total > height) {
            scrollBar.setMaxHeight(total);
            start = (int) Math.round(scrollBar.getProgress() * (total - historyArea.getHeight()));
            end = start + height;
        }
        int offset = 0;
        for (int index = 0; index < entries.length; index++) {
            LogEntry entry = entries[index];
            int entryHeight = entry.getHeight();
            offset += entryHeight;
            if ((offset > start || offset - entryHeight > start) && offset - entryHeight < end) {
                entry.render(historyArea, 0, height + (start - offset));
            }
        }
        scrollBar.render(area);
        if (tmp != scrollBar.isUpdating()) {
            scrollBar.setUpdating(tmp);
        }
    }
    
    /*
     * Scrollbar handling
     */
    
    @Override
    public void update(long deltaTime) {
        scrollBar.update(deltaTime);
    }
    
    @Override
    protected void onAdd() {
        scrollBar.setup(true);
    }
    
    @Override
    protected void onRemove() {
        scrollBar.setup(false);
    }
    
    @Override
    protected void onHeightChange(int oldHeight, int newHeight) {
        scrollBar.updateDimensions();
    }
    
    @Override
    protected void onWidthChange(int oldWidth, int newWidth) {
        scrollBar.updateDimensions();
    }
    
    /*
     * Logging
     */

    public void info(String line) {
        log(new LogEntry(infoColor, line, true));
    }

    public void warn(String line) {
        log(new LogEntry(warnColor, line, true));
    }

    public void error(String line) {
        log(new LogEntry(errorColor, line, true));
    }

    public void command(String line) {
        log(new LogEntry(commandColor, "> " + line, false));
    }

    public void append(Color color, String line) {
        log(new LogEntry(color, line, false));
    }

    public void log(Color color, String line) {
        log(new LogEntry(color, line, true));
    }

    public void log(LogEntry entry) {
        entry.setFont(fontName, fontSize, fontStyle);
        if (history.size() + 1 > historySize) {
            history.remove(historySize - 1);
        }
        history.add(0, entry);
        if (listener != null) {
            listener.accept(entry);
        }
    }

    private void updateFonts() {
        LogEntry[] history = getHistory();
        for (LogEntry entry : history) {
            entry.setFont(fontName, fontSize, fontStyle);
        }
    }

    public LogEntry remove(int index) {
        return outside(index) ? null : history.remove(index);
    }

    public void clear() {
        history.clear();
    }

    public LogEntry[] getHistory() {
        return history.toArray(new LogEntry[history.size()]);
    }

    private boolean outside(int index) {
        return index < 0 || index > history.size();
    }
    
    /*
     * Setter & Getter 
     */
    
    public ScrollBar getScrollBar() {
        return scrollBar;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setListener(Consumer<LogEntry> listener) {
        this.listener = listener;
    }

    public Consumer<LogEntry> getListener() {
        return listener;
    }

    public void setFontName(String fontName) {
        if (this.fontName.equals(fontName) || fontName == null) {
            return;
        }
        this.fontName = fontName;
        updateFonts();
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontSize(int fontSize) {
        if (this.fontSize == fontSize) {
            return;
        }
        this.fontSize = fontSize;
        updateFonts();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontStyle(int fontStyle) {
        if (this.fontStyle == fontStyle) {
            return;
        }
        this.fontStyle = fontStyle;
        updateFonts();
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setInfoColor(Color infoColor) {
        this.infoColor = infoColor;
    }

    public Color getInfoColor() {
        return infoColor;
    }

    public void setErrorColor(Color errorColor) {
        this.errorColor = errorColor;
    }

    public Color getErrorColor() {
        return errorColor;
    }

    public void setCommandColor(Color commandColor) {
        this.commandColor = commandColor;
    }

    public Color getCommandColor() {
        return commandColor;
    }

    public void setWarnColor(Color warnColor) {
        this.warnColor = warnColor;
    }

    public Color getWarnColor() {
        return warnColor;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLine(Color line) {
        this.line = line;
    }

    public Color getLine() {
        return line;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getBackground() {
        return background;
    }

}
