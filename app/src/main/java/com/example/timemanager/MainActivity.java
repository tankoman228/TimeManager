package com.example.timemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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

        timer = new CustomTimer(this);

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

        String[] from = { "text"};
        int[] to = { R.id.tvText};

        @Override
        public void updateTimer() {

            if (currentTask == nullTask) return;

            tvOut.setText(String.valueOf((float)currentTask.time / 1000.0f));

            ArrayList<Map<String, Object>> data = new ArrayList<>(
                    taskTimes.size());
            Map<String, Object> m;

            for (int i = 0; i < taskTimes.size(); i++) {
                m = new HashMap<>();
                m.put("text", taskTimes.get(i).task +  " >=< " + taskTimes.get(i).time / 60000 + " mins");
                data.add(m);
            }

            runOnUiThread(() -> {
                SimpleAdapter sAdapter = new SimpleAdapter(context, data, R.layout.item_task,
                        from, to);

                lvOut.setAdapter(sAdapter);
            });
        }
    }
}