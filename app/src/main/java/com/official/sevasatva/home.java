package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class home extends AppCompatActivity {

    String email;
    String name;
    String branch;
    String cls;
    int uid, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        name = extras.getString("name");
        branch = extras.getString("branch");
        cls = extras.getString("cls");
        uid = extras.getInt("uid");
        year = extras.getInt("year");
    }
}