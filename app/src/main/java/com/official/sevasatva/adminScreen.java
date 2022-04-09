package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class adminScreen extends AppCompatActivity {

    boolean isEnrollmentStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstLaunch", false).apply();

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isAdmin", true).apply();

        findViewById(R.id.allocate_mentors_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnrollmentStopped)
                    startActivity(new Intent(adminScreen.this, mentorAllocation.class));
                else
                    Toast.makeText(adminScreen.this, "Stop the enrollment first!", Toast.LENGTH_SHORT).show();
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
    }
}