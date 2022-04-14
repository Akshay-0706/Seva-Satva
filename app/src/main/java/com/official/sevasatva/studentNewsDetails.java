package com.official.sevasatva;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class studentNewsDetails extends AppCompatActivity {

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
        setContentView(R.layout.activity_student_news_details);

        String title, desc, image, url;

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        image = getIntent().getStringExtra("image");
        url = getIntent().getStringExtra("url");

        TextView newsTitle = findViewById(R.id.studentNewsDetailsTitle);
        TextView newsDesc = findViewById(R.id.studentNewsDetailsDesc);
        ImageView news = findViewById(R.id.studentNewsDetailsImage);
        Button readNewsBtn = findViewById(R.id.studentNewsDetailsKnowMoreBtn);

        newsTitle.setText(title);
        newsDesc.setText(desc);

        if (image != null)
            Picasso.get().load(image).into(news);
        else
            Picasso.get().load(R.drawable.student_news_default_image).into(news);

        readNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }
}