package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class profileAboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_about_us);

        ((TextView) findViewById(R.id.textView19)).setText(BuildConfig.VERSION_NAME);

        findViewById(R.id.supportEmailImage).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmail(false);
                    }
                }
        );

        findViewById(R.id.supportEmailText).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmail(false);
                    }
                }
        );

        findViewById(R.id.developerEmailText).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmail(true);
                    }
                }
        );
    }

    private void sendEmail(boolean toDeveloper) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipient;

        if (toDeveloper)
            recipient = new String[]{"akshay0706vhatkar@gmail.com"};
        else
            recipient = new String[]{getResources().getString(R.string.about_us_support_email)};

        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm"); // Added Gmail Package to forcefully open Gmail App
        startActivity(Intent.createChooser(intent, "Send email via"));
    }
}