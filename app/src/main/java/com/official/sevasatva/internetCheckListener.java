package com.official.sevasatva;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

public class internetCheckListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (internetCheck.isOffline(context)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            View view = LayoutInflater.from(context).inflate(R.layout.fragment_internet_check, null);
//            builder.setView(view);

//            AlertDialog alertDialog = builder.create();
            Dialog internetCheckDialog = new Dialog(context);
            internetCheckDialog.setContentView(R.layout.fragment_internet_check);
            internetCheckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            internetCheckDialog.setCancelable(false);
            internetCheckDialog.show();

//            alertDialog.show();
//            alertDialog.setCancelable(false);
//            alertDialog.getWindow().setGravity(Gravity.CENTER);

            AppCompatButton exit = internetCheckDialog.findViewById(R.id.internetCheckExit);
            AppCompatButton retry = internetCheckDialog.findViewById(R.id.internetCheckRetry);

            if (context.getClass().equals(studentLogin.class))
                retry.setVisibility(View.GONE);

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) context).finishAffinity();
                }
            });

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    internetCheckDialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
