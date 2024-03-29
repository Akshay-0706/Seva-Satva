package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    internetCheckListener internetCheckListener = new internetCheckListener();
    final List<chatScreenModel> chatList = new ArrayList<>();
    chatScreenAdapter chatScreenAdapter;
    Context chatContext;
    String mentorEmail = "";

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
        setContentView(R.layout.activity_chat_screen);

        ImageButton chatBackButton = findViewById(R.id.chatBackButton);
        ImageButton chatSendButton = findViewById(R.id.chatSendButton);
        EditText text = findViewById(R.id.chatEditText);
        RecyclerView chatRecyclerView = findViewById(R.id.chatRecyclerView);

        chatBackButton.setOnClickListener(v -> onBackPressed());

        chatSendButton.setOnClickListener(v -> {
            if (!text.getText().toString().isEmpty()) {
                final Animation animation = AnimationUtils.loadAnimation(chatScreen.this, R.anim.btn_effect);
                chatSendButton.startAnimation(animation);

                sendMessage(text.getText().toString().trim(), chatScreen.this);
                text.setText("");
            }
        });

        Dialog loadingDialog = new Dialog(this);
        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeChatLoading", true)) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        getChats(chatRecyclerView, this, loadingDialog);

    }

    public void getChats(RecyclerView chatRecyclerView, Context context, Dialog loadingDialog) {
        chatContext = context;

        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout lottieAnimationView = ((Activity) context).findViewById(R.id.chatEmptyAnimation);

        if (context.getClass().equals(mentorScreen.class))
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "email");
        else
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "Anonymous");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String finalMentorEmail = mentorEmail;
        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                if (snapshot.hasChild(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))) {
                    if (snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                            .hasChild(finalMentorEmail.replaceAll("\\.", "_"))) {
                        lottieAnimationView.setVisibility(View.GONE);

                        for (DataSnapshot dataSnapshot : snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(finalMentorEmail.replaceAll("\\.", "_")).getChildren()) {
                            Log.i("Data snapshot check", "Snapshot: " + dataSnapshot);

                            final String date = dataSnapshot.child("date").getValue(String.class);
                            final String email = dataSnapshot.child("email").getValue(String.class);
                            final Boolean isStudent = dataSnapshot.child("isStudent").getValue(Boolean.class);
                            final String msg = dataSnapshot.child("msg").getValue(String.class);
                            final String name = dataSnapshot.child("name").getValue(String.class);
                            final String time = dataSnapshot.child("time").getValue(String.class);
                            final String id = dataSnapshot.getKey();

                            chatScreenModel chatScreenModel = new chatScreenModel(date, email, isStudent, msg, name, time, id);
                            chatList.add(chatScreenModel);

                            if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeChatLoading", true)) {
                                loadingDialog.dismiss();
                                context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeChatLoading", false).apply();
                            }
                        }
                    } else {
                        loadingDialog.dismiss();
                        lottieAnimationView.setVisibility(View.VISIBLE);
                    }
                } else {
                    loadingDialog.dismiss();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }

                if(chatList.isEmpty() && mentorEmail.equals("Anonymous")) {
                    anonymousMessagesInfo("This section are for those students whose mentors are not allocated yet. " +
                            "You can discuss your doubts among other students here. " +
                            "Messages in this section will not be visible to the mentors in future.", context);
                }
                chatScreenAdapter = new chatScreenAdapter(chatList, context, context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("date", "temp"));
                new ItemTouchHelper(simpleCallback).attachToRecyclerView(chatRecyclerView);
                chatRecyclerView.setAdapter(chatScreenAdapter);
                chatRecyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Toast.makeText(chatScreen.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView.findViewById(R.id.receiverLayout).getVisibility() == View.VISIBLE)
                return 0;
            else
                return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
            Log.i("Dir", "onSwiped: " + swipeDir);
            //Remove swiped item from list and notify the RecyclerView
            Dialog confirmationDialog = new Dialog(chatContext);
            confirmationDialog.setContentView(R.layout.fragment_confirmation);
            confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationDialog.setCancelable(false);
            confirmationDialog.show();

            ((TextView) confirmationDialog.findViewById(R.id.confirmInfo)).setText("Delete this message?");
            ((TextView) confirmationDialog.findViewById(R.id.confirmInfo)).setTextSize(16);

            confirmationDialog.findViewById(R.id.confirmCourse).setVisibility(View.INVISIBLE);
            confirmationDialog.findViewById(R.id.confirmCourseCode).setVisibility(View.INVISIBLE);

            confirmationDialog.findViewById(R.id.confirmNoButton).setOnClickListener(v -> {
                confirmationDialog.dismiss();
                chatScreenAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
            });

            confirmationDialog.findViewById(R.id.confirmYesButton).setOnClickListener(v -> {
                confirmationDialog.dismiss();
                Toast.makeText(chatContext, "Message deleted", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference;
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("messages").child(v.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                        .child(mentorEmail.replaceAll("\\.", "_")).child(chatList.get(viewHolder.getAdapterPosition()).getId()).removeValue();
            });
        }
    };

    public void anonymousMessagesInfo(String message, Context context)
    {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        map.put("email", "anonymous.messages@gmail.com");
        map.put("isStudent", false);
        map.put("msg", message);
        map.put("name", "System");
        map.put("date", currentDate);
        map.put("time", currentTime);

        databaseReference.child("messages").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                .child(mentorEmail.replaceAll("\\.", "_")).child(timeStamp).setValue(map);
    }

    public void sendMessage(String message, Context context) {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        String mentorEmail;
        if (context.getClass().equals(mentorScreen.class))
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10");
        else
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "SV10");

        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        map.put("email", context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("email", "mentor@spit.ac.in"));
        map.put("isStudent", context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isUserStudent", true));
        map.put("msg", message);
        map.put("name", context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "Mentor"));
        map.put("date", currentDate);
        map.put("time", currentTime);

        databaseReference.child("messages").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                .child(mentorEmail.replaceAll("\\.", "_")).child(timeStamp).setValue(map);


//        String finalMentorEmail = mentorEmail;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
//                response -> {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        String currentDate = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
//                        String currentTime = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), false);
//                        String dateTimeStamp = jsonObject.getInt("hour") + ":" + jsonObject.getInt("minute") + ":" + jsonObject.getInt("seconds")
//                                + " " + jsonObject.getInt("day") + " " + jsonObject.getInt("month") + " " + jsonObject.getInt("year");
//
//                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd MM yyyy", Locale.ENGLISH);
//                        Date date = format.parse(dateTimeStamp);
//                        long timestamp = date.getTime();
//
//                        map.put("date", currentDate);
//                        map.put("time", currentTime);
//
//                        databaseReference.child("messages").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
//                                .child(finalMentorEmail.replaceAll("\\.", "_")).child(String.valueOf(timestamp)).setValue(map);
//
//                    } catch (JSONException | ParseException e) {
//                        e.printStackTrace();
//                    }
//                },
//
//                error -> {
//                    Toast.makeText(this, "Unable to access current date!", Toast.LENGTH_LONG).show();
//                }
//        );
//
//        int socketTimeOut = 50000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULTI);
//        stringRequest.setRetryPolicy(policy);
//        RequestQueue queue = Volley.newRequestQueue(context);
//        queue.add(stringRequest);

    }
}