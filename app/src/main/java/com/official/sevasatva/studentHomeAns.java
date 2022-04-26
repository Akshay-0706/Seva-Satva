package com.official.sevasatva;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class studentHomeAns extends AppCompatActivity {

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
        setContentView(R.layout.activity_student_home_ans);

        findViewById(R.id.homeAnsBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        showNotification(this, "Title", "This is the message to display", new Intent(getApplicationContext(), studentHomeAns.class), 1);
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

        String mentorEmail = "";
        if (context.getClass().equals(mentorHomeAns.class))
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10");
        else
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "SV10");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String finalMentorEmail = mentorEmail;
        databaseReference.child("announcements").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ansList.clear();
                if (snapshot.hasChild(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))) {

                    if (snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                            .hasChild(finalMentorEmail.replaceAll("\\.", "_"))) {
                        lottieAnimationView.setVisibility(View.GONE);

                        for (DataSnapshot dataSnapshot : snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(finalMentorEmail.replaceAll("\\.", "_")).getChildren()) {

                            final String title = dataSnapshot.child("title").getValue(String.class);
                            final String desc = dataSnapshot.child("desc").getValue(String.class);
                            final Boolean hasAttach = dataSnapshot.child("hasAttach").getValue(Boolean.class);
                            final ArrayList<String> attach = (ArrayList<String>) dataSnapshot.child("attach").getValue();
                            final String id = dataSnapshot.getKey();

//                        Toast.makeText(context, "Changed!", Toast.LENGTH_SHORT).show();

//                        int reqCode = 1;
//                        Intent intent = new Intent(getApplicationContext(), studentHomeAns.class);
//                        showNotification(context, title, desc, new Intent(getApplicationContext(), studentHomeAns.class), 1);

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

//        databaseReference.child("announcements")
//                .child("SV10")
//                .addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
//
//                final String title = snapshot.child("title").getValue(String.class);
//                final String desc = snapshot.child("desc").getValue(String.class);
//                if (title == null)
//                    Toast.makeText(context, "title", Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
//                new sendNotification(context, title, desc, new Intent(getApplicationContext(), studentHomeAns.class), 1);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                Toast.makeText(context, "Changed", Toast.LENGTH_SHORT).show();
////                showNotification(context, "Title", "This is the message to display", new Intent(getApplicationContext(), studentHomeAns.class), 1);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

//    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
//
//        NotificationManager mNotificationManager;
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
//        Intent ii = new Intent(context.getApplicationContext(), studentHomeAns.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//        bigText.bigText(message);
//        bigText.setBigContentTitle(title);
//
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setSmallIcon(R.drawable.xtra_app_icon);
//        mBuilder.setContentTitle("New announcement added");
//        mBuilder.setContentText("Your mentor has added new announcement, check it now!");
//        mBuilder.setPriority(Notification.PRIORITY_MAX);
//        mBuilder.setStyle(bigText);
//
//        mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "Your_channel_id";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel Announcements",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mNotificationManager.createNotificationChannel(channel);
//            mBuilder.setChannelId(channelId);
//        }
//
//        mNotificationManager.notify(0, mBuilder.build());
//
//        Log.d("showNotification", "showNotification: " + reqCode);
//    }
}