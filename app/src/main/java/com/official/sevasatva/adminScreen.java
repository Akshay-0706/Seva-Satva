package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminScreen extends AppCompatActivity {

    boolean enrollmentAllowed = false, startCourse = false;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstLaunch", false).apply();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isAdmin", true).apply();

        checkFlags();

        findViewById(R.id.adminAllocateMentorsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enrollmentAllowed)
                    startActivity(new Intent(adminScreen.this, mentorAllocation.class));
                else
                    Toast.makeText(adminScreen.this, "Stop the enrollment first!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.adminLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        SwitchMaterial switchCourse = findViewById(R.id.adminStartCourseSwitch);
        SwitchMaterial switchEnrollment = findViewById(R.id.adminStopEnrollmentSwitch);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("flags").child("areStudentsAllowed").get().addOnCompleteListener(
//                new OnCompleteListener<DataSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        isEnrollmentStopped = (boolean) task.getResult().getValue();
//                        switchEnrollment.setChecked(isEnrollmentStopped);
//                    }
//                }
//        );
        databaseReference.child("flags").child("startCourse").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                startCourse = (boolean) snapshot.getValue();
                switchCourse.setChecked(startCourse);

                if (startCourse)
                    Toast.makeText(adminScreen.this, "Course started!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(adminScreen.this, "Course ended!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("flags").child("areStudentsAllowed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enrollmentAllowed = (boolean) snapshot.getValue();
                switchEnrollment.setChecked(enrollmentAllowed);

                if (enrollmentAllowed)
                    Toast.makeText(adminScreen.this, "Enrollment allowed!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(adminScreen.this, "Enrollment stopped!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection("Courses").document("Flags").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot documentSnapshot = null;
//                Map<String, Object> data = null;
//                if (task.getResult() != null)
//                    documentSnapshot = task.getResult();
//                if (documentSnapshot.getData() != null)
//                    data = documentSnapshot.getData();
//
//                switchEnrollment.setChecked(!(boolean) data.get("areStudentsAllowed"));
//            }
//        });

        switchCourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("flags").child("startCourse").setValue(isChecked);
            }
        });

        switchEnrollment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("flags").child("areStudentsAllowed").setValue(isChecked);
            }
        });



        findViewById(R.id.adminContactDevelopersButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"akshay0706vhatkar@gmail.com", "ddpshah123@gmail.com", "meetshrimankar1@gmail.com"};//Add multiple recipients here
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");//Added Gmail Package to forcefully open Gmail App
                startActivity(Intent.createChooser(intent, "Send email"));
            }
        });

        findViewById(R.id.adminGetCourseDateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = "https://docs.google.com/spreadsheets/d/1wt9ifWS34BlNn-QSjEZkuLYXjcsZlDKcJ_0n9YQ4Wr8/export?format=xlsx&gid=1090794568";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });
    }

    private void logout() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().clear().apply();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("426988667812-meoa78skojkt8d3u0rs5mi9dd4i5nok3.apps.googleusercontent.com") // R.string.default_web_client_id
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInClient.signOut();

        startActivity(new Intent(this, splash.class));
        finishAffinity();
    }

    private void checkFlags() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("flags").get().addOnCompleteListener(
                task -> {
                    String version = "", startCourse = "", underMaintenance = "";

                    DataSnapshot dataSnapshot = task.getResult();
                    version = (String) dataSnapshot.child("version").getValue();
                    underMaintenance = (boolean) dataSnapshot.child("underMaintenance").getValue() ? "true" : "false";

                    Dialog alertDialog = new Dialog(adminScreen.this);
                    alertDialog.setContentView(R.layout.fragment_alert);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(false);
                    LottieAnimationView alertDialogAnim = (LottieAnimationView) alertDialog.findViewById(R.id.lottieAnimationView);

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

                                    intent.setPackage("com.google.android.youtube");
                                    try {
                                        Toast.makeText(adminScreen.this, "Got you!", Toast.LENGTH_SHORT).show();
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
                            if (!underMaintenance.equals("false")) {
                                alertDialogAnim.setAnimation("app_maintenance.json");
                                alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                                ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("App is under maintenance, try again after some time.");
                                ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(adminScreen.this, R.color.white));
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
        );
    }
}