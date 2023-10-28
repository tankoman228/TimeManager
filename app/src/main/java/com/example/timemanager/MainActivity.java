package com.example.timemanager;

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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    CustomTimer timer;
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

        findViewById(R.id.btnStop).setOnClickListener(view -> {
            timer.stopTimer();
        });

        findViewById(R.id.btnStart).setOnClickListener(view -> {
            timer.setCurrentTask(etTask.getText().toString());
            timer.startTimer();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timer = new CustomTimer(this);
        timer.renderAdapter();
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
            timer.deleteTask(etTask.getText().toString());
            timer.renderAdapter();
        }
        if (id == R.id.delete_all) {
            timer.clearTasks();
            timer.renderAdapter();
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
        timer.saveTasksTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.saveTasksTime();
    }

    private class CustomTimer extends TimerApp {

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

            if (currentTask == null) {
                Toast.makeText(context, "No task chosen", Toast.LENGTH_SHORT).show();
                return;
            }

            super.startTimer();
        }



        long durationInMillis, millis, second, minute, hour;

        @Override
        public void updateTimer() {

            if (currentTask == nullTask) return;

            durationInMillis = currentTask.time;
            millis = durationInMillis % 1000;
            second = (durationInMillis / 1000) % 60;
            minute = (durationInMillis / (60000)) % 60;
            hour = (durationInMillis / (3600000));

            tvOut.setText(String.format("%02d:%02d:%02d.%d", hour, minute, second, millis));

            runOnUiThread(() -> {
                renderAdapter();
            });
        }

        String[] from = { "text", "value"};
        int[] to = { R.id.tvText, R.id.tvValue};
        public void renderAdapter() {

            ArrayList<Map<String, Object>> data = new ArrayList<>(
                    taskTimes.size());
            Map<String, Object> m;

            for (int i = 0; i < taskTimes.size(); i++) {
                m = new HashMap<>();
                m.put("text", taskTimes.get(i).task);
                m.put("value", taskTimes.get(i).time / 60000 + " mins");
                data.add(m);
            }

            SimpleAdapter sAdapter = new SimpleAdapter(context, data, R.layout.item_task,
                    from, to);

            lvOut.setAdapter(sAdapter);
        }
    }
}