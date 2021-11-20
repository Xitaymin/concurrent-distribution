package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Suscriber {
    private final String name;
    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

    public Suscriber(String name) {
        this.name = name;
    }
}
