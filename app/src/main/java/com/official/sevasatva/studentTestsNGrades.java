package com.official.sevasatva;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link studentTestsNGrades#newInstance} factory method to
 * create an instance of this fragment.
 */
public class studentTestsNGrades extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public studentTestsNGrades() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tests.
     */
    // TODO: Rename and change types and number of parameters
    public static studentTestsNGrades newInstance(String param1, String param2) {
        studentTestsNGrades fragment = new studentTestsNGrades();
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
        return inflater.inflate(R.layout.fragment_student_tests_n_grades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "Tests", Toast.LENGTH_SHORT).show();
        final int[] current = {0}; // 0 for tests layout and 1 for grades layout.
        ConstraintLayout studentTests = view.findViewById(R.id.studentTestsLayout);
        ConstraintLayout studentGrades = view.findViewById(R.id.studentGradesLayout);
        TextView testsText = view.findViewById(R.id.studentTestsText);
        TextView gradesText = view.findViewById(R.id.studentGradesText);
        replaceFragment(new studentTests());

        studentTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current[0] != 0) {
                    replaceFragment(new studentTests());
                    Toast.makeText(getContext(), "Tests", Toast.LENGTH_SHORT).show();
                    studentTests.setBackground(null);
                    studentGrades.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.student_grades_heading_bg));
                    testsText.setTextColor(getResources().getColor(R.color.black));
                    gradesText.setTextColor(getResources().getColor(R.color.white));
                    current[0] = 0;
                }
            }
        });

        studentGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current[0] != 1) {
                    replaceFragment(new studentGrades());
                    Toast.makeText(getContext(), "Grades", Toast.LENGTH_SHORT).show();
                    studentGrades.setBackground(null);
                    studentTests.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.student_tests_heading_bg));
                    gradesText.setTextColor(getResources().getColor(R.color.black));
                    testsText.setTextColor(getResources().getColor(R.color.white));
                    current[0] = 1;
                }
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        fragmentTransaction.replace(R.id.studentTestsNGradesFrameLayout, fragment);
        fragmentTransaction.commit();
    }

}