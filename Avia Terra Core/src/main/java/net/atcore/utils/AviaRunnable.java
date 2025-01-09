package net.atcore.utils;

import lombok.Getter;

@Getter
public class AviaRunnable implements Runnable {

    private final StackTraceElement[] stackTraceElements;
    private final Runnable task;
    private final boolean isHeavyProcess;

    public AviaRunnable(Runnable task, boolean isHeavyProcess) {
        this.task = task;
        this.stackTraceElements = Thread.currentThread().getStackTrace();
        this.isHeavyProcess = isHeavyProcess;
    }

    @Override
    public void run() {
        task.run();
    }
}
