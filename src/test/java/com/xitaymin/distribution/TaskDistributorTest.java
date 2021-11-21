package com.xitaymin.distribution;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskDistributorTest {

    @Test
    void startDistribution() {
        SubscriberImpl subscriber = new SubscriberImpl("First");
        SubscriberImpl subscriber1 = new SubscriberImpl("Second");

        System.out.println(LocalDateTime.now());

        DistributionTask task = new DeferredDistributionTask(() -> "Today is" + LocalDateTime.now(),
                LocalDateTime.of(2021, 11, 21, 14, 15));
        DistributionTask task1 = new InstantDistributionTask("Instant message");

        TaskDistributor taskDistributor = new TaskDistributor();
        taskDistributor.subscribe(subscriber);
        taskDistributor.submit(task);
        taskDistributor.subscribe(subscriber1);
        taskDistributor.submit(task1);
        taskDistributor.stop();

        assertEquals(subscriber.getReceivedMessages().size(), 2);
        assertEquals(subscriber.getReceivedMessages().size(), 1);


    }
}