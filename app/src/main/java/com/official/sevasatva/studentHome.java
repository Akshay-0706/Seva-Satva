package com.official.sevasatva;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link studentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class studentHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public studentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static studentHome newInstance(String param1, String param2) {
        studentHome fragment = new studentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager;
        viewPager = getView().findViewById(R.id.studentHomeViewPager);

        studentHomePageAdapter studentHomePageAdapter = new studentHomePageAdapter(getActivity());
        viewPager.setAdapter(studentHomePageAdapter);

        Handler handler = new Handler();
        final int[] currentPage = {viewPager.getCurrentItem()};

        Runnable update = new Runnable() {

            public void run() {
                currentPage[0]++;
                if (currentPage[0] == 3) {
                    currentPage[0] = 0;
                }
                viewPager.setCurrentItem(currentPage[0], true);
            }
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 5000, 5000);

        ((TextView) getView().findViewById(R.id.homeName)).setText(getFirstName());
        ((TextView) getView().findViewById(R.id.homeCourseName)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("cn", "India's top problems"));
        ((TextView) getView().findViewById(R.id.homeCourseCode)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("cc", "SV12"));
        ((TextView) getView().findViewById(R.id.homeCourseDesc)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("desc", "Study of India's top two problems"));
        ((TextView) getView().findViewById(R.id.homeCourseEvalType)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("eval", "Your mentor will set the evaluation type soon."));

        getView().findViewById(R.id.homeAnsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), studentHomeAns.class));
            }

        });

    }

    private String getFirstName() {
        String fullName = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("name", "User");
        int index = fullName.indexOf(' ');
        return fullName.substring(0, index);
    }
}