package com.official.sevasatva;

import static android.os.Build.BRAND;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static android.os.Build.PRODUCT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class mentorLogin extends AppCompatActivity {

    String email = "", pass = "";
    boolean loginClicked = false;

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
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_mentor_login);

        findViewById(R.id.contactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipient;

                recipient = new String[]{getResources().getString(R.string.about_us_support_email)};

                intent.putExtra(Intent.EXTRA_EMAIL, recipient);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm"); // Added Gmail Package to forcefully open Gmail App
                startActivity(Intent.createChooser(intent, "Send email via"));
            }
        });

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
                    if (!loginClicked) {
                        loginClicked = true;
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", email);
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("pass", pass);
                        checkIfAdmin();
                    } else
                        Toast.makeText(mentorLogin.this, "Logging in, please wait...", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(mentorLogin.this, "Enter credentials first", Toast.LENGTH_SHORT).show();
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
                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sendAlertEmail();
                                } else {
                                    auth.signOut();
                                    auth.signInWithEmailAndPassword(email, pass)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    sendAlertEmail();
                                                }
                                            });
                                }
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("email", email).apply();
                        assert firebaseUser != null;
//                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("image", firebaseUser.getPhotoUrl().toString()).apply();

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("Courses").document("Mentors").get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        boolean found = false;
                                        String name = "", code = "", courseName = "", courseDesc = "";
                                        DocumentSnapshot documentSnapshot = null;
                                        Map<String, Object> data = null;
                                        if (task.getResult() != null)
                                            documentSnapshot = task.getResult();
                                        if (documentSnapshot.getData() != null)
                                            data = documentSnapshot.getData();

                                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                                            if (found)
                                                break;
                                            Map<String, Object> map = (Map<String, Object>) entry.getValue();
                                            for (Map.Entry<String, Object> entry2 : map.entrySet()) {
                                                if (entry2.getKey().equals("name"))
                                                    name = entry2.getValue().toString();
                                                else if (entry2.getKey().equals("cc"))
                                                    code = entry2.getValue().toString();
                                                else if (entry2.getKey().equals("cn"))
                                                    courseName = entry2.getValue().toString();
                                                else if (entry2.getKey().equals("desc"))
                                                    courseDesc = entry2.getValue().toString();
                                                else if (entry2.getValue().equals(email)) {
                                                    found = true;
                                                }
                                            }
                                        }
                                        Toast.makeText(mentorLogin.this, "Log in successful!", Toast.LENGTH_SHORT).show();

                                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("name", name).apply();
                                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("cc", code).apply();
                                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("cn", courseName).apply();
                                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("desc", courseDesc).apply();
                                        startActivity(new Intent(mentorLogin.this, mentorScreen.class));
                                        finish();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mentorLogin.this, "You are not allocated to any course yet!", Toast.LENGTH_SHORT).show();
                loginClicked = false;
            }
        });


    }

    private void sendAlertEmail() {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("akshay0706vhatkar@gmail.com", "qazxcvbnmlp");
            }
        });

        MimeMessage message = new MimeMessage(session);

        try {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("akshay0706vhatkar@gmail.com"));

            message.setSubject("Admin log in detected!");
            message.setText("Someone just logged in as an Admin!\n\nDevice info:\n" +
                    MANUFACTURER + " " + BRAND + " " + PRODUCT + " " + MODEL);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            Toast.makeText(mentorLogin.this, "Welcome back admin!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mentorLogin.this, adminScreen.class));
            finish();

            Log.i("Device", "sendAlertEmail: " + MANUFACTURER);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}