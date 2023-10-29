package com.example.timemanager;

import static com.example.timemanager.TimerApp.nullTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(this, TimerService.class);
        startService(intent);

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
            TimerService.timer.updateTimer();
        }
        if (id == R.id.delete_all) {
            TimerService.timer.clearTasks();
            TimerService.timer.updateTimer();
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
        TimerService.timer.saveTasksTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TimerService.timer.saveTasksTime();
    }


    long durationInMillis, millis, second, minute, hour;
    String[] from = { "text", "value"};
    int[] to = { R.id.tvText, R.id.tvValue};
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

            ArrayList<Map<String, Object>> data = new ArrayList<>(
                    taskTimes.size());
            Map<String, Object> m;

            for (int i = 0; i < taskTimes.size(); i++) {
                m = new HashMap<>();
                m.put("text", taskTimes.get(i).task);
                m.put("value", taskTimes.get(i).time / 60000 + " mins");
                data.add(m);
            }

            SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item_task,
                    from, to);

            lvOut.setAdapter(sAdapter);
        });
    }

}