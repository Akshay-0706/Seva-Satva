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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("flags").get().addOnCompleteListener(
                task -> {
                    DataSnapshot dataSnapshot = task.getResult();
                    version = (String) dataSnapshot.child("version").getValue();
                    startCourse = (boolean) dataSnapshot.child("startCourse").getValue() ? "true" : "false";
                    underMaintenance = (boolean) dataSnapshot.child("underMaintenance").getValue() ? "true" : "false";

                    //        new Handler().postDelayed(new Runnable() {
                    //            @Override
                    //            public void run() {

                    Dialog alertDialog = new Dialog(splash.this);
                    alertDialog.setContentView(R.layout.fragment_alert);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(false);
                    LottieAnimationView alertDialogAnim = (LottieAnimationView) alertDialog.findViewById(R.id.lottieAnimationView);

                    if (startCourse.equals("false")) {
                        alertDialogAnim.setAnimation("course_ended.json");
                        alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                        ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("The course has ended!");
                        ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(splash.this, R.color.white));
                        alertDialog.findViewById(R.id.alertNegative).setBackground(getDrawable(R.drawable.student_profile_feedback_main_btn_bg));
                        alertDialog.show();

                        alertDialog.findViewById(R.id.alertNegative).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                finishAffinity();
                            }
                        });
                    } else {
                        if (!version.equals(BuildConfig.VERSION_NAME)) {
                            alertDialogAnim.setAnimation("app_update.json");
                            ((AppCompatButton) alertDialog.findViewById(R.id.alertPositive)).setText("Update");
                            ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("An update is available, please update the app to continue.");
                            alertDialog.show();

                            alertDialog.findViewById(R.id.alertNegative).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    finishAffinity();
                                }
                            });

                            alertDialog.findViewById(R.id.alertPositive).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    String urlString = "https://youtu.be/dQw4w9WgXcQ";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    //  intent.setPackage("com.android.chrome");

                                    intent.setPackage("com.google.android.youtube");
                                    try {
                                        Toast.makeText(splash.this, "Got you!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finishAffinity();
                                    } catch (ActivityNotFoundException ex) {
                                        // Chrome browser presumably not installed so allow user to choose instead
                                        intent.setPackage(null);
                                        startActivity(intent);
                                    }
                                }
                            });

                        } else {
                            if (underMaintenance.equals("false")) {
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
                            } else {
                                alertDialogAnim.setAnimation("app_maintenance.json");
                                alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                                ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("App is under maintenance, try again after some time.");
                                ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(splash.this, R.color.white));
                                alertDialog.findViewById(R.id.alertNegative).setBackground(getDrawable(R.drawable.student_profile_feedback_main_btn_bg));
                                alertDialog.show();

                                alertDialog.findViewById(R.id.alertNegative).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        finishAffinity();
                                    }
                                });
                            }
                        }
                    }
                    //            }
                    //        }, 1000);


                }
        );
    }
}