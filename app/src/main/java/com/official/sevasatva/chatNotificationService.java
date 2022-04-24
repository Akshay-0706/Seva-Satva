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

public class chatNotificationService extends Service {

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

        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        final String name = snapshot.child("name").getValue(String.class);
                        final String msg = snapshot.child("msg").getValue(String.class);

                        if (start[0] && getApplicationContext().getClass().equals(chatScreen.class))
                            new sendNotification(context, name, msg, new Intent(getApplicationContext(), chatScreen.class), 1, true);
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