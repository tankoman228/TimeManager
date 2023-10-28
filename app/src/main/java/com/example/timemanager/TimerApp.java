package com.example.timemanager;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class TimerApp {
    protected Timer timer;
    protected long startTime;
    protected boolean isRunning;
    protected TaskTime nullTask = new TaskTime("NULL TASK", 0);
    protected TaskTime currentTask = nullTask;
    protected ArrayList<TaskTime> taskTimes;

    final String savefilename = "stats.ser";

    protected Context context;

    public TimerApp(Context context) {

        this.context = context;

        timer = new Timer();
        startTime = 0;
        isRunning = false;
        taskTimes = new ArrayList<>();

        startTime = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            long previousTime = startTime,
                    currentTime, elapsedTime;

            @Override
            public void run() {

                currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - previousTime;
                currentTask.time += elapsedTime;
                previousTime = currentTime;

                updateTimer();
            }
        }, 0, 10);

        try {
            FileInputStream fis = context.openFileInput(savefilename);
            ObjectInputStream os = new ObjectInputStream(fis);
            taskTimes = (ArrayList<TaskTime>) os.readObject();
            os.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startTimer() {

        if (!isRunning) {
            isRunning = true;
        }
    }

    public void stopTimer() {

        if (isRunning) {

            isRunning = false;
            currentTask = nullTask;

            saveTasksTime();
        }
    }

    public void setCurrentTask(String task) {

        for (TaskTime tt : taskTimes) {
            if (tt.task.equals(task)) {
                currentTask = tt;
                return;
            }
        }

        currentTask = new TaskTime(task, 0);
        taskTimes.add(currentTask);
    }

    public ArrayList<TaskTime> getTaskTimes() {
        return taskTimes;
    }

    //ПЕРЕОПРЕДЕЛЯТЬ!!!!!!
    public abstract void updateTimer();

    public void saveTasksTime() {

        try {
            FileOutputStream fos = context.openFileOutput(savefilename, Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(taskTimes);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        // Save task time to file with today's date in the name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "task_times_" + dateFormat.format(new Date()) + ".txt";

        try {
            File file = new File(fileName);
            FileWriter writer = new FileWriter(file, false);

            for (TaskTime tt : taskTimes) {
                writer.write(
                        tt.task + " : " + tt.time / 60000 + " min + \n"
                );
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }
}