package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class PeriodicalDistributionTask implements DistributionTask {
    private final Supplier<String> messageSupplier;
    private final long period;

    public PeriodicalDistributionTask(Supplier<String> messageSupplier, long period) {
        this.messageSupplier = messageSupplier;
        this.period = period;
    }

    @Override
    public Future<?> sentMessage(ScheduledExecutorService executorService, List<Subscriber> subscribers) {
        return executorService.scheduleAtFixedRate(() -> {
            for (Subscriber subscriber : subscribers) {
                subscriber.receiveMessage(messageSupplier.get());
            }
        }, 0, period, TimeUnit.SECONDS);
    }
}
