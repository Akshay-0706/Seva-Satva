package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class adminScreen extends AppCompatActivity {

    boolean isEnrollmentStopped = false;

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

        findViewById(R.id.adminAllocateMentorsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnrollmentStopped)
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


        SwitchMaterial switchMaterial = findViewById(R.id.adminStopEnrollmentSwitch);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Courses").document("Flags").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = null;
                Map<String, Object> data = null;
                if (task.getResult() != null)
                    documentSnapshot = task.getResult();
                if (documentSnapshot.getData() != null)
                    data = documentSnapshot.getData();

                switchMaterial.setChecked(!(boolean) data.get("areStudentsAllowed"));
            }
        });

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> map = new HashMap<>();
                map.put("areStudentsAllowed", !isChecked);
                firestore.collection("Courses").document("Flags").set(map, SetOptions.merge());
                if (isChecked)
                    Toast.makeText(adminScreen.this, "Enrollment stopped!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(adminScreen.this, "Enrollment allowed!", Toast.LENGTH_SHORT).show();

                isEnrollmentStopped = isChecked;
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
}