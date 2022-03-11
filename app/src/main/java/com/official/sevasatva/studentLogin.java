package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class studentLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_student_login);
    }
}