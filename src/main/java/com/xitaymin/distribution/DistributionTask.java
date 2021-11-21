package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public interface DistributionTask {

    Future<?> sentMessage(ScheduledExecutorService executorService, List<Subscriber> subscribers);
}
