package com.xitaymin.distribution;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DeferredDistributionTask implements DistributionTask {
    private final Supplier<String> messageSupplier;
    private final LocalDateTime sendingTime;

    public DeferredDistributionTask(Supplier<String> messageSupplier, LocalDateTime sendingTime) {
        this.messageSupplier = messageSupplier;
        this.sendingTime = sendingTime;
    }

    @Override
    public Future<?> sentMessage(ScheduledExecutorService executorService, List<Subscriber> subscribers) {
        return executorService.schedule(() -> {
            for (Subscriber subscriber : subscribers) {
                subscriber.receiveMessage(messageSupplier.get());
            }
        }, LocalDateTime.now().until(sendingTime, ChronoUnit.SECONDS), TimeUnit.SECONDS);
    }

}
