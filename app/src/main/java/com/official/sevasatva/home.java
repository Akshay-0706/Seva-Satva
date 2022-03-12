package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        String surname = extras.getString("surname");
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, surname, Toast.LENGTH_SHORT).show();
    }
}