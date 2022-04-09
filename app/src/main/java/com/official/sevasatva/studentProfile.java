package com.official.sevasatva;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link studentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class studentProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public studentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static studentProfile newInstance(String param1, String param2) {
        studentProfile fragment = new studentProfile();
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
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null)
            Picasso.get()
                    .load(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("image", ""))
                    .into((ImageView) getView().findViewById(R.id.proImage));

        ((TextView) getView().findViewById(R.id.studentProName)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("name", "User"));
        ((TextView) getView().findViewById(R.id.studentProEmail)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("email", "User email"));

//        ConstraintLayout proTheme = getView().findViewById(R.id.proTheme);
//        ConstraintLayout proThemeLayout = getView().findViewById(R.id.proThemeLayout);
//        ImageView proSpinner = getView().findViewById(R.id.proSpinner);
//        proThemeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (proTheme.getVisibility() == View.GONE) {
//                    proTheme.setVisibility(View.VISIBLE);
//                    proSpinner.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.xtra_dropup_icon));
//                } else {
//                    proTheme.setVisibility(View.GONE);
//                    proSpinner.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.xtra_dropdown_icon));
//                }
//            }
//        });

        Intent intent = new Intent(getActivity(), chatScreen.class);

        getView().findViewById(R.id.proAskMentorText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        getView().findViewById(R.id.proAskMentorIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });


//        proSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    proTheme.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                proTheme.setVisibility(View.GONE);
//            }
//        });
    }
}