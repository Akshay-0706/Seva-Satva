package com.official.sevasatva;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    String version = "", startCourse = "", underMaintenance = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView sevaText = findViewById(R.id.splashTextSeva);
        TextView slashText = findViewById(R.id.splashTextSlash);
        TextView satvaText = findViewById(R.id.splashTextSatva);

        sevaText.animate().alpha(1).setDuration(500);
        slashText.animate().alpha(1).setDuration(1000);
        satvaText.animate().alpha(1).setDuration(1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirstRunAppIntro = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isFirstRunAppIntro", true);

                boolean isUserStudent = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isUserStudent", true);

                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeLoading", true).apply();

                if (isFirstRunAppIntro) {
                    //show start activity
                    startActivity(new Intent(splash.this, appIntro.class));
                    finish();
                } else if (isUserStudent) {
                    startActivity(new Intent(splash.this, studentLogin.class));
                    finish();
                } else {
                    startActivity(new Intent(splash.this, mentorLogin.class));
                    finish();
                }
            }
        }, 2000);
    }
}