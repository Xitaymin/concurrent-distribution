package com.xitaymin.distribution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskProvider {
    private final List<DistributionTask> tasks = new CopyOnWriteArrayList<>();

    public void addTask(DistributionTask task) {
        tasks.add(task);
    }


}
