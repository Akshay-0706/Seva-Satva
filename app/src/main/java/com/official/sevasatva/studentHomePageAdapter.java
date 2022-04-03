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

public class studentHomePageAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public studentHomePageAdapter(Context context) {
        this.context = context;
    }

    public String[] sliderAnims = {
            "student_home_ans.json",
            "student_home_news.json",
            "student_home_doubts.json"
    };

    public int[] sliderHeadings = {
            R.string.home_page_adapter_ans_heading,
            R.string.home_page_adapter_news_heading,
            R.string.home_page_adapter_doubts_heading
    };

    public int[] sliderTitles = {
            R.string.home_page_adapter_ans_title,
            R.string.home_page_adapter_news_title,
            R.string.home_page_adapter_doubts_title
    };

    public int[] sliderDescs = {
            R.string.home_page_adapter_ans_desc,
            R.string.home_page_adapter_news_desc,
            R.string.home_page_adapter_doubts_desc
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
        View view = layoutInflater.inflate(R.layout.fragment_student_home_slider_pages, container, false);

        LottieAnimationView sliderAnim = view.findViewById(R.id.studentHomeSliderAnim);
        TextView sliderHeading = view.findViewById(R.id.studentHomeSliderHeading);
        TextView sliderTitle = view.findViewById(R.id.studentHomeSliderTitle);
        TextView sliderDesc = view.findViewById(R.id.studentHomeSliderDesc);

        sliderAnim.setAnimation(sliderAnims[position]);
        sliderHeading.setText(sliderHeadings[position]);
        sliderTitle.setText(sliderTitles[position]);
        sliderDesc.setText(sliderDescs[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
