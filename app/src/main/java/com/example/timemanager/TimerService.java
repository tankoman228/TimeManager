package com.example.timemanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    public static CustomTimer timer;
    public static TimerCallback currentActivity;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer = new CustomTimer(this);
        timer.updateTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.saveTasksTime();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }


    public class CustomTimer extends TimerApp {

        @Override
        public void deleteTask(String task) {
            super.deleteTask(task);
            updateTimer();
        }

        @Override
        public void clearTasks() {
            super.clearTasks();
            stopTimer();
            updateTimer();
        }

        public CustomTimer(Context context) {
            super(context);
        }

        @Override
        public void startTimer() {
            super.startTimer();
        }

        @Override
        public void updateTimer() {
            currentActivity.onTimerTick(currentTask, taskTimes);
        }
    }
}
