package com.example.timemanager;

import java.io.Serializable;

public class TaskTime implements Serializable {
    public String task;
    public long time;

    public TaskTime(String task, long time) {
        this.task = task;
        this.time = time;
    }
}
