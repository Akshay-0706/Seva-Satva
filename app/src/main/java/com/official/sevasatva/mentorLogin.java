package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class mentorLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_mentor_login);
    }
}