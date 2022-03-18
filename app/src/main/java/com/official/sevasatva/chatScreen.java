package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class chatScreen extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private final List<chatScreenModel> chatList = new ArrayList<>();
    private chatScreenAdapter chatScreenAdapter;
    private Boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        ImageButton chatBackButton = findViewById(R.id.chatBackButton);
        ImageButton chatSendButton = findViewById(R.id.chatSendButton);
        EditText text = findViewById(R.id.chatEditText);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

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

                    sendMessage(text.getText().toString());
                    text.setText("");
                }
            }
        });


        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))) {
                    chatList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).getChildren()) {

                        final String date = dataSnapshot.child("date").getValue(String.class);
                        final String email = dataSnapshot.child("email").getValue(String.class);
                        final Boolean isStudent = dataSnapshot.child("isStudent").getValue(Boolean.class);
                        final String msg = dataSnapshot.child("msg").getValue(String.class);
                        final String name = dataSnapshot.child("name").getValue(String.class);
                        final String time = dataSnapshot.child("time").getValue(String.class);

                        chatScreenModel chatScreenModel = new chatScreenModel(date, email, isStudent, msg, name, time);
                        chatList.add(chatScreenModel);
                        chatScreenAdapter = new chatScreenAdapter(chatList, chatScreen.this);
                        chatRecyclerView.setAdapter(chatScreenAdapter);
                        chatRecyclerView.scrollToPosition(chatList.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());
        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        map.put("date", currentDate);
        map.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        map.put("isStudent", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isUserStudent", true));
        map.put("msg", message);
        map.put("name", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "temp"));
        map.put("time", currentTime);

        databaseReference.child("messages").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).child(timeStamp).setValue(map);

    }
}