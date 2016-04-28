package ru.lipetsk.camera.cmagent.core.task;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Ivan on 22.03.2016.
 */
public interface ITask {
    String getName();

    TaskType getType();

    long getInitialDelay();

    long getPeriod();

    Future getFuture();

    void setFuture(Future future);

    boolean isRunning();

    void setRunning(boolean running);

    void execute();

    void stop(boolean interrupt);

    void onRegister();

    void onUnregister();
}