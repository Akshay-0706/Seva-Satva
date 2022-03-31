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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class chatScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        ImageButton chatBackButton = findViewById(R.id.chatBackButton);
        ImageButton chatSendButton = findViewById(R.id.chatSendButton);
        EditText text = findViewById(R.id.chatEditText);
        RecyclerView chatRecyclerView = findViewById(R.id.chatRecyclerView);

        chatBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().isEmpty()) {
                    final Animation animation = AnimationUtils.loadAnimation(chatScreen.this, R.anim.btn_effect);
                    chatSendButton.startAnimation(animation);

                    sendMessage(text.getText().toString().trim(), chatScreen.this);
                    text.setText("");
                }
            }
        });

        initChatScreen(chatRecyclerView, this);

    }

    public void initChatScreen(RecyclerView chatRecyclerView, Context context) {
        final List<chatScreenModel> chatList = new ArrayList<>();
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout lottieAnimationView = ((Activity) context).findViewById(R.id.chatEmptyAnimation);

        Dialog loadingDialog = new Dialog(context);
        if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                if (snapshot.hasChild(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))) {
                    lottieAnimationView.setVisibility(View.GONE);

                    for (DataSnapshot dataSnapshot : snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10")).getChildren()) {

                        final String date = dataSnapshot.child("date").getValue(String.class);
                        final String email = dataSnapshot.child("email").getValue(String.class);
                        final Boolean isStudent = dataSnapshot.child("isStudent").getValue(Boolean.class);
                        final String msg = dataSnapshot.child("msg").getValue(String.class);
                        final String name = dataSnapshot.child("name").getValue(String.class);
                        final String time = dataSnapshot.child("time").getValue(String.class);

                        chatScreenModel chatScreenModel = new chatScreenModel(date, email, isStudent, msg, name, time);
                        chatList.add(chatScreenModel);

                        if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
                            loadingDialog.dismiss();
                            context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeLoading", false).apply();
                        }
                    }
                } else {
                    loadingDialog.dismiss();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }

                chatScreenAdapter chatScreenAdapter = new chatScreenAdapter(chatList, context);
                chatRecyclerView.setAdapter(chatScreenAdapter);
                chatRecyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendMessage(String message, Context context) {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());
        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        map.put("date", currentDate);
        map.put("email", context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("email", "mentor@spit.ac.in"));
        map.put("isStudent", context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isUserStudent", true));
        map.put("msg", message);
        map.put("name", context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "Mentor"));
        map.put("time", currentTime);

        databaseReference.child("messages").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10")).child(timeStamp).setValue(map);

    }
}