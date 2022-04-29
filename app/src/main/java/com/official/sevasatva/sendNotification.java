package com.official.sevasatva;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class sendNotification {
    sendNotification(Context context, String title, String message, Intent intent, int reqCode) {

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), intent.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, ii, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(title);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.xtra_app_icon);
        if (context.getClass().equals(serviceChatNotify.class))
            mBuilder.setContentTitle("New message");
        else if (context.getClass().equals(serviceAnsNotify.class))
            mBuilder.setContentTitle("New announcement added");
        else
            mBuilder.setContentTitle("New tests created");

        if (context.getClass().equals(serviceChatNotify.class))
            mBuilder.setContentText("Someone just asked doubt, check it now!");
        else if (context.getClass().equals(serviceAnsNotify.class))
            mBuilder.setContentText("Your mentor has added new announcement, check it now!");
        else
            mBuilder.setContentText("Your mentor has created a test, check it now!");

        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId;
            String channerName;
            if (context.getClass().equals(serviceChatNotify.class)) {
                channerName = "Messages";
                channelId = "seva_satva_messages";
            } else if (context.getClass().equals(serviceAnsNotify.class)) {
                channerName = "Announcements";
                channelId = "seva_satva_announcements";
            } else {
                channerName = "Tests";
                channelId = "seva_satva_tests";
            }
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channerName,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());

        Log.d("showNotification", "showNotification: " + reqCode);
    }
}
