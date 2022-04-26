package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
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
//        Toast.makeText(getContext(), getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
//                .getString("desc", "Course description here"), Toast.LENGTH_SHORT).show();
        ((TextView) getView().findViewById(R.id.homeCourseName)).setText(getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("cn", "Course name here"));
        ((TextView) getView().findViewById(R.id.homeCourseCode)).setText(getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("cc", "Course code here"));
        ((TextView) getView().findViewById(R.id.homeCourseDesc)).setText(getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("desc", "Course description here"));
        ((TextView) getView().findViewById(R.id.homeCourseMentorName)).setText(getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("mentorName", "Not allocated yet"));

        getView().findViewById(R.id.homeAnsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), studentHomeAns.class));
            }
        });

//        requireView().setOnTouchListener(new studentHomeOnSwipeTouchListener(getContext()) {
//            @Override
//            public void onSwipeLeft() {
//                startActivity(new Intent(getActivity(), studentHomeAns.class));
//            }
//        });

        setTestStatus();
    }

    private String getFirstName() {
        String fullName = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "User");
        int index = fullName.indexOf(' ');
        return fullName.substring(0, index);
    }

    private void setTestStatus() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("tests").child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                .child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "temp").replaceAll("\\.", "_"))
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = null;
                Map<String, Object> data;
                if (task.getResult() != null)
                    dataSnapshot = task.getResult();
                data = (Map<String, Object>) dataSnapshot.getValue();

                if (data != null) {

                    Map<String, Object> comingTest = (Map<String, Object>) data.get("comingTest");
                    String time = (String) comingTest.get("time");
                    String date = (String) comingTest.get("date");

                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a dd MMMM yyyy", Locale.ENGLISH);
                        Date deadline = simpleDateFormat.parse(time + " " + date);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
                                response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String currentDate = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
                                        String currentTime = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), true);

                                        Date current = new SimpleDateFormat("HH:mm:ss a dd MMMM yyyy", Locale.ENGLISH).parse(currentTime + " " + currentDate);

                                        if (current.before(deadline))
                                            ((TextView) getView().findViewById(R.id.homeTestsInfo)).setText("Test is scheduled on " + date + " at " + time + ".");
                                        else
                                            ((TextView) getView().findViewById(R.id.homeTestsInfo)).setText(R.string.home_temp_tests_info_text);

                                    } catch (JSONException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                },

                                error -> {
                                    Toast.makeText(getContext(), "Unable to access current date!", Toast.LENGTH_LONG).show();
                                }
                        );

                        int socketTimeOut = 50000;
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        stringRequest.setRetryPolicy(policy);
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        queue.add(stringRequest);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    ((TextView) getView().findViewById(R.id.homeTestsInfo)).setText(R.string.home_temp_tests_info_text);
                }
            }
        });
    }
}