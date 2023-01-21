package me.lauriichan.build.builder.ui;

import java.awt.Color;

import me.lauriichan.build.builder.ui.util.ColorParser;
import me.lauriichan.build.builder.ui.window.ui.BasicPane;
import me.lauriichan.build.builder.ui.window.ui.Panel;
import me.lauriichan.build.builder.ui.window.ui.component.Button;
import me.lauriichan.build.builder.ui.window.ui.component.Label;
import me.lauriichan.build.builder.ui.window.ui.component.ProgressBar;
import me.lauriichan.build.builder.ui.window.ui.component.geometry.Rectangle;

public final class Dialogs {

    public static interface IProgress {

        long total();

        long work();

        ITask begin(String task, long work);

        ITask begin(String task, long work, long progress);

    }

    public static interface ITask {

        long total();

        long work();

        void work(long work);

        void end();

    }

    @FunctionalInterface
    public static interface IDialogTask {

        void execute(IProgress progress) throws Exception;

    }

    private static final class ProgressImpl implements IProgress {

        private final Panel window = new Panel();

        private final Label titleLabel = new Label();
        private final Label taskLabel = new Label();
        private final ProgressBar progressBar = new ProgressBar();

        private final long total;
        private volatile long work;

        private TaskImpl task;

        public ProgressImpl(String taskName, long work, boolean smooth) {
            this.total = Math.max(work, 1);
            window.setBackground(Color.GRAY);
            window.setBarHeight(12);
            window.setTitle(taskName);
            window.setWidth(320);
            BasicPane pane = (BasicPane) window.getPane();
            titleLabel.setText(taskName);
            titleLabel.setTextCentered(true);
            titleLabel.setFontSize(16);
            titleLabel.setHeight(32);
            titleLabel.setWidth(window.getWidth() - 12);
            titleLabel.setX(6);
            pane.addChild(titleLabel);
            taskLabel.setText("");
            taskLabel.setWidth(window.getWidth() - 12);
            taskLabel.setHeight(32);
            taskLabel.setX(6);
            taskLabel.setY(titleLabel.getY() + titleLabel.getHeight() + 12);
            pane.addChild(taskLabel);
            progressBar.setWidth(window.getWidth() - 12);
            progressBar.setSmoothProgress(smooth);
            progressBar.setX(6);
            progressBar.setY(taskLabel.getY() + taskLabel.getHeight() + 6);
            progressBar.setHeight(48);
            progressBar.setBorder(2);
            pane.addChild(progressBar);
            window.setHeight(progressBar.getY() + progressBar.getHeight() + 6 + window.getBarHeight());
            window.show();
            window.center();
        }

        @Override
        public long total() {
            return total;
        }

        @Override
        public long work() {
            return work;
        }

        @Override
        public ITask begin(String taskName, long work) {
            return begin(taskName, work, work);
        }

        @Override
        public ITask begin(String taskName, long work, long progress) {
            if (task != null) {
                throw new IllegalStateException("Can't create a new task while old task is still active");
            }
            if (this.work + progress > total) {
                throw new IllegalArgumentException("Can't have more work than total");
            }
            TaskImpl task = new TaskImpl(this, work, progress);
            taskLabel.setText(taskName + "...");
            return task;
        }

        void add(long work) {
            if (work == 0) {
                return;
            }
            this.work += work;
            progressBar.setProgress(this.work / (double) total);
        }

        void end() {
            window.exit();
        }

    }

    private static final class TaskImpl implements ITask {

        private final ProgressImpl progress;
        private final long progressTotal;
        private final long total;

        private long progressWork;
        private long work;

        public TaskImpl(final ProgressImpl progress, final long work, final long progressWork) {
            this.progress = progress;
            this.total = work;
            this.progressTotal = progressWork;
        }

        @Override
        public long total() {
            return total;
        }

        @Override
        public long work() {
            return work;
        }

        @Override
        public void work(long work) {
            work = Math.max(work, 1);
            if (this.work + work > total) {
                throw new IllegalArgumentException("Can't have more work than total");
            }
            this.work += work;
            long calcProgress = (long) Math.floor(progressTotal * (this.work / (double) total));
            progress.add(calcProgress - progressWork);
            progressWork = calcProgress;
        }

        @Override
        public void end() {
            if (this.progressWork != progressTotal) {
                progress.add(progressTotal - progressWork);
            }
            progress.taskLabel.setText("");
            progress.task = null;
        }

    }

    public static boolean openTaskDialog(String taskName, long work, IDialogTask task) {
        return openTaskDialog(taskName, work, task, true);
    }

    public static boolean openTaskDialog(String taskName, long work, IDialogTask task, boolean smooth) {
        ProgressImpl progress = new ProgressImpl(taskName, work, smooth);
        try {
            task.execute(progress);
        } catch (Exception exception) {
            System.err.println("ERROR: " + exception.getMessage());
            progress.end();
            return false;
        }
        progress.end();
        return true;
    }

    public static void openInfoDialog(String title, String[] description) {
        openInfoDialog(title, String.join("\n", description));
    }

    public static void openInfoDialog(String title, String description) {
        Panel window = new Panel();
        window.setBackground(Color.GRAY);
        window.setBarHeight(12);
        window.setTitle(title);
        window.setWidth(320);
        BasicPane pane = (BasicPane) window.getPane();
        Label titleLabel = new Label();
        titleLabel.setText(title);
        titleLabel.setTextCentered(true);
        titleLabel.setFontSize(16);
        titleLabel.setHeight(titleLabel.getFontSize() + titleLabel.getFontSize() / 2);
        titleLabel.setWidth(window.getWidth() - 12);
        titleLabel.setX(6);
        pane.addChild(titleLabel);
        Label descriptionLabel = new Label();
        descriptionLabel.setText(description);
        descriptionLabel.setMultilineAllowed(true);
        descriptionLabel.setWidth(window.getWidth() - 16);
        descriptionLabel.setHeight(descriptionLabel.getFontSize() * 4 + descriptionLabel.getFontSize() / 2);
        descriptionLabel.setX(8);
        descriptionLabel.setY(titleLabel.getY() + titleLabel.getHeight() + 14);
        Rectangle descriptionBackground = new Rectangle();
        descriptionBackground.setSize(descriptionLabel.getWidth() + 4, descriptionLabel.getHeight() + 4);
        descriptionBackground.setPosition(descriptionLabel.getX() - 2, descriptionLabel.getY() - 2);
        descriptionBackground.setColor(ColorParser.parse("6A6A6A"));
        pane.addChild(descriptionBackground);
        pane.addChild(descriptionLabel);
        Button okButton = new Button();
        okButton.setPress(ColorParser.parse("C30184"));
        okButton.setHover(Color.DARK_GRAY, ColorParser.parse("A80172"));
        okButton.setHoverFade(0.3d, 0.4d);
        okButton.setText("Ok");
        okButton.setTextCentered(true);
        okButton.setWidth(window.getWidth() - 12);
        okButton.setHeight(okButton.getFontSize() * 2);
        okButton.setX(6);
        okButton.setY(descriptionLabel.getY() + descriptionBackground.getHeight() + 12);
        okButton.setAction(() -> window.exit());
        pane.addChild(okButton);
        window.setHeight(okButton.getY() + okButton.getHeight() + 6 + window.getBarHeight());
        window.show();
        window.center();
        window.await();
    }

}
