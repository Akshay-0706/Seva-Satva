package com.official.sevasatva;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class ansNotificationService extends Service {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final boolean[] start = {false};
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start[0] = true;
            }
        }, 2000);

        String mentorEmail = "";
        if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isUserStudent", true))
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "SV10");
        else
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10");

        FirebaseDatabase.getInstance().getReference().child("announcements")
                .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                .child(mentorEmail.replaceAll("\\.", "_"))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        final String title = snapshot.child("title").getValue(String.class);
                        final String desc = snapshot.child("desc").getValue(String.class);

                        if (start[0])
                            new sendNotification(context, title, desc, new Intent(getApplicationContext(), studentHomeAns.class), 2, false);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}