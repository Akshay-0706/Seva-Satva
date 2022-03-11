package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView sevaText = (TextView) findViewById(R.id.splashTextSeva);
        TextView slashText = (TextView) findViewById(R.id.splashTextSlash);
        TextView satvaText = (TextView) findViewById(R.id.splashTextSatva);

        sevaText.animate().alpha(1).setDuration(1000);
        slashText.animate().alpha(1).setDuration(1500);
        satvaText.animate().alpha(1).setDuration(2000);



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isFirstRun", true);

                if (isFirstRun) {
                    //show start activity

                    startActivity(new Intent(splash.this, appIntro.class));
                    finish();
                }
                else {
                    startActivity(new Intent(splash.this, studentLogin.class));
                    finish();
                }

                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply();
            }
        }, 3000);
    }
}