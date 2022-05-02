package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class mentorTestsDetails extends AppCompatActivity {

    RecyclerView mentorTestsDetailsRecyclerView;
    List<mentorTestsDetailsModel> studentsList = new ArrayList<>();

    String id, title, deadline;
    long submitted;
    int marks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_tests_details);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        title = bundle.getString("title");
        marks = Integer.parseInt(bundle.getString("marks"));
        deadline = bundle.getString("deadline");
        ((TextView) findViewById(R.id.mentorTestsDetailsTitle)).setText(title);
        ((TextView) findViewById(R.id.mentorTestsDetailsMarks)).setText("Marks: " + marks);
        ((TextView) findViewById(R.id.mentorTestsDetailsDeadline)).setText("Deadline: " + deadline);

        mentorTestsDetailsRecyclerView = findViewById(R.id.mentorTestsDetailsRecyclerView);

        setDetails();
    }

    private void setDetails() {
        mentorTestsDetailsRecyclerView.setHasFixedSize(true);
        mentorTestsDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout lottieAnimationView = findViewById(R.id.mentorTestsDetailsNoSubAnimation);

        Dialog loadingDialog = new Dialog(this);
        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("tests")
                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                .child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsList.clear();

                if (snapshot.hasChild("students")) {
                    lottieAnimationView.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.child("students").getChildren()) {

                        String studentEmail = dataSnapshot.getKey();
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        mentorTestsDetailsModel mentorTestsDetailsModel = new mentorTestsDetailsModel(studentEmail, map.get("name").toString(), map.get("branch").toString(),
                                map.get("class").toString(), map.get("uid").toString(), map.get("grades").toString(), map.get("status").toString());

                        studentsList.add(mentorTestsDetailsModel);

                        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
                            loadingDialog.dismiss();
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeLoading", false).apply();
                        }
                    }

                    if (snapshot.hasChild("submitted"))
                        databaseReference.child("tests")
                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                .child(id).child("submitted").setValue(snapshot.child("students").getChildrenCount());
                } else {
                    loadingDialog.dismiss();
                    lottieAnimationView.setVisibility(View.VISIBLE);

                    if (snapshot.hasChild("submitted"))
                        databaseReference.child("tests")
                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                .child(id).child("submitted").setValue(0);
                }

                if (snapshot.hasChild("submitted"))
                    submitted = (long) snapshot.child("submitted").getValue();
                ((TextView) findViewById(R.id.mentorTestsDetailsStatus)).setText("Submitted: " + submitted + "/" + getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getInt("studentsCount", 0));

                mentorTestsDetailsAdapter mentorTestsDetailsAdapter = new mentorTestsDetailsAdapter(studentsList, id, title, deadline, marks);
                mentorTestsDetailsRecyclerView.setAdapter(mentorTestsDetailsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}