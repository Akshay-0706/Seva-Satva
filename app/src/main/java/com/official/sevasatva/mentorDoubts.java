package com.official.sevasatva;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mentorDoubts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mentorDoubts extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mentorDoubts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mentorDoubts.
     */
    // TODO: Rename and change types and number of parameters
    public static mentorDoubts newInstance(String param1, String param2) {
        mentorDoubts fragment = new mentorDoubts();
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
        return inflater.inflate(R.layout.fragment_mentor_doubts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton chatSendButton = getView().findViewById(R.id.chatMentorSendButton);
        EditText text = getView().findViewById(R.id.chatMentorEditText);
        RecyclerView chatRecyclerView = getView().findViewById(R.id.chatMentorRecyclerView);
        chatScreen chatScreen = new chatScreen();

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().isEmpty()) {
                    final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_effect);
                    chatSendButton.startAnimation(animation);

                    chatScreen.sendMessage(text.getText().toString().trim(), getActivity());
                    text.setText("");
                }
            }
        });

        chatScreen.initChatScreen(chatRecyclerView, getActivity());
    }
}