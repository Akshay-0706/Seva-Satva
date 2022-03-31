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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
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


public class mentorLogin extends AppCompatActivity {

    String email = "", pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_mentor_login);


        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isAdmin", false)) {
            startActivity(new Intent(mentorLogin.this, adminScreen.class));
            finish();
        } else if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isMentorLoggedIn", false)) {
            startActivity(new Intent(mentorLogin.this, mentorScreen.class));
            finish();
        } else
            getInput();
    }

    private void getInput() {

        AppCompatButton loginButton = findViewById(R.id.loginButton);
        TextInputLayout emailLayout = findViewById(R.id.emailInput);
        TextInputEditText emailTextInput = findViewById(R.id.emailTextInput);
        TextInputLayout passLayout = findViewById(R.id.passInput);
        TextInputEditText passTextInput = findViewById(R.id.passTextInput);

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(email.isEmpty() && pass.isEmpty())) {
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", email);
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("pass", pass);
                    checkIfAdmin();
                }
            }
        });

    }

    private void checkIfAdmin() {
        Toast.makeText(mentorLogin.this, "Please wait…", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://script.google.com/macros/s/AKfycbyc1hahzcdy0yDIUWp_8wmiPrQSLpvAb2Hf5tve9zvSUvXpMVP3iXTduEkoBURcV76R/exec?email=" +
                        email + "&pass=" + pass, response -> {
            if (response.equals("Accepted")) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(mentorLogin.this, "Welcome back admin!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(mentorLogin.this, adminScreen.class));
                        finish();
                    }
                });

            } else
                getMentor();
        }, error -> Toast.makeText(mentorLogin.this, "Unable to check", Toast.LENGTH_SHORT).show());

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void getMentor() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(mentorLogin.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mentorLogin.this, mentorScreen.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: "+e);
                Toast.makeText(mentorLogin.this, "You are not allocated to any course yet", Toast.LENGTH_SHORT).show();
            }
        });


    }
}