package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class InstantDistributionTask implements DistributionTask {
    private final String message;

    public InstantDistributionTask(String message) {
        this.message = message;
    }

    @Override
    public Future<?> sentMessage(ScheduledExecutorService executorService, List<Subscriber> subscribers) {
        return executorService.submit(() -> {
            for (Subscriber subscriber : subscribers) {
                subscriber.receiveMessage(message);
            }
        });
    }
}
