package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubscriberImpl implements Subscriber {
    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

    public SubscriberImpl() {
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void receiveMessage(String message) {
        receivedMessages.add(message);
    }

}
