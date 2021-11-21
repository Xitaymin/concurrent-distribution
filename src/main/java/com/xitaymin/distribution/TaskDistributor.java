package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskDistributor {
    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final List<Future<?>> unDoneFutures = new CopyOnWriteArrayList<>();

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void submit(DistributionTask task) {
        Future<?> future = task.sentMessage(executorService, subscribers);
        if (!future.isDone()) {
            unDoneFutures.add(future);
        }
        executorService.execute(() -> {
            while (!isStopped.get() || (!unDoneFutures.isEmpty())) {
                unDoneFutures.removeIf(f -> future.isDone());
            }
            for (Future<?> f : unDoneFutures) {
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
