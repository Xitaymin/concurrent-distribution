package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubscriberImpl implements Subscriber {
    private final String name;
    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

    public SubscriberImpl(String name) {
        this.name = name;
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void receiveMessage(String message) {
        receivedMessages.add(message);
    }

}
