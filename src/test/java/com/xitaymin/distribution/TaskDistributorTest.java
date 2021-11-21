package com.xitaymin.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskDistributorTest {
    @Timeout(5000)
    @Test
    void onlyInitialSubscribersGetAllMessages() {
        SubscriberImpl subscriber = new SubscriberImpl("First");
        SubscriberImpl subscriber1 = new SubscriberImpl("Second");

        long delayInSeconds = 2;
        DistributionTask deferredTask = new DeferredDistributionTask(() -> "Today is " + LocalDateTime.now(),
                LocalDateTime.now().plus(delayInSeconds, ChronoUnit.SECONDS));
        DistributionTask instantTask = new InstantDistributionTask("Instant message");


        TaskDistributor taskDistributor = new TaskDistributor();
        taskDistributor.subscribe(subscriber);
        taskDistributor.submit(instantTask);
        taskDistributor.subscribe(subscriber1);
        taskDistributor.submit(deferredTask);
        try {
            Thread.sleep(delayInSeconds * 3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskDistributor.stop();

        assertEquals(subscriber.getReceivedMessages().size(), 2);
        assertEquals(subscriber1.getReceivedMessages().size(), 1);

    }

    @Timeout(5000)
    @Test()
    void subscribersDontReceiveMessagesWhenStopped() throws InterruptedException {
        SubscriberImpl subscriber = new SubscriberImpl("Subscriber");
        long periodInSeconds = 2;

        DistributionTask periodicalTask =
                new PeriodicalDistributionTask(() -> "Next message will be sent in " + LocalDateTime.now()
                        .plus(periodInSeconds, ChronoUnit.SECONDS), periodInSeconds);
        DistributionTask deferredTask = new DeferredDistributionTask(() -> "Today is " + LocalDateTime.now(),
                LocalDateTime.now().plus(periodInSeconds, ChronoUnit.SECONDS));

        TaskDistributor taskDistributor = new TaskDistributor();
        taskDistributor.subscribe(subscriber);
        taskDistributor.submit(periodicalTask);
        taskDistributor.submit(deferredTask);
        taskDistributor.stop();

        Thread.sleep(periodInSeconds * 3000);

        DistributionTask instantTask = new InstantDistributionTask("Instant message");

        taskDistributor.submit(instantTask);
        System.out.println(subscriber.getReceivedMessages());

        assertEquals(subscriber.getReceivedMessages().size(), 2);
    }
}