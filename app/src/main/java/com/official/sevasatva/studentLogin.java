package com.official.sevasatva;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class studentLogin extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

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

    private void getCategory(boolean isCalledFromBelow) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("news").get().addOnCompleteListener(task -> {
            DataSnapshot dataSnapshot = null;
            if (task.getResult() != null)
                dataSnapshot = task.getResult();
            String category = "All";
            if (dataSnapshot != null)
                category = (String) dataSnapshot.child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).getValue();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("category", category).apply();

            if (!getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("areMentorsAllocated", false) || isCalledFromBelow)
                getMentorDetails();
            else {
                startActivity(new Intent(studentLogin.this, studentScreen.class));
                finish();
            }
        });
    }

    public void signIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("426988667812-meoa78skojkt8d3u0rs5mi9dd4i5nok3.apps.googleusercontent.com") // R.string.default_web_client_id
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        resultLauncher.launch(new Intent(googleSignInClient.getSignInIntent()));
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Log.d(TAG, "authenticate: Connected to google account");
            Intent intent = result.getData();
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                String email = account.getEmail();
                assert email != null;
//                    if (email.contains("@spit.ac.in"))
                    authenticate(account);
//                    else {
//                        googleSignInClient.signOut();
//                        Toast.makeText(studentLogin.this, R.string.error_501, Toast.LENGTH_SHORT).show();
//                        signIn();
//                    }
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        } else {
            Toast.makeText(studentLogin.this, R.string.error_100, Toast.LENGTH_SHORT).show();
            signIn();
        }
    });

    private void authenticate(GoogleSignInAccount account) {
        Log.d(TAG, "authenticate: Begin firebase auth with google account");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(authResult -> {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    sharedPreferences.edit().putString("email", firebaseUser.getEmail()).apply();
                    sharedPreferences.edit().putString("image", firebaseUser.getPhotoUrl().toString()).apply();

                    checkFlags();
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: login failed" + e.getMessage()));
    }

    private void checkFlags() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("flags").get().addOnCompleteListener(
                task -> {
                    String version, startCourse, underMaintenance;

                    DataSnapshot dataSnapshot = task.getResult();
                    version = (String) dataSnapshot.child("version").getValue();
                    startCourse = (boolean) dataSnapshot.child("startCourse").getValue() ? "true" : "false";
                    underMaintenance = (boolean) dataSnapshot.child("underMaintenance").getValue() ? "true" : "false";

                    Dialog alertDialog = new Dialog(studentLogin.this);
                    alertDialog.setContentView(R.layout.fragment_alert);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(false);
                    LottieAnimationView alertDialogAnim = alertDialog.findViewById(R.id.lottieAnimationView);

                    if (startCourse.equals("false")) {
                        alertDialogAnim.setAnimation("course_ended.json");
                        alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                        ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("The course has ended!");
                        ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(studentLogin.this, R.color.white));
                        alertDialog.findViewById(R.id.alertNegative).setBackground(getDrawable(R.drawable.student_profile_feedback_main_btn_bg));
                        alertDialog.show();

                        alertDialog.findViewById(R.id.alertNegative).setOnClickListener(v -> {
                            alertDialog.dismiss();
                            finishAffinity();
                        });
                    } else {
                        if (!version.equals(BuildConfig.VERSION_NAME)) {
                            alertDialogAnim.setAnimation("app_update.json");
                            ((AppCompatButton) alertDialog.findViewById(R.id.alertPositive)).setText("Update");
                            ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("An update is available, please update the app to continue.");
                            alertDialog.show();

                            alertDialog.findViewById(R.id.alertNegative).setOnClickListener(v -> {
                                alertDialog.dismiss();
                                finishAffinity();
                            });

                            alertDialog.findViewById(R.id.alertPositive).setOnClickListener(v -> {
                                alertDialog.dismiss();
                                String urlString = "https://drive.google.com/drive/folders/1HlkMQvH8ErTcIjB4P7Ou6K7yzAVmTZ4C?usp=sharing";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                intent.setPackage("com.google.android.apps.docs");
                                try {
                                    startActivity(intent);
                                    finishAffinity();
                                } catch (ActivityNotFoundException ex) {
                                    // Chrome browser presumably not installed so allow user to choose instead
                                    intent.setPackage(null);
                                    startActivity(intent);
                                }
                            });

                        } else {
                            if (underMaintenance.equals("false")) {
                                if (sharedPreferences.getBoolean("isFirstLaunch", true))
                                    fetchStudentDetails();
                                else
                                    getCategory(false);
                            } else {
                                alertDialogAnim.setAnimation("app_maintenance.json");
                                alertDialog.findViewById(R.id.alertPositive).setVisibility(View.GONE);
                                ((TextView) alertDialog.findViewById(R.id.alertMessage)).setText("App is under maintenance, try again after some time.");
                                ((AppCompatButton) alertDialog.findViewById(R.id.alertNegative)).setTextColor(ContextCompat.getColor(studentLogin.this, R.color.white));
                                alertDialog.findViewById(R.id.alertNegative).setBackground(getDrawable(R.drawable.student_profile_feedback_main_btn_bg));
                                alertDialog.show();

                                alertDialog.findViewById(R.id.alertNegative).setOnClickListener(v -> {
                                    alertDialog.dismiss();
                                    finishAffinity();
                                });
                            }
                        }
                    }
                }
        );
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

            checkExistingEnrollment();
        } catch (
                JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Your data was not found", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Signed in as a guest user", Toast.LENGTH_LONG).show();

            sharedPreferences.edit().putString("uid", "1234567890").apply();
            sharedPreferences.edit().putString("name", firebaseUser.getDisplayName()).apply();
            sharedPreferences.edit().putString("branch", "GUEST").apply();
            sharedPreferences.edit().putString("class", "NEW").apply();
            sharedPreferences.edit().putString("year", "2023").apply();

            checkExistingEnrollment();
        }

    }

    private void checkExistingEnrollment() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("flags").child("areStudentsAllowed").get().addOnCompleteListener(
                task -> {
//                        isEnrollmentStopped = (boolean) task.getResult().getValue();
//                        switchMaterial.setChecked(isEnrollmentStopped);

                    final boolean[] isAllowed = {true};
                    isAllowed[0] = (boolean) task.getResult().getValue();
//                    }
//                }
//        );
//
//            firestore.collection("Courses").document("Flags").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    DocumentSnapshot documentSnapshot = null;
//                    Map<String, Object> data = null;
//                    if (task.getResult() != null)
//                        documentSnapshot = task.getResult();
//                    if (documentSnapshot.getData() != null)
//                        data = documentSnapshot.getData();
//
//                    final boolean[] isAllowed = {true};
//                    isAllowed[0] = (boolean) data.get("areStudentsAllowed");

                    firestore.collection("Courses").document("Students").get().addOnCompleteListener(task1 -> {
                        DocumentSnapshot documentSnapshot = null;
                        Map<String, Object> data2 = null;
                        if (task1.getResult() != null)
                            documentSnapshot = task1.getResult();
                        if (documentSnapshot.getData() != null)
                            data2 = documentSnapshot.getData();

                        String cc = "", cn = "", desc = "";

                        boolean found = false;

                        if (data2 != null)
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

                            getCategory(true);
                        }
                    });

                });

    }

    private void getMentorDetails() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Courses").document(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = null;
                    Map<String, Object> data3 = null;
                    if (task.getResult() != null)
                        documentSnapshot = task.getResult();
                    if (documentSnapshot.getData() != null)
                        data3 = documentSnapshot.getData();

                    String mentorName = "", mentorEmail = "Anonymous";
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
                });
    }

//    private String[] getNameSurname(String email) {
//        String name = email.substring(0, email.indexOf("."));
//        name = name.substring(0, 1).toUpperCase() + name.substring(1);
//        String surname = email.substring(email.indexOf(".") + 1, email.indexOf("@"));
//        surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
//        return new String[]{name, surname};
//    }
}