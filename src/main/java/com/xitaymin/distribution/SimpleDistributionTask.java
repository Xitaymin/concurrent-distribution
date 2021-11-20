package com.xitaymin.distribution;

public class SimpleDistributionTask implements DistributionTask {
    private final String message;

    public SimpleDistributionTask(String message) {
        this.message = message;
    }
}
