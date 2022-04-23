package com.official.sevasatva;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class studentLogin extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    SharedPreferences sharedPreferences;

    private static final String TAG = "Google sign in tag";

    boolean isNewStudent = true;

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
        setContentView(R.layout.activity_student_login);

        sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String date = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("date", date).apply();

                        signIn();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                error -> {
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void signIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("426988667812-meoa78skojkt8d3u0rs5mi9dd4i5nok3.apps.googleusercontent.com") // R.string.default_web_client_id
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        resultLauncher.launch(new Intent(googleSignInClient.getSignInIntent()));
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "authenticate: Connected to google account");
                Intent intent = result.getData();
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                    String email = account.getEmail();
                    assert email != null;
                    if (email.contains("@spit.ac.in"))
                        authenticate(account);
                    else {
                        googleSignInClient.signOut();
                        Toast.makeText(studentLogin.this, R.string.error_501, Toast.LENGTH_SHORT).show();
                        signIn();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onActivityResult: " + e.getMessage());
                }
            } else {
                Toast.makeText(studentLogin.this, R.string.error_100, Toast.LENGTH_SHORT).show();
                signIn();
            }
        }
    });

    private void authenticate(GoogleSignInAccount account) {
        Log.d(TAG, "authenticate: Begin firebase auth with google account");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        assert firebaseUser != null;
                        sharedPreferences.edit().putString("email", firebaseUser.getEmail()).apply();
                        sharedPreferences.edit().putString("image", firebaseUser.getPhotoUrl().toString()).apply();

                        if (sharedPreferences.getBoolean("isFirstLaunch", true))
                            fetchStudentDetails();
                        else {
                            if (!getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("areMentorsAllocated", false))
                                getMentorDetails();
                            else {
                                startActivity(new Intent(studentLogin.this, studentScreen.class));
                                finish();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: login failed" + e.getMessage());
                    }
                });

    }

    private void fetchStudentDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbzH90F-mXT__bwGafdJI6pNljuBDKScyc1ncuDkTqVpsXXALWxF4x3-VzfQCXIuUYd6/exec?action=getItems&email=" + sharedPreferences.getString("email", "temp"),
                this::parseItems,

                error -> {
                    Toast.makeText(this, "Unable to access student data!", Toast.LENGTH_LONG).show();
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void parseItems(String jsonResponse) {

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("student_database");
            JSONObject jo = jarray.getJSONObject(0);

            sharedPreferences.edit().putString("uid", jo.getString("uid")).apply();
            sharedPreferences.edit().putString("name", jo.getString("name")).apply();
            sharedPreferences.edit().putString("branch", jo.getString("branch")).apply();
            sharedPreferences.edit().putString("class", jo.getString("class")).apply();
            sharedPreferences.edit().putString("year", jo.getString("year")).apply();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("Courses").document("Flags").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = null;
                    Map<String, Object> data = null;
                    if (task.getResult() != null)
                        documentSnapshot = task.getResult();
                    if (documentSnapshot.getData() != null)
                        data = documentSnapshot.getData();

                    final boolean[] isAllowed = {true};
                    isAllowed[0] = (boolean) data.get("areStudentsAllowed");

                    firestore.collection("Courses").document("Students").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = null;
                            Map<String, Object> data2 = null;
                            if (task.getResult() != null)
                                documentSnapshot = task.getResult();
                            if (documentSnapshot.getData() != null)
                                data2 = documentSnapshot.getData();

                            String cc = "", cn = "", desc = "";

                            boolean found = false;

                            for (Map.Entry<String, Object> entry : data2.entrySet()) {
                                if (found)
                                    break;
                                for (Map.Entry<String, Object> entry2 : ((Map<String, Object>) entry.getValue()).entrySet()) {
                                    switch (entry2.getKey()) {
                                        case "cc":
                                            cc = entry2.getValue().toString();
                                            break;
                                        case "cn":
                                            cn = entry2.getValue().toString();
                                            break;
                                        case "desc":
                                            desc = entry2.getValue().toString();
                                            break;
                                        case "email":
                                            if (entry2.getValue().equals(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp"))) {
                                                isNewStudent = false;
                                                found = true;
                                            }
                                            break;
                                    }
                                }
                            }

                            if (!isAllowed[0] && isNewStudent) {
                                Toast.makeText(studentLogin.this, "Course enrollment has stopped!", Toast.LENGTH_LONG).show();
                                finishAndRemoveTask();
                            } else if (isNewStudent) {
                                startActivity(new Intent(studentLogin.this, studentDetails.class));
                                finish();
                            } else {
                                sharedPreferences.edit().putString("cc", cc).apply();
                                sharedPreferences.edit().putString("cn", cn).apply();
                                sharedPreferences.edit().putString("desc", desc).apply();

                                getMentorDetails();
                            }
                        }
                    });

                }
            });


        } catch (
                JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Your data is not listed in our database, please contact our organizer.", Toast.LENGTH_LONG).show();
            finishAndRemoveTask();
        }

    }

    private void getMentorDetails() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Courses").document(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = null;
                        Map<String, Object> data3 = null;
                        if (task.getResult() != null)
                            documentSnapshot = task.getResult();
                        if (documentSnapshot.getData() != null)
                            data3 = documentSnapshot.getData();

                        String mentorName = "", mentorEmail = "";
                        boolean areMentorsAllocated = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("areMentorsAllocated", false);
                        boolean found2 = false;

                        if (!areMentorsAllocated) {
                            for (Map.Entry<String, Object> entry : data3.entrySet()) {
//                                Toast.makeText(studentLogin.this, entry.getKey().toString(), Toast.LENGTH_SHORT).show();
                                if (entry.getKey().equals("Students")) {
                                    for (Map.Entry<String, Object> entry2 : ((Map<String, Object>) entry.getValue()).entrySet()) {
                                        if (found2)
                                            break;
                                        for (Map.Entry<String, Object> entry3 : ((Map<String, Object>) entry2.getValue()).entrySet())
                                            switch (entry3.getKey()) {
                                                case "mentorName":
                                                    mentorName = entry3.getValue().toString();
                                                    break;
                                                case "mentorEmail":
                                                    mentorEmail = entry3.getValue().toString();
                                                    break;
                                                case "email":
                                                    if (entry3.getValue().equals(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp"))) {
                                                        isNewStudent = false;
                                                        found2 = true;
                                                    }
                                                    break;
                                            }
                                    }
                                }
                                if (entry.getKey().equals("areAllocated"))
                                    areMentorsAllocated = (boolean) entry.getValue();
                            }

                            sharedPreferences.edit().putString("mentorName", mentorName).apply();
                            sharedPreferences.edit().putString("mentorEmail", mentorEmail).apply();
                            sharedPreferences.edit().putBoolean("areMentorsAllocated", areMentorsAllocated).apply();
//                            Toast.makeText(studentLogin.this, "allocated: " + areMentorsAllocated, Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(studentLogin.this, studentScreen.class));
                        finish();
                    }
                });
    }

    private String[] getNameSurname(String email) {
        String name = email.substring(0, email.indexOf("."));
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        String surname = email.substring(email.indexOf(".") + 1, email.indexOf("@"));
        surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
        return new String[]{name, surname};
    }
}