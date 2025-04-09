package net.atcore.utils;

import lombok.Getter;

@Getter
public class AviaRunnable implements Runnable {

    private final Runnable task;
    private final boolean isHeavyProcess;

    public AviaRunnable(Runnable task, boolean isHeavyProcess) {
        this.task = task;
        this.isHeavyProcess = isHeavyProcess;
    }

    @Override
    public void run() {
        task.run();
    }
}
