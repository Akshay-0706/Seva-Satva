package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class studentHomeAns extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_ans);

        findViewById(R.id.homeAnsBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RecyclerView ansRecyclerView = findViewById(R.id.ansRecyclerView);
        getAnnouncements(ansRecyclerView, this);

    }

    public void getAnnouncements(RecyclerView ansRecyclerView, Context context) {

        final List<studentHomeAnsModel> ansList = new ArrayList<>();
        ansRecyclerView.setHasFixedSize(true);
        ansRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        LinearLayout lottieAnimationView = ((Activity) context).findViewById(R.id.ansEmptyAnimation);

        Dialog loadingDialog = new Dialog(context);
        if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("announcements").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ansList.clear();
                if (snapshot.hasChild(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))) {
                    lottieAnimationView.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10")).getChildren()) {

                        final String title = dataSnapshot.child("title").getValue(String.class);
                        final String desc = dataSnapshot.child("desc").getValue(String.class);
                        final Boolean hasAttach = dataSnapshot.child("hasAttach").getValue(Boolean.class);
                        final ArrayList<String> attach = (ArrayList<String>) dataSnapshot.child("attach").getValue();
                        final String id = dataSnapshot.getKey();

                        studentHomeAnsModel studentHomeAnsModel = new studentHomeAnsModel(title, desc, hasAttach, false, attach, id);
                        ansList.add(studentHomeAnsModel);

                        if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
                            loadingDialog.dismiss();
                            context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeLoading", false).apply();
                        }
                    }
                } else {
                    loadingDialog.dismiss();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }

                studentHomeAnsAdapter studentHomeAnsAdapter = new studentHomeAnsAdapter(ansList, context);
                ansRecyclerView.setAdapter(studentHomeAnsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}