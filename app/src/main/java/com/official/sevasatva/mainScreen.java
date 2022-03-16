package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.official.sevasatva.databinding.ActivityMainScreenBinding;

public class mainScreen extends AppCompatActivity {

    ActivityMainScreenBinding binding;
    String[] studentData = {"", "", "", "", "", ""};
    String value = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new home());
        setNavTab((LinearLayout) findViewById(R.id.homeTab), (ImageView) findViewById(R.id.homeIcon), (TextView) findViewById(R.id.homeTabText), "Home", R.drawable.xtra_home_icon, R.drawable.xtra_home_icon_selected);

        Bundle extras = getIntent().getExtras();
        studentData = extras.getStringArray("student_data");

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRunStudentDetails", false).apply();

        findViewById(R.id.homeTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNavTab((LinearLayout) findViewById(R.id.homeTab),(ImageView) findViewById(R.id.homeIcon), (TextView) findViewById(R.id.homeTabText), "Home", R.drawable.xtra_home_icon, R.drawable.xtra_home_icon_selected);
                replaceFragment(new home());
                value = "home";
            }
        });
        findViewById(R.id.newsTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNavTab((LinearLayout) findViewById(R.id.newsTab), (ImageView) findViewById(R.id.newsIcon), (TextView) findViewById(R.id.newsTabText), "News", R.drawable.xtra_news_icon, R.drawable.xtra_news_icon_selected);
                replaceFragment(new news());
                value = "news";
            }
        });
        findViewById(R.id.testsTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNavTab((LinearLayout) findViewById(R.id.testsTab), (ImageView) findViewById(R.id.testsIcon), (TextView) findViewById(R.id.testsTabText), "Tests", R.drawable.xtra_tests_icon, R.drawable.xtra_tests_icon_selected);
                replaceFragment(new tests());
                value = "tests";
            }
        });
        findViewById(R.id.proTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNavTab((LinearLayout) findViewById(R.id.proTab), (ImageView) findViewById(R.id.proIcon), (TextView) findViewById(R.id.proTabText), "Profile", R.drawable.xtra_pro_icon, R.drawable.xtra_pro_icon_selected);
                replaceFragment(new profile());
                value = "pro";
            }
        });



    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void setNavTab(LinearLayout linearLayout, ImageView imageView, TextView textView, String text, int drawable, int drawableSelected) {
        Drawable backgrounds1[] = new Drawable[2];
        Drawable backgrounds2[] = new Drawable[2];
        Resources res = getResources();
        backgrounds1[0] = res.getDrawable(R.drawable.main_screen_not_selected_tab);
        backgrounds1[1] = res.getDrawable(R.drawable.main_screen_selected_tab);
        backgrounds2[0] = res.getDrawable(drawable);
        backgrounds2[1] = res.getDrawable(drawableSelected);

        TransitionDrawable transitionDrawable1 = new TransitionDrawable(backgrounds1);
        TransitionDrawable transitionDrawable2 = new TransitionDrawable(backgrounds2);
        transitionDrawable1.setCrossFadeEnabled(true);
        transitionDrawable2.setCrossFadeEnabled(true);
        linearLayout.setBackground(transitionDrawable1);
        transitionDrawable1.startTransition(500);

        imageView.setBackground(transitionDrawable2);
        transitionDrawable2.startTransition(500);

        textView.setText(text);
    }

    private void changeNavTab(LinearLayout linearLayoutNew, ImageView imageViewNew, TextView textViewNew, String text, int drawableNew, int drawableNewSelected) {
        LinearLayout linearLayoutCurrent = null;
        ImageView imageViewCurrent = null;
        TextView textViewCurrent = null;
        int drawableCurrent, drawableCurrentSelected;
        switch (value) {
            case "home":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.homeTab);
                imageViewCurrent = (ImageView) findViewById(R.id.homeIcon);
                textViewCurrent = (TextView) findViewById(R.id.homeTabText);
                drawableCurrent = R.drawable.xtra_home_icon;
                drawableCurrentSelected = R.drawable.xtra_home_icon_selected;
                break;
            case "news":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.newsTab);
                imageViewCurrent = (ImageView) findViewById(R.id.newsIcon);
                textViewCurrent = (TextView) findViewById(R.id.newsTabText);
                drawableCurrent = R.drawable.xtra_news_icon;
                drawableCurrentSelected = R.drawable.xtra_news_icon_selected;
                break;
            case "tests":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.testsTab);
                imageViewCurrent = (ImageView) findViewById(R.id.testsIcon);
                textViewCurrent = (TextView) findViewById(R.id.testsTabText);
                drawableCurrent = R.drawable.xtra_tests_icon;
                drawableCurrentSelected = R.drawable.xtra_tests_icon_selected;
                break;
            case "pro":
                linearLayoutCurrent = (LinearLayout) findViewById(R.id.proTab);
                imageViewCurrent = (ImageView) findViewById(R.id.proIcon);
                textViewCurrent = (TextView) findViewById(R.id.proTabText);
                drawableCurrent = R.drawable.xtra_pro_icon;
                drawableCurrentSelected = R.drawable.xtra_pro_icon_selected;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + value);
        }

        Drawable backgrounds1[] = new Drawable[2];
        Drawable backgrounds2[] = new Drawable[2];
        Resources res = getResources();
        backgrounds1[0] = res.getDrawable(R.drawable.main_screen_selected_tab);
        backgrounds1[1] = res.getDrawable(R.drawable.main_screen_not_selected_tab);
        backgrounds2[0] = res.getDrawable(drawableCurrentSelected);
        backgrounds2[1] = res.getDrawable(drawableCurrent);

        TransitionDrawable transitionDrawable1 = new TransitionDrawable(backgrounds1);
        TransitionDrawable transitionDrawable2 = new TransitionDrawable(backgrounds2);
        transitionDrawable1.setCrossFadeEnabled(true);
        transitionDrawable2.setCrossFadeEnabled(true);

        linearLayoutCurrent.setBackground(transitionDrawable1);
        transitionDrawable1.startTransition(500);

        imageViewCurrent.setBackground(transitionDrawable2);
        transitionDrawable2.startTransition(500);

        textViewCurrent.setText(null);
        setNavTab(linearLayoutNew, imageViewNew, textViewNew, text, drawableNew, drawableNewSelected);
    }
}