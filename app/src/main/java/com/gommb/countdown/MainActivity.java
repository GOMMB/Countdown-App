package com.gommb.countdown;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private TextView text;
    private TextView secText;
    public static final String PREFS = "CountdownPrefs";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private long out;
    private String endMessage = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView);
        secText = findViewById(R.id.sec);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        editor = prefs.edit();

        out = prefs.getLong("out", Long.MIN_VALUE);
        endMessage = prefs.getString("endMessage", "");
        if (out == Long.MIN_VALUE)
            showDateTimePicker();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        }, 0, 1000);

        holdListener();

    }

    private void update() {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        long now = System.currentTimeMillis() / 1000;
        int sec;
        int min;
        int hour;
        String secc;
        String minn;
        String hourr;
        String count;
        if (out > now) {
            int dist = (int)(out - now);
            secText.setText(formatter.format(dist));
            sec = dist % 60;
            dist /= 60;
            min = dist % 60;
            dist /= 60;
            hour = dist % 24;
            dist /= 24;
            if (sec < 10)
                secc = "0"+sec;
            else
                secc = String.valueOf(sec);
            if (min < 10)
                minn = "0"+min;
            else
                minn = String.valueOf(min);
            if (hour < 10)
                hourr = "0"+hour;
            else
                hourr = String.valueOf(hour);
            count = dist+":"+hourr+":"+minn+":"+secc;
            text.setText(count);
        } else {
            text.setText(endMessage);
        }
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                        out = date.getTimeInMillis() / 1000;
                        editor.putLong("out", out);
                        editor.apply();

                        final EditText endmsg = new EditText(MainActivity.this);
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("The message once the countdown ends.")
                                .setView(endmsg)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        endMessage = endmsg.getText().toString();

                                        editor.putString("endMessage", endMessage);
                                        editor.apply();
                                    }
                                }).show();
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }

    private void holdListener() {
        findViewById(R.id.activity_main).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDateTimePicker();
                return true;
            }
        });
    }

}
