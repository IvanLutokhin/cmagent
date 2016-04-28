package ru.lipetsk.camera.cmagent.core.task;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Ivan on 22.03.2016.
 */
public class TaskManager {
    private static TaskManager instance = null;

    public synchronized static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }

        return instance;
    }

    private Map<String, ITask> taskMap;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    private TaskManager() {
        this.taskMap = new ConcurrentHashMap<>();
    }

    public void register(ITask task) {
        this.taskMap.put(task.getName(), task);

        task.onRegister();
    }

    public ITask retrieve(String taskName) {
        return this.taskMap.get(taskName);
    }

    public boolean hasTask(String taskName) {
        return this.taskMap.containsKey(taskName);
    }

    public ITask unregister(String taskName) {
        ITask task = this.retrieve(taskName);

        if (task != null) {
            task.stop(true);

            this.taskMap.remove(taskName);

            task.onUnregister();
        }

        return task;
    }

    public void execute(ITask task, boolean autoRegistration) {
        if (autoRegistration && !this.hasTask(task.getName())) {
            this.register(task);
        }

        if (!task.isRunning()) {
            task.setRunning(true);

            Future future;

            switch (task.getType()) {
                case RUN_ONCE:
                    future = this.executorService.submit((Runnable) task);

                    break;
                case SCHEDULED_AT_FIXED_RATE:
                    future = this.scheduledExecutorService.scheduleAtFixedRate((Runnable) task, task.getInitialDelay(), task.getPeriod(), TimeUnit.SECONDS);

                    break;
                case SCHEDULED_WITH_FIXED_RATE:
                    future = this.scheduledExecutorService.scheduleAtFixedRate((Runnable) task, task.getInitialDelay(), task.getPeriod(), TimeUnit.SECONDS);

                    break;
                default: return;
            }

            task.setFuture(future);
        }
    }

    public void execute(ITask task) {
        this.execute(task, true);
    }

    public void shutdown() {
        this.executorService.shutdownNow();

        this.scheduledExecutorService.shutdownNow();

        this.taskMap.clear();
    }
}