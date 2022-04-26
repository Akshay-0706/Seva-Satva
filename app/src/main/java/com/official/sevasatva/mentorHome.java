package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mentorHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mentorHome extends Fragment {

    RecyclerView mentorHomeRecyclerView;
    List<mentorHomeModel> studentsList = new ArrayList<>();
    final Map<String, Object>[] studentsData = new Map[]{new HashMap<>()};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mentorHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mentorHome.
     */
    // TODO: Rename and change types and number of parameters
    public static mentorHome newInstance(String param1, String param2) {
        mentorHome fragment = new mentorHome();
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
        return inflater.inflate(R.layout.fragment_mentor_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.homeMentorAnsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), mentorHomeAns.class));
            }
        });

//        requireView().setOnTouchListener(new studentHomeOnSwipeTouchListener(getContext()) {
//            @Override
//            public void onSwipeLeft() {
//                startActivity(new Intent(getActivity(), mentorHomeAns.class));
//            }
//        });

        mentorHomeRecyclerView = getView().findViewById(R.id.mentorHomeRecyclerView);
        getStudents();

    }

    private void getStudents() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Courses").document(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean found = false;
                        DocumentSnapshot documentSnapshot = null;
                        Map<String, Object> data = null;
                        if (task.getResult() != null)
                            documentSnapshot = task.getResult();
                        if (documentSnapshot.getData() != null)
                            data = documentSnapshot.getData();

                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (found)
                                break;

                            if (entry.getKey().equals("Mentors")) {
                                for (Map.Entry<String, Object> entry2 : ((Map<String, Object>) entry.getValue()).entrySet()) {
                                    if (found)
                                        break;

                                    for (Map.Entry<String, Object> entry3 : ((Map<String, Object>) entry2.getValue()).entrySet()) {
                                        if (entry3.getKey().equals("students"))
                                            studentsData[0] = (Map<String, Object>) entry3.getValue();

                                        if (entry3.getKey().equals("email") && entry3.getValue().equals(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp"))) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        setStudents();
                    }
                });
    }

    private void setStudents() {
        studentsList.clear();
        getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putInt("studentsCount", studentsData[0].size()).apply();
        ((TextView) getView().findViewById(R.id.mentorHomeCourseStudents)).setText("Students: " + studentsData[0].size());
        ((TextView) getView().findViewById(R.id.mentorHomeCourseName)).setText(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cn", "temp"));
        ((TextView) getView().findViewById(R.id.mentorHomeCourseCode)).setText(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"));
        ((TextView) getView().findViewById(R.id.mentorHomeCourseDesc)).setText(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("desc", "temp"));

        for (Map.Entry<String, Object> entry : studentsData[0].entrySet()) {
            final String data[] = new String[5];
            for (Map.Entry<String, Object> entry2 : ((Map<String, Object>) entry.getValue()).entrySet()) {
                if (entry2.getKey().equals("name"))
                    data[0] = entry2.getValue().toString();
                if (entry2.getKey().equals("uid"))
                    data[1] = entry2.getValue().toString();
                if (entry2.getKey().equals("cls"))
                    data[2] = entry2.getValue().toString();
                if (entry2.getKey().equals("branch"))
                    data[3] = entry2.getValue().toString();
                if (entry2.getKey().equals("image"))
                    data[4] = entry2.getValue().toString();
            }
            mentorHomeModel mentorHomeModel = new mentorHomeModel(data[0], data[1], data[2] + " " + data[3], data[4]);
            studentsList.add(mentorHomeModel);
        }
        mentorHomeAdapter mentorHomeAdapter = new mentorHomeAdapter(studentsList);
        mentorHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mentorHomeRecyclerView.setAdapter(mentorHomeAdapter);

    }
}








