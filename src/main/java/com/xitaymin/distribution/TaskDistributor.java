package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TaskDistributor {
    private final List<Suscriber> subscribers = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public void subscribe(Suscriber suscriber) {
        subscribers.add(suscriber);
    }

    public void unsubscribe(Suscriber suscriber) {
        subscribers.remove(suscriber);
    }


}
