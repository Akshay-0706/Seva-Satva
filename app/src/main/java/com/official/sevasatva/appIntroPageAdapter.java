package com.official.sevasatva;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;

public class appIntroPageAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public appIntroPageAdapter(Context context) {
        this.context = context;
    }

    public String[] sliderAnim = {
            "app_intro_student.json",
            "app_intro_mentor.json"
    };

    public int[] sliderHeadings = {
            R.string.app_intro_st_heading,
            R.string.app_intro_mt_heading
    };

    @Override
    public int getCount() {
        return sliderHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_app_intro_slider_pages, container, false);

        LottieAnimationView sliderImage = (LottieAnimationView) view.findViewById(R.id.appIntroSliderAnim);
        TextView sliderHeading = (TextView) view.findViewById(R.id.appIntroSliderHeading);

        sliderImage.setAnimation(sliderAnim[position]);
        sliderHeading.setText(sliderHeadings[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
