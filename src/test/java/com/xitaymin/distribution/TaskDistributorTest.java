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
        SubscriberImpl subscriber = new SubscriberImpl("First");
        SubscriberImpl subscriber1 = new SubscriberImpl("Second");
        SubscriberImpl subscriber2 = new SubscriberImpl("Third");

        DistributionTask deferredTask = new DeferredDistributionTask(() -> "Today is " + LocalDateTime.now(),
                LocalDateTime.now().plus(periodInSeconds, ChronoUnit.SECONDS));
        DistributionTask instantTask = new InstantDistributionTask("Instant message");


        TaskDistributor taskDistributor = new TaskDistributor();

        taskDistributor.subscribe(subscriber);
        taskDistributor.subscribe(subscriber2);
        taskDistributor.unsubscribe(subscriber2);

        taskDistributor.startDistribution(deferredTask);

        taskDistributor.subscribe(subscriber1);

        taskDistributor.startDistribution(instantTask);

        try {
            Thread.sleep(periodInSeconds * 3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(subscriber.getReceivedMessages().size(), 2);
        assertEquals(subscriber1.getReceivedMessages().size(), 1);
        assertTrue(subscriber2.getReceivedMessages().isEmpty());


//        taskDistributor.exit();
    }

    @Timeout(periodInSeconds*4000)
    @Test()
    void subscribersDontReceiveMessagesWhenStopped() throws InterruptedException {
        SubscriberImpl subscriber = new SubscriberImpl("Subscriber");


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

        System.out.println(subscriber.getReceivedMessages());
        //first periodical message will be send with 0 delay
        assertTrue(subscriber.getReceivedMessages().size() <= 1);

//        taskDistributor.exit();

    }

    @Test
    void newMessagesCanBeReceivedAfterStop() throws InterruptedException {

        SubscriberImpl subscriber = new SubscriberImpl("Subscriber");

//        DistributionTask periodicalTask =
//                new PeriodicalDistributionTask(() -> "Next message will be sent in " + LocalDateTime.now()
//                        .plus(periodInSeconds, ChronoUnit.SECONDS), periodInSeconds);
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