package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskDistributor {
    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicBoolean isDistributionStopped = new AtomicBoolean(false);
    private final List<Future<?>> futures = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isProgramStopped = new AtomicBoolean(false);

    public TaskDistributor() {
        if (executorService instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) executorService).setRemoveOnCancelPolicy(true);
        }
        executorService.execute(() -> {
            while (!isProgramStopped.get()) {
                if (isDistributionStopped.get()) {
                    for (Future<?> f : futures) {
                        f.cancel(true);
                        futures.remove(f);
                    }
                }
            }
        });
    }


    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void startDistribution(DistributionTask task) {
        List<Subscriber> currentSubscriber = new CopyOnWriteArrayList<>(subscribers);
        isDistributionStopped.set(false);
        Future<?> future = task.sentMessage(executorService, currentSubscriber);
        futures.add(future);
    }

    public void stopDistribution() {
        isDistributionStopped.set(true);
    }

    public void exit() {
        stopDistribution();
        isProgramStopped.set(true);
        executorService.shutdown();
    }

}
