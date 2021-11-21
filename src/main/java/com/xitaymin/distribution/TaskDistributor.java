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

//    private final Queue<DistributionTask> tasks = new LinkedBlockingQueue<>();
//    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
//    private final Lock readLock = readWriteLock.readLock();
//    private final Lock writeLock = readWriteLock.writeLock();
//    private final TaskProvider taskProvider;

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void startDistribution(DistributionTask task) {
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

    public void stopDistribution() {
        isStopped.set(true);
    }

    public void submit(DistributionTask task) {
        startDistribution(task);
    }


}
