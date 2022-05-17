package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class mentorAllocator extends AppCompatActivity {

    String cn, cc, desc, name = "", email = "", pass = "";
    int enrolled = 0, mentorDetailsIndex = 0, studentDataIndex = 0;
    int[] mentorsDetails;
    boolean addingMentor = false, editingAvailable = false, areAllocated = false, newLaunch = true;
    List<mentorAllocatorModel> mentorList = new ArrayList<>();
    TextView requiredMentors;
    Map<String, Object> studentData = new HashMap<>();
    FirebaseFirestore firebaseFirestore;
    RecyclerView mentorAllocatorRecyclerView;

    internetCheckListener internetCheckListener = new internetCheckListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckListener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_allocator);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mentorAllocatorRecyclerView = findViewById(R.id.mentorAlcDetailsRecyclerView);

        Bundle bundle = getIntent().getExtras();
        cn = bundle.getString("name");
        cc = bundle.getString("code");
        desc = bundle.getString("desc");

        setMentorsItems();

        findViewById(R.id.mentorAlcAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addingMentor)
                    Toast.makeText(mentorAllocator.this, "Adding mentor please wait!", Toast.LENGTH_SHORT).show();
                if (!editingAvailable)
                    Toast.makeText(mentorAllocator.this, "Fetching details please wait", Toast.LENGTH_SHORT).show();
                else if (mentorsDetails.length == 0)
                    Toast.makeText(mentorAllocator.this, "No mentors required!", Toast.LENGTH_SHORT).show();
                else if (areAllocated)
                    Toast.makeText(mentorAllocator.this, "Can't add more mentors!", Toast.LENGTH_SHORT).show();
                else {
                    if (findViewById(R.id.mentorAlcAddLayout).getVisibility() == View.GONE) {
                        findViewById(R.id.mentorAlcAddLayout).setVisibility(View.VISIBLE);
                        getInput();
                    } else
                        findViewById(R.id.mentorAlcAddLayout).setVisibility(View.GONE);
                }
            }
        });
    }

    private void setMentorsItems() {

        firebaseFirestore.collection("Courses").document(cc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = null;
                Map<String, Object> data = null;
                if (task.getResult() != null)
                    documentSnapshot = task.getResult();
                if (documentSnapshot.getData() != null)
                    data = documentSnapshot.getData();

                Map<String, Object> mentorsData = null;
                Map<String, Object> mentorsData2 = null;

                if (data != null) {
                    mentorList.clear();
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        if (entry.getKey().equals("Mentors")) {
                            mentorsData = (Map<String, Object>) entry.getValue();
                            if (mentorsData.size() != 0)
                                mentorDetailsIndex = mentorsData.size() - 1;
                            for (Map.Entry<String, Object> entry2 : mentorsData.entrySet()) {
                                mentorsData2 = (Map<String, Object>) entry2.getValue();

                                String mentorName = "", mentorEmail = "", mentorPass = "", mentorStudentCount = "";

                                for (Map.Entry<String, Object> entry3 :
                                        mentorsData2.entrySet()) {
                                    if (entry3.getKey().equals("name"))
                                        mentorName = entry3.getValue().toString();
                                    else if (entry3.getKey().equals("email"))
                                        mentorEmail = entry3.getValue().toString();
                                    else if (entry3.getKey().equals("pass"))
                                        mentorPass = entry3.getValue().toString();
                                    else if (entry3.getKey().equals("studentCount"))
                                        mentorStudentCount = entry3.getValue().toString();
                                }
                                mentorAllocatorModel allocatorModel = new mentorAllocatorModel(mentorName, "Email: " + mentorEmail, "Password: " + mentorPass, "Students: " + mentorStudentCount, false);
                                mentorList.add(allocatorModel);
                            }
                        }
                    }
                    mentorAllocatorAdapter mentorAllocatorAdapter = new mentorAllocatorAdapter(mentorList);
                    mentorAllocatorRecyclerView.setLayoutManager(new LinearLayoutManager(mentorAllocator.this));
                    mentorAllocatorRecyclerView.setAdapter(mentorAllocatorAdapter);
                }

                if (newLaunch) {
                    if (data != null) {
                        if (data.get("areAllocated") == null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("areAllocated", false);
                            firebaseFirestore.collection("Courses").document(cc).set(map, SetOptions.merge());
                            areAllocated = false;
                        } else
                            areAllocated = (boolean) data.get("areAllocated");
                    }
                    getStudentsEnrolled();
                }
            }
        });

    }

    private void getStudentsEnrolled() {
        newLaunch = false;

        firebaseFirestore.collection("Courses").document(cc).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = null;
                        Map<String, Object> data = null;
                        if (task.getResult() != null)
                            documentSnapshot = task.getResult();
                        if (documentSnapshot.getData() != null)
                            data = documentSnapshot.getData();

                        if (data != null)
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                if (entry.getKey().equals("Students")) {
                                    studentData = (Map<String, Object>) entry.getValue();
                                    break;
                                }
                            }

                        setDetails();

                    }
                });
    }

    private void setDetails() {
        enrolled = studentData.size();

        TextView courseName = findViewById(R.id.mentorAlcCardName);
        TextView courseCode = findViewById(R.id.mentorAlcCardCode);
        TextView studentsEnrolled = findViewById(R.id.mentorAlcCardEnrolled);
        requiredMentors = findViewById(R.id.mentorAlcCardRequired);

        courseName.setText(this.cn);
        courseCode.setText(cc);
        studentsEnrolled.setText(String.valueOf(enrolled));
        getMentors();
    }

    private void getMentors() {
        int n = enrolled;
        boolean acceptable = false;
        int m = 0, s = 9, r = 0;

        if (n != 0)
            if (n < 5) {
                m = 1;
                s = n;
                r = 0;
            } else {
                while (!acceptable) {
                    m = n / s;
                    r = n % s;
                    if (m > r)
                        acceptable = true;
                    else
                        s--;
                }
            }
        else
            s = 0;

        mentorsDetails = new int[m];

        for (int i = 0; i < m; i++) {
            mentorsDetails[i] = s;
            if (r != 0) {
                mentorsDetails[i] += 1;
                r--;
            }
        }

        requiredMentors.setText(String.valueOf(mentorsDetails.length));

        if (mentorDetailsIndex > 0)
            for (int i = 0; i < mentorsDetails[mentorDetailsIndex]; i++)
                studentDataIndex++;

        editingAvailable = true;

    }

    private void getInput() {

        TextInputLayout nameLayout = findViewById(R.id.mentorAlcNameLayout);
        TextInputEditText nameTextInput = findViewById(R.id.mentorAlcNameText);
        TextInputLayout emailLayout = findViewById(R.id.mentorAlcEmailLayout);
        TextInputEditText emailTextInput = findViewById(R.id.mentorAlcEmailText);
        TextInputLayout passLayout = findViewById(R.id.mentorAlcPassLayout);
        TextInputEditText passTextInput = findViewById(R.id.mentorAlcPassText);

        nameTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });

        emailTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();
            }
        });

        passTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pass = s.toString();
            }
        });

        findViewById(R.id.mentorAlcSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(name.isEmpty() && email.isEmpty() && pass.isEmpty())) {
                    addingMentor = true;
                    Toast.makeText(mentorAllocator.this, "Adding mentor...", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.mentorAlcAddLayout).setVisibility(View.GONE);
                    addMentor();
                } else
                    Toast.makeText(mentorAllocator.this, "Please enter details first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMentor() {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", name);
        map2.put("email", email);
        map2.put("cc", cc);
        map2.put("cn", cn);
        map2.put("desc", desc);
        map.put(timeStamp, map2);
        firebaseFirestore.collection("Courses").document("Mentors").set(map, SetOptions.merge());

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(mentorAllocator.this, "Mentor added", Toast.LENGTH_SHORT).show();
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e);
                Toast.makeText(mentorAllocator.this, "Mentor with this email already exits!", Toast.LENGTH_SHORT).show();
            }
        });

        // Create map object for mentor.
        Map<String, Object> mentor = new HashMap<>();
        Map<String, Object> allocatedMentor = new HashMap<>();
        Map<String, Object> allocatedMentorDetails = new HashMap<>();
        // Create sub map object to store data of each student.
        Map<String, Object> allocatedStudents = new HashMap<>();

        // Add mentor details
        allocatedMentorDetails.put("name", name);
        allocatedMentorDetails.put("email", email);
        allocatedMentorDetails.put("pass", pass);
        allocatedMentorDetails.put("students", allocatedStudents);

        // Add student count
        allocatedMentorDetails.put("studentCount", mentorsDetails[mentorDetailsIndex]);

        // Set iterable for studentData map.
        Iterator<Map.Entry<String, Object>> iterator = studentData.entrySet().iterator();
        for (int i = 0; i < studentDataIndex; i++) {
            iterator.next();
        }

        String[] studentEmails = new String[mentorsDetails[mentorDetailsIndex]];
        int studentEmailsIndex = 0;

        // Iterate through the map till the allocated students.
        for (int i = 0; i < mentorsDetails[mentorDetailsIndex]; i++) {
            Map.Entry<String, Object> entry = iterator.next();
            studentDataIndex++;
            Map<String, Object> subStudentData = (Map<String, Object>) entry.getValue();
            subStudentData.put("mentorName", name);
            subStudentData.put("mentorEmail", email);
            studentEmails[studentEmailsIndex++] = "" + subStudentData.get("email");
            allocatedStudents.put(entry.getKey(), entry.getValue());
        }
        String studentEmailsString = Arrays.toString(studentEmails);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwlaKsBo6JoKSO2Ww6d5359UGEW07uIBOLxYrkiZ0WMw0k5b0c-alh-Ha20SfTRz7zs/exec",
                response -> {
                },

                error -> {
                    Toast.makeText(this, "Unable to details in google sheet", Toast.LENGTH_SHORT).show();
                }
        ) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parmas = new HashMap<>();

                parmas.put("action", "alcMentor");
                parmas.put("emails", studentEmailsString.substring(1, studentEmailsString.length() - 1));
                parmas.put("cc", cc);
                parmas.put("mentor", name);

                return parmas;
            }
        };

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

        allocatedMentor.put(String.valueOf(System.currentTimeMillis()).substring(0, 10), allocatedMentorDetails);
        mentor.put("Mentors", allocatedMentor);
        firebaseFirestore.collection("Courses").document(cc).set(mentor, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mentorDetailsIndex++;
                        addingMentor = false;
                        Toast.makeText(mentorAllocator.this, "Mentor added successfully!", Toast.LENGTH_SHORT).show();
                        setMentorsItems();
                    }
                });

        if (mentorDetailsIndex == mentorsDetails.length - 1) {
            Map<String, Object> areAllocatedBoolean = new HashMap<>();
            Map<String, Object> students = new HashMap<>();
            areAllocatedBoolean.put("areAllocated", true);
            students.put("Students", studentData);
            areAllocated = true;
            firebaseFirestore.collection("Courses").document(cc).set(areAllocatedBoolean, SetOptions.merge());
            firebaseFirestore.collection("Courses").document(cc).set(students, SetOptions.merge());
        }

    }

}