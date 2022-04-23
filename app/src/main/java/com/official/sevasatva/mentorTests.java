package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mentorTests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mentorTests extends Fragment {
    Dialog createTestDialog;
    String[] input = {"", ""};
    String[] dateTime = {"", ""};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mentorTests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mentorTests.
     */
    // TODO: Rename and change types and number of parameters
    public static mentorTests newInstance(String param1, String param2) {
        mentorTests fragment = new mentorTests();
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
        return inflater.inflate(R.layout.fragment_mentor_tests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.mentorTestsRecyclerView);
        getTests(recyclerView, getContext());

        view.findViewById(R.id.mentorTestsFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTestInput();
            }
        });
    }

//    private boolean getOnlineStatus(String deadline) {
//        boolean status = false;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
//                response -> {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
////                        String currentDate = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
////                        String currentTime = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), false);
////                        String dateTimeStamp = jsonObject.getInt("hour") + ":" + jsonObject.getInt("minute") + ":" + jsonObject.getInt("seconds")
////                                + " " + jsonObject.getInt("day") + " " + jsonObject.getInt("month") + " " + jsonObject.getInt("year");
//
////                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss a dd MMMM yyyy", Locale.ENGLISH);
////                        Date date = format.parse(dateTimeStamp);
//                        String date = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
//                        String time = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), false);
//                        Toast.makeText(getContext(), time + " " + date, Toast.LENGTH_SHORT).show();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                },
//
//                error -> {
//                    Toast.makeText(getContext(), "Unable to access current date!", Toast.LENGTH_LONG).show();
//                }
//        );
//        return status;
//    }

    public void getTests(RecyclerView recyclerView, Context context) {
        final List<studentTestsModel> testsList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        LinearLayout lottieAnimationView = ((Activity) context).findViewById(R.id.testsAnimation);

        Dialog loadingDialog = new Dialog(context);
        if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        String key = "";
        if (context.getClass().equals(mentorScreen.class))
            key = "email";
        else
            key = "mentorEmail";

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String finalKey = key;
        databaseReference.child("tests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("areMentorsAllocated", true) &&
                        snapshot.hasChild(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))) {
                    lottieAnimationView.setVisibility(View.GONE);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
                            response -> {
                                try {
                                    testsList.clear();
                                    JSONObject jsonObject = new JSONObject(response);
                                    String date = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
                                    String time = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), true);

                                    Date current = new SimpleDateFormat("HH:mm:ss a dd MMMM yyyy", Locale.ENGLISH).parse(time + " " + date);

                                    for (DataSnapshot dataSnapshot : snapshot.child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                            .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(finalKey, "SV10").replaceAll("\\.", "_")).getChildren()) {

                                        if (!dataSnapshot.getKey().equals("comingTest")) {
                                            final String title = dataSnapshot.child("title").getValue(String.class);
                                            final String marks = String.valueOf(dataSnapshot.child("marks").getValue(Long.class));
                                            final String deadline = dataSnapshot.child("deadline").getValue(String.class);
                                            final Map<String, Object> students = (Map<String, Object>) dataSnapshot.child("students").getValue();
                                            final String id = dataSnapshot.getKey();
                                            final String submitted = "Submitted: " + dataSnapshot.child("submitted").getValue(Long.class) + "/" + context.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                                    .getInt("studentsCount", 0);
                                            Date deadlineDate = new SimpleDateFormat("HH:mm a dd MMMM yyyy", Locale.ENGLISH).parse(deadline);
                                            boolean onlineStatus = current.before(deadlineDate);
                                            //                                        testLists.get(getAdapterPosition()).setOnlineStatus(false);
//                                    notifyItemChanged(getAdapterPosition());

                                                studentTestsModel studentTestsModel = new studentTestsModel(title, marks, submitted, deadline, onlineStatus, students, id);
                                            testsList.add(studentTestsModel);

                                            if (context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
                                                loadingDialog.dismiss();
                                                context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeLoading", false).apply();
                                            }
                                        }
                                    }

                                    studentTestsAdapter studentTestsAdapter = new studentTestsAdapter(testsList, context);
                                    recyclerView.setAdapter(studentTestsAdapter);

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
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(stringRequest);


                } else {
                    loadingDialog.dismiss();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getTestInput() {
        createTestDialog = new Dialog(getContext());
        createTestDialog.setContentView(R.layout.fragment_mentor_tests_create_dialog);
        createTestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createTestDialog.setCancelable(false);
        createTestDialog.show();

        TextInputLayout mentorTestsTitle = createTestDialog.findViewById(R.id.mentorTestsTitle);
        TextInputEditText mentorTestsTitleText = createTestDialog.findViewById(R.id.mentorTestsTitleText);
        TextInputLayout mentorTestsMarks = createTestDialog.findViewById(R.id.mentorTestsMarks);
        TextInputEditText mentorTestsMarksText = createTestDialog.findViewById(R.id.mentorTestsMarksText);

        mentorTestsTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input[0] = s.toString();
            }
        });

        mentorTestsMarksText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input[1] = s.toString();
            }
        });

        createTestDialog.findViewById(R.id.mentorTestsDeadlineTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTime();
            }
        });

        createTestDialog.findViewById(R.id.mentorTestsDeadlineDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });

        createTestDialog.findViewById(R.id.mentorTestsCancelText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input[0] = "";
                input[1] = "";
                dateTime[0] = "";
                dateTime[1] = "";
                createTestDialog.dismiss();
            }
        });

        createTestDialog.findViewById(R.id.mentorTestsSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input[0].equals("") && !input[1].equals("") && !dateTime[0].equals("") && !dateTime[1].equals("")) {
                    Toast.makeText(getContext(), "Creating test, please wait...", Toast.LENGTH_SHORT).show();
                    createTestDialog.dismiss();
                    createTest();
                } else
                    Toast.makeText(getContext(), "Please give all inputs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTest() {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> map2 = new HashMap<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        map.put("title", input[0]);
        map.put("deadline", dateTime[0] + " " + dateTime[1]);
        map.put("submitted", 0);
        map.put("marks", Integer.parseInt(input[1]));

        databaseReference.child("tests").child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                .child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_")).child(timeStamp).setValue(map);

        databaseReference.child("tests").child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                .child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = null;
                Map<String, Object> data;
                if (task.getResult() != null)
                    dataSnapshot = task.getResult();
                data = (Map<String, Object>) dataSnapshot.getValue();

                Map<String, Object> comintTest = (Map<String, Object>) data.get("comingTest");

                if (comintTest != null) {
                    String time = (String) comintTest.get("time");
                    String date = (String) comintTest.get("date");

                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a dd MMMM yyyy", Locale.ENGLISH);
                        Date deadline = simpleDateFormat.parse(dateTime[0] + " " + dateTime[1]);
                        Date current = simpleDateFormat.parse(time + " " + date);

                        if (deadline.before(current)) {
                            Toast.makeText(getContext(), "Here", Toast.LENGTH_SHORT).show();
                            map2.put("time", dateTime[0]);
                            map2.put("date", dateTime[1]);

                            databaseReference.child("tests").child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                                    .child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_")).child("comingTest").setValue(map2);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    map2.put("time", dateTime[0]);
                    map2.put("date", dateTime[1]);

                    databaseReference.child("tests").child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                            .child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_")).child("comingTest").setValue(map2);

                }
                Toast.makeText(getContext(), "Test is live!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getDate() {
        int date = Integer.parseInt(new SimpleDateFormat("dd").format(Calendar.getInstance().getTime()));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateTime[1] = dayOfMonth + " " + getDateNTime.getMonth(month + 1) + " " + year;
                ((TextView) createTestDialog.findViewById(R.id.mentorTestsDeadlineDate)).setText(dateTime[1]);
            }
        }, year, month - 1, date);

        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

    }

    private void getTime() {
        int hour = Integer.parseInt(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
        int minute = Integer.parseInt(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 12)
                    dateTime[0] = (hourOfDay == 0 ? "12" : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute) + " am";
                else
                    dateTime[0] = getDateNTime.getTime(hourOfDay + ":" + (minute < 10 ? "0" + minute : minute), 0, false);

                ((TextView) createTestDialog.findViewById(R.id.mentorTestsDeadlineTime)).setText(dateTime[0]);
            }
        }, hour, minute, false);
        timePickerDialog.show();
        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

//        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
//                "OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        if (which == DialogInterface.BUTTON_POSITIVE) {
//                            Toast.makeText(getContext(), "Ok pressed!", Toast.LENGTH_SHORT).show();
//                            ((TextView) getView().findViewById(R.id.mentorTestsDeadlineTime)).setText(dateTime[0] + ",\n" + dateTime[1]);
//                            timePickerDialog.dismiss();
//                        }
//                    }
//                });
    }
}