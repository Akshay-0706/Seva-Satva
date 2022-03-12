package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.zip.Inflater;

public class appIntro extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout linearLayout;
    TextView[] dots;
    appIntroPageAdapter appIntroPageAdapter;
    TextView nextBtn, prevBtn;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_app_intro);

        viewPager = findViewById(R.id.viewPager);
        linearLayout = findViewById(R.id.dotsLayout);
        nextBtn = findViewById(R.id.appIntroNext);
        prevBtn = findViewById(R.id.appIntroPrev);

        appIntroPageAdapter = new appIntroPageAdapter(this);
        viewPager.setAdapter(appIntroPageAdapter);

        dotsIndicator(0);

        viewPager.addOnPageChangeListener(viewListener);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPage == 1) {
                    Inflater inflater = new Inflater();
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(appIntro.this, R.style.appIntoBottomDrawerTheme);
                    View drawer = LayoutInflater.from(appIntro.this).inflate(
                            R.layout.fragment_app_intro_bottom_drawer,
                            (ConstraintLayout) findViewById(R.id.bottomDrawerLayout)
                    );
                    bottomSheetDialog.setContentView(drawer);
                    bottomSheetDialog.show();
                    bottomSheetDialog.setCancelable(false);
                    drawer.findViewById(R.id.roleContinueBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RadioGroup radioGroup = drawer.findViewById(R.id.radioGroup);
                            int radioCheckID = radioGroup.getCheckedRadioButtonId();
                            RadioButton radioStudent = drawer.findViewById(R.id.radioStudent);
                            RadioButton radioMentor = drawer.findViewById(R.id.radioMentor);
                            bottomSheetDialog.dismiss();

                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                    .putBoolean("isFirstRunAppIntro", false).apply();

                            if (radioCheckID == radioStudent.getId()) {
                                Intent intent = new Intent(appIntro.this, studentLogin.class);
                                appIntro.this.startActivity(intent);
                                finish();
                            }
                            else {
                                Intent intent = new Intent(appIntro.this, mentorLogin.class);
                                appIntro.this.startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                viewPager.setCurrentItem(currentPage + 1);

            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
                nextBtn.setText(R.string.app_intro_next_text);
            }
        });
    }

    public void dotsIndicator(int position) {
        dots = new TextView[2];
        linearLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9679;"));
            dots[i].setTextSize(20);
            dots[i].setTextColor(Color.parseColor("#6D6D6D"));
            linearLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[position].setTextColor(getResources().getColor(R.color.textColor));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            dotsIndicator(position);
            if (position == 0) {
                nextBtn.setText(R.string.app_intro_next_text);
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(false);
                prevBtn.setVisibility(View.INVISIBLE);
            }
            else {
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);
                prevBtn.setVisibility(View.VISIBLE);
                nextBtn.setText(R.string.app_intro_continue_text);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}