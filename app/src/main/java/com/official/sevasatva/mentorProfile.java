package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mentorProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mentorProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mentorProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mentorProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static mentorProfile newInstance(String param1, String param2) {
        mentorProfile fragment = new mentorProfile();
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
        return inflater.inflate(R.layout.fragment_mentor_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) getView().findViewById(R.id.mentorProName)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("name", "Mentor"));
        ((TextView) getView().findViewById(R.id.mentorProEmail)).setText(getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("email", "Mentor email"));

        view.findViewById(R.id.mentorProFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), profileFeedback.class));
            }
        });

        view.findViewById(R.id.mentorProFeedbackArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), profileFeedback.class));
            }
        });

        view.findViewById(R.id.mentorProLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        view.findViewById(R.id.mentorProLogoutArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        view.findViewById(R.id.proContactOrganizer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGmail();
            }
        });

        view.findViewById(R.id.proContactOrganizerArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGmail();
            }
        });

        view.findViewById(R.id.mentorProAboutUs).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), profileAboutUs.class));
                    }
                }
        );

        view.findViewById(R.id.mentorProAboutUsArrow).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), profileAboutUs.class));
                    }
                }
        );

        view.findViewById(R.id.mentorProShare).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareApp();
                    }
                }
        );

        view.findViewById(R.id.mentorProShareArrow).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareApp();
                    }
                }
        );
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Check out our Seva/Satva app!\nDownload it from https://playstorelink.com");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share this app"));
    }

    private void openGmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"admin.official@sevasatva.in"};//Add multiple recipients here
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");//Added Gmail Package to forcefully open Gmail App
        startActivity(Intent.createChooser(intent, "Send email"));
    }

    private void logout() {
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRunAppIntro", true).apply();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isUserStudent", true).apply();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("firstRealtimeLoading", true).apply();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstLaunch", true).apply();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("areMentorsAllocated", false).apply();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isAdmin", false).apply();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isMentorLoggedIn", false).apply();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("426988667812-meoa78skojkt8d3u0rs5mi9dd4i5nok3.apps.googleusercontent.com") // R.string.default_web_client_id
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        googleSignInClient.signOut();

        startActivity(new Intent(getActivity(), splash.class));
        getActivity().finishAffinity();
    }
}