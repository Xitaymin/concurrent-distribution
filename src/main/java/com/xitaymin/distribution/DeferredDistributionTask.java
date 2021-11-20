package com.xitaymin.distribution;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class DeferredDistributionTask implements DistributionTask {
    private final Supplier<String> messageProvider;
    private final LocalDateTime sendingTime;

    public DeferredDistributionTask(Supplier<String> messageProvider, LocalDateTime sendingTime) {
        this.messageProvider = messageProvider;
        this.sendingTime = sendingTime;
    }

    public Supplier<String> getMessageProvider() {
        return messageProvider;
    }
}
