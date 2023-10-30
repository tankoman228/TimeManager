package com.example.timemanager;

import java.util.List;

public interface TimerCallback {
    void onTimerTick(TaskTime currentTask, List<TaskTime> taskTimes);
    void RebuildAdapter(List<TaskTime> taskTimes);
}
