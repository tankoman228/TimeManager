package com.example.timemanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    public static CustomTimer timer;
    public static TimerCallback currentActivity;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "TimeManager_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Service Channel",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.rubik)
                .setContentTitle("My Service")
                .setContentText("Service is running in foreground")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);

        timer = new CustomTimer(this);
        timer.updateTimer();

        return START_NOT_STICKY;
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
