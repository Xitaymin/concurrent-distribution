package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskDistributor {
    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final List<Future<?>> futures = new CopyOnWriteArrayList<>();

    public TaskDistributor() {
        if (executorService instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) executorService).setRemoveOnCancelPolicy(true);
            System.out.println("Everything should be fine");
        }
    }

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void submit(DistributionTask task) {
        Future<?> future = task.sentMessage(executorService, subscribers);
        futures.add(future);

        executorService.execute(() -> {
            while (!isStopped.get()) {
            }
            for (Future<?> f : futures) {
                f.cancel(true);
            }
        });
    }

    public void stop() {
        isStopped.set(true);
    }

    public void exit() {
        stop();
        executorService.shutdown();
    }

}
