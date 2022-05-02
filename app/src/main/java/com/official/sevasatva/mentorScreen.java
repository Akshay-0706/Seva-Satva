package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.official.sevasatva.databinding.ActivityMentorScreenBinding;

public class mentorScreen extends AppCompatActivity {

    ActivityMentorScreenBinding binding;
    String value = "home";

    internetCheckListener internetCheckListener = new internetCheckListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (value.equals("home"))
            super.onBackPressed();
        else {
            changeNavTab((LinearLayout) findViewById(R.id.homeTab), (ImageView) findViewById(R.id.homeIcon), (TextView) findViewById(R.id.homeTabText), "Home", R.drawable.xtra_home_icon, R.drawable.xtra_home_icon_selected);
            replaceFragment(new mentorHome());
            value = "home";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_screen);

        binding = ActivityMentorScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new mentorHome());
        setNavTab((LinearLayout) findViewById(R.id.homeTab), (ImageView) findViewById(R.id.homeIcon), (TextView) findViewById(R.id.homeTabText), "Home", R.drawable.xtra_home_icon, R.drawable.xtra_home_icon_selected);

        checkFlags();

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            String intentValue = bundle.getString("value");
//
//            if (intentValue.equals("doubts")) {
//                changeNavTab((LinearLayout) findViewById(R.id.doubtsTab), (ImageView) findViewById(R.id.doubtsIcon), (TextView) findViewById(R.id.doubtsTabText), "Doubts", R.drawable.xtra_doubts_icon, R.drawable.xtra_doubts_icon_selected);
//                replaceFragment(new mentorDoubts());
//                value = "doubts";
//            } else if (intentValue.equals("tests")) {
//                changeNavTab((LinearLayout) findViewById(R.id.testsTab), (ImageView) findViewById(R.id.testsIcon), (TextView) findViewById(R.id.testsTabText), "Tests", R.drawable.xtra_tests_icon, R.drawable.xtra_tests_icon_selected);
//                replaceFragment(new mentorTests());
//                value = "tests";
//            }
//        }

        startService(new Intent(this, serviceAnsNotify.class));
        startService(new Intent(this, serviceChatNotify.class));
        startService(new Intent(this, serviceTestsNotify.class));
        alarmReceiver.setAlarm(this);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstLaunch", false).apply();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isMentorLoggedIn", true).apply();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeAnsLoading", true).apply();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeChatLoading", true).apply();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeTestsLoading", true).apply();

        findViewById(R.id.homeTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value.equals("home")) {
                    changeNavTab((LinearLayout) findViewById(R.id.homeTab), (ImageView) findViewById(R.id.homeIcon), (TextView) findViewById(R.id.homeTabText), "Home", R.drawable.xtra_home_icon, R.drawable.xtra_home_icon_selected);
                    replaceFragment(new mentorHome());
                    value = "home";
                }
            }
        });
        findViewById(R.id.doubtsTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value.equals("doubts")) {
                    changeNavTab((LinearLayout) findViewById(R.id.doubtsTab), (ImageView) findViewById(R.id.doubtsIcon), (TextView) findViewById(R.id.doubtsTabText), "Doubts", R.drawable.xtra_doubts_icon, R.drawable.xtra_doubts_icon_selected);
                    replaceFragment(new mentorDoubts());
                    value = "doubts";
                }
            }
        });
        findViewById(R.id.testsTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value.equals("tests")) {
                    changeNavTab((LinearLayout) findViewById(R.id.testsTab), (ImageView) findViewById(R.id.testsIcon), (TextView) findViewById(R.id.testsTabText), "Tests", R.drawable.xtra_tests_icon, R.drawable.xtra_tests_icon_selected);
                    replaceFragment(new mentorTests());
                    value = "tests";
                }
            }
        });
        findViewById(R.id.proTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value.equals("pro")) {
                    changeNavTab((LinearLayout) findViewById(R.id.proTab), (ImageView) findViewById(R.id.proIcon), (TextView) findViewById(R.id.proTabText), "Profile", R.drawable.xtra_pro_icon, R.drawable.xtra_pro_icon_selected);
                    replaceFragment(new mentorProfile());
                    value = "pro";
                }
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void setNavTab(LinearLayout linearLayout, ImageView imageView, TextView textView, String text, int drawable, int drawableSelected) {
        Drawable backgrounds1[] = new Drawable[2];
        Drawable backgrounds2[] = new Drawable[2];
        Resources res = getResources();
        backgrounds1[0] = res.getDrawable(R.drawable.main_screen_not_selected_tab);
        backgrounds1[1] = res.getDrawable(R.drawable.main_screen_selected_tab);
        backgrounds2[0] = res.getDrawable(drawable);
        backgrounds2[1] = res.getDrawable(drawableSelected);

        TransitionDrawable transitionDrawable1 = new TransitionDrawable(backgrounds1);
        TransitionDrawable transitionDrawable2 = new TransitionDrawable(backgrounds2);
        transitionDrawable1.setCrossFadeEnabled(true);
        transitionDrawable2.setCrossFadeEnabled(true);
        linearLayout.setBackground(transitionDrawable1);
        transitionDrawable1.startTransition(300);

        imageView.setBackground(transitionDrawable2);
        transitionDrawable2.startTransition(300);

        textView.setText(text);
    }

    private void changeNavTab(LinearLayout linearLayoutNew, ImageView imageViewNew, TextView textViewNew, String text, int drawableNew, int drawableNewSelected) {
        LinearLayout linearLayoutCurrent = null;
        ImageView imageViewCurrent = null;
        TextView textViewCurrent = null;
        int drawableCurrent, drawableCurrentSelected;
        switch (value) {
            case "home":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.homeTab);
                imageViewCurrent = (ImageView) findViewById(R.id.homeIcon);
                textViewCurrent = (TextView) findViewById(R.id.homeTabText);
                drawableCurrent = R.drawable.xtra_home_icon;
                drawableCurrentSelected = R.drawable.xtra_home_icon_selected;
                break;
            case "doubts":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.doubtsTab);
                imageViewCurrent = (ImageView) findViewById(R.id.doubtsIcon);
                textViewCurrent = (TextView) findViewById(R.id.doubtsTabText);
                drawableCurrent = R.drawable.xtra_doubts_icon;
                drawableCurrentSelected = R.drawable.xtra_doubts_icon_selected;
                break;
            case "tests":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.testsTab);
                imageViewCurrent = (ImageView) findViewById(R.id.testsIcon);
                textViewCurrent = (TextView) findViewById(R.id.testsTabText);
                drawableCurrent = R.drawable.xtra_tests_icon;
                drawableCurrentSelected = R.drawable.xtra_tests_icon_selected;
                break;
            case "pro":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.proTab);
                imageViewCurrent = (ImageView) findViewById(R.id.proIcon);
                textViewCurrent = (TextView) findViewById(R.id.proTabText);
                drawableCurrent = R.drawable.xtra_pro_icon;
                drawableCurrentSelected = R.drawable.xtra_pro_icon_selected;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + value);
        }

        Drawable backgrounds1[] = new Drawable[2];
        Drawable backgrounds2[] = new Drawable[2];
        Resources res = getResources();
        backgrounds1[0] = res.getDrawable(R.drawable.main_screen_selected_tab);
        backgrounds1[1] = res.getDrawable(R.drawable.main_screen_not_selected_tab);
        backgrounds2[0] = res.getDrawable(drawableCurrentSelected);
        backgrounds2[1] = res.getDrawable(drawableCurrent);

        TransitionDrawable transitionDrawable1 = new TransitionDrawable(backgrounds1);
        TransitionDrawable transitionDrawable2 = new TransitionDrawable(backgrounds2);
        transitionDrawable1.setCrossFadeEnabled(true);
        transitionDrawable2.setCrossFadeEnabled(true);

        linearLayoutCurrent.setBackground(transitionDrawable1);
        transitionDrawable1.startTransition(300);

        imageViewCurrent.setBackground(transitionDrawable2);
        transitionDrawable2.startTransition(300);

        textViewCurrent.setText(null);
        setNavTab(linearLayoutNew, imageViewNew, textViewNew, text, drawableNew, drawableNewSelected);
    }

    private void checkFlags() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("flags").get().addOnCompleteListener(
                task -> {
                    String version = "", startCourse = "", underMaintenance = "";

                    DataSnapshot dataSnapshot = task.getResult();
                    version = (String) dataSnapshot.child("version").getValue();
                    startCourse = (boolean) dataSnapshot.child("startCourse").getValue() ? "true" : "false";
                    underMaintenance = (boolean) dataSnapshot.child("underMaintenance").getValue() ? "true" : "false";

                    //        new Handler().postDelayed(new Runnable() {
                    //            @Override
                    //            public void run() {

                    Dialog alertDialog = new Dialog(mentorScreen.this);
                    alertDialog.setContentView(R.layout.fragment_alert);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(false);
                    LottieAnimationView alertDialogAnim = (LottieAnimationView) alertDialog.findViewById(R.id.lottieAnimationView);

                    if (startCourse.equals("false")) {
                        alertDialogAnim.setAnimation("course_ended.json");
                        alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                        ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("The course has ended!");
                        ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(mentorScreen.this, R.color.white));
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
                                        Toast.makeText(mentorScreen.this, "Got you!", Toast.LENGTH_SHORT).show();
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
                            } else {
                                alertDialogAnim.setAnimation("app_maintenance.json");
                                alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                                ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("App is under maintenance, try again after some time.");
                                ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(mentorScreen.this, R.color.white));
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
                }
        );
    }
}