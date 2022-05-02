package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class splash extends AppCompatActivity {

    String version = "", startCourse = "", underMaintenance = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView sevaText = (TextView) findViewById(R.id.splashTextSeva);
        TextView slashText = (TextView) findViewById(R.id.splashTextSlash);
        TextView satvaText = (TextView) findViewById(R.id.splashTextSatva);

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