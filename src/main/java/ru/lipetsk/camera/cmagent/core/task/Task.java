package ru.lipetsk.camera.cmagent.core.task;

import ru.lipetsk.camera.cmagent.core.facade.FacadeHolder;

import java.util.concurrent.*;

/**
 * Created by Ivan on 22.03.2016.
 */
public abstract class Task extends FacadeHolder implements ITask, Runnable {
    protected final String name;

    protected final TaskType type;

    protected final long initialDelay;

    protected final long period;

    protected volatile Future future;

    protected volatile boolean running;

    public Task(String name, TaskType type, long initialDelay, long period) {
        this.name = name;

        this.type = type;

        this.initialDelay = initialDelay;

        this.period = period;

        this.future = null;

        this.running = false;
    }

    public Task(String name, TaskType type, long period) {
        this(name, type, 0, period);
    }

    public Task(String name) {
        this(name, TaskType.RUN_ONCE, 0, 0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TaskType getType() {
        return this.type;
    }

    @Override
    public long getInitialDelay() {
        return this.initialDelay;
    }

    @Override
    public long getPeriod() {
        return this.period;
    }

    @Override
    public Future getFuture() {
        return this.future;
    }

    @Override
    public void setFuture(Future future) {
        this.future = future;
    }

    @Override
    public boolean isRunning() { return this.running; }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

    public void stop(boolean interrupt) {
        this.future.cancel(interrupt);

        this.running = false;

        this.future = null;
    }

    @Override
    public void run() {
        if (this.running) {
            this.execute();
        }
    }
}