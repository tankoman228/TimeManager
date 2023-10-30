package com.example.timemanager;

import static androidx.core.app.ServiceCompat.stopForeground;
import static com.example.timemanager.TimerApp.nullTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TimerCallback {

    TextView tvOut;
    ListView lvOut;
    EditText etTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOut = findViewById(R.id.tvTime);
        lvOut = findViewById(R.id.lvStats);
        etTask = findViewById(R.id.etTask);

        TimerService.currentActivity = this;

        findViewById(R.id.btnStop).setOnClickListener(view -> {
            TimerService.timer.stopTimer();
        });

        findViewById(R.id.btnStart).setOnClickListener(view -> {
            TimerService.timer.setCurrentTask(etTask.getText().toString());
            TimerService.timer.startTimer();
            RebuildAdapter(TimerService.timer.taskTimes);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(this, TimerService.class);
        startForegroundService(intent);
        //TimerService.timer.updateTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete_chosen) {
            TimerService.timer.deleteTask(etTask.getText().toString());
            RebuildAdapter(TimerService.timer.taskTimes);
        }
        if (id == R.id.delete_all) {
            TimerService.timer.clearTasks();
            RebuildAdapter(TimerService.timer.taskTimes);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //timer.startTimer();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TimerService.timer.saveTasksTime();
    }


    long durationInMillis, millis, second, minute, hour;

    @Override
    public void onTimerTick(TaskTime currentTask, List<TaskTime> taskTimes) {

        durationInMillis = currentTask.time;
        millis = durationInMillis % 1000;
        second = (durationInMillis / 1000) % 60;
        minute = (durationInMillis / (60000)) % 60;
        hour = (durationInMillis / (3600000));

        runOnUiThread(() -> {

            if (currentTask != nullTask) {
                tvOut.setText(String.format("%02d:%02d:%02d.%d", hour, minute, second, millis));
            }

            int currentPosition = taskTimes.indexOf(currentTask);

            if (currentPosition >= 0) {
                Map<String, Object> currentItem = new HashMap<>();
                currentItem.put("text", currentTask.task);
                currentItem.put("value", currentTask.time / 60000 + " min");

                View view = lvOut.getChildAt(currentPosition - lvOut.getFirstVisiblePosition());
                if (view != null) {
                    TextView tvText = view.findViewById(R.id.tvText);
                    TextView tvValue = view.findViewById(R.id.tvValue);
                    tvText.setText((String) currentItem.get("text"));
                    tvValue.setText((String) currentItem.get("value"));
                }
            }

        });
    }

    String[] from = { "text", "value"};
    int[] to = { R.id.tvText, R.id.tvValue};

    @Override
    public void RebuildAdapter(List<TaskTime> taskTimes) {
        ArrayList<Map<String, Object>> data = new ArrayList<>(
                taskTimes.size());
        Map<String, Object> m;

        for (int i = 0; i < taskTimes.size(); i++) {
            m = new HashMap<>();
            m.put("text", taskTimes.get(i).task);
            m.put("value", taskTimes.get(i).time / 60000 + " min");
            data.add(m);
        }

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item_task,
                from, to);

        lvOut.setAdapter(sAdapter);
    }

}