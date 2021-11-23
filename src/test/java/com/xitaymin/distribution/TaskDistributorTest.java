package com.xitaymin.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskDistributorTest {
    private final long periodInSeconds = 1;

    @Timeout(periodInSeconds*4000)
    @Test
    void onlyInitialSubscribersGetAllMessages() {
        SubscriberImpl first = new SubscriberImpl();
        SubscriberImpl second = new SubscriberImpl();
        SubscriberImpl third = new SubscriberImpl();

        DistributionTask deferredTask = new DeferredDistributionTask(() -> "Today is " + LocalDateTime.now(),
                LocalDateTime.now().plus(periodInSeconds, ChronoUnit.SECONDS));
        DistributionTask instantTask = new InstantDistributionTask("Instant message");


        TaskDistributor taskDistributor = new TaskDistributor();

        taskDistributor.subscribe(first);
        taskDistributor.subscribe(third);
        taskDistributor.unsubscribe(third);

        taskDistributor.startDistribution(deferredTask);

        taskDistributor.subscribe(second);

        taskDistributor.startDistribution(instantTask);

        try {
            Thread.sleep(periodInSeconds * 3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(first.getReceivedMessages().size(), 2);
        assertEquals(second.getReceivedMessages().size(), 1);
        assertTrue(third.getReceivedMessages().isEmpty());

    }

    @Timeout(periodInSeconds*4000)
    @Test()
    void subscribersDontReceiveMessagesWhenStopped() throws InterruptedException {
        SubscriberImpl subscriber = new SubscriberImpl();


        DistributionTask periodicalTask =
                new PeriodicalDistributionTask(() -> "Next message will be sent in " + LocalDateTime.now()
                        .plus(periodInSeconds, ChronoUnit.SECONDS), periodInSeconds);
        DistributionTask deferredTask = new DeferredDistributionTask(() -> "Today is " + LocalDateTime.now(),
                LocalDateTime.now().plus(periodInSeconds, ChronoUnit.SECONDS));

        TaskDistributor taskDistributor = new TaskDistributor();

        taskDistributor.subscribe(subscriber);

        taskDistributor.startDistribution(periodicalTask);
        taskDistributor.startDistribution(deferredTask);

        taskDistributor.stopDistribution();

        Thread.sleep(periodInSeconds * 3000);
        //first periodical message will be send with 0 delay
        assertTrue(subscriber.getReceivedMessages().size() <= 1);

    }

    @Timeout(periodInSeconds * 5000)
    @Test
    void newMessagesCanBeReceivedAfterStop() throws InterruptedException {

        SubscriberImpl subscriber = new SubscriberImpl();

        DistributionTask deferredTask = new DeferredDistributionTask(() -> "Today is " + LocalDateTime.now(),
                LocalDateTime.now().plus(periodInSeconds, ChronoUnit.SECONDS));

        TaskDistributor taskDistributor = new TaskDistributor();
        taskDistributor.subscribe(subscriber);
        taskDistributor.startDistribution(deferredTask);
        taskDistributor.stopDistribution();

        DistributionTask instantTask = new InstantDistributionTask("Instant message");
        taskDistributor.startDistribution(instantTask);

        Thread.sleep(periodInSeconds * 3000);

        taskDistributor.stopDistribution();

        assertEquals(subscriber.getReceivedMessages().size(), 1);

    }
}