package com.official.sevasatva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class mentorAllocator extends AppCompatActivity {

    String name, code, email = "", pass = "";
    int enrolled;
    int[] mentorsDetails;
    TextView requiredMentors;
    boolean editingAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_allocator);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        code = bundle.getString("code");

        getStudentsEnrolled();
        findViewById(R.id.mentorAlcAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editingAvailable)
                    Toast.makeText(mentorAllocator.this, "Fetching details please wait", Toast.LENGTH_SHORT).show();
                else {
                    if (findViewById(R.id.mentorAlcAddLayout).getVisibility() == View.GONE) {
                        findViewById(R.id.mentorAlcAddLayout).setVisibility(View.VISIBLE);
                        getInput();
                    }
                    else
                        findViewById(R.id.mentorAlcAddLayout).setVisibility(View.GONE);
                }
            }
        });


    }

    private void getStudentsEnrolled() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://script.google.com/macros/s/AKfycbwlaKsBo6JoKSO2Ww6d5359UGEW07uIBOLxYrkiZ0WMw0k5b0c-alh-Ha20SfTRz7zs/exec?action=getCount&cc=" + code,
                this::setDetails, error -> Toast.makeText(mentorAllocator.this, "Unable to get students enrolled details", Toast.LENGTH_SHORT).show());

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void setDetails(String Enrolled) {
        enrolled = Integer.parseInt(Enrolled);

        TextView courseName = findViewById(R.id.mentorAlcCardName);
        TextView courseCode = findViewById(R.id.mentorAlcCardCode);
        TextView studentsEnrolled = findViewById(R.id.mentorAlcCardEnrolled);
        requiredMentors = findViewById(R.id.mentorAlcCardRequired);

        courseName.setText(name);
        courseCode.setText(code);
        studentsEnrolled.setText(Enrolled);
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
        editingAvailable = true;

    }

    private void getInput() {

        TextInputLayout emailLayout = findViewById(R.id.mentorAlcEmailLayout);
        TextInputEditText emailTextInput = findViewById(R.id.mentorAlcEmailText);
        TextInputLayout passLayout = findViewById(R.id.mentorAlcPassLayout);
        TextInputEditText passTextInput = findViewById(R.id.mentorAlcPassText);

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
                Toast.makeText(mentorAllocator.this, "Clicked!", Toast.LENGTH_SHORT).show();
                if (!(email.isEmpty() && pass.isEmpty())) {
                    Toast.makeText(mentorAllocator.this, "Inside!", Toast.LENGTH_SHORT).show();
                    addMentor();
                }
            }
        });
    }

    private void addMentor() {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", email);
        map2.put("cc", code);
        map.put(timeStamp, map2);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(mentorAllocator.this, "Mentor added", Toast.LENGTH_SHORT).show();
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("Courses").document("Mentors").set(map, SetOptions.merge());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: "+e);
                Toast.makeText(mentorAllocator.this, "Unable to add mentor", Toast.LENGTH_SHORT).show();
            }
        });
    }

}