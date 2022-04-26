package com.official.sevasatva;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class studentTestsDetails extends AppCompatActivity {

    RecyclerView studentTestsDetailsRecyclerView;
    DatabaseReference databaseReference;

    Map<String, Object> students = new HashMap<>();
    ArrayList<String> documentsList = new ArrayList<>();
    boolean isStudentSide = false;
    int marks;
    Uri fileUri;
    String title, deadline, fileName;
    String context;
    String studentEmail;
    String id;
    String key = "";
    String grades = "Not graded yet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tests_details);

        Bundle bundle = getIntent().getExtras();
        context = bundle.getString("context");

        if (context.equals("com.official.sevasatva.studentScreen"))
            isStudentSide = true;

        marks = Integer.parseInt(bundle.getString("marks"));
        deadline = bundle.getString("deadline");

        if (isStudentSide) {
            studentEmail = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp");
            title = bundle.getString("title");
            ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).setEnabled(false);
        } else {
            studentEmail = bundle.getString("studentEmail");
            title = bundle.getString("studentName");
            ((ImageButton) findViewById(R.id.studentTestsDetailsAddBtn)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.studentTestsDetailsSubTitle)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.studentTestsDetailsSubTitle)).setText(bundle.getString("studentUID") + " - " +
                    bundle.getString("studentClass") + bundle.getString("studentBranch"));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        id = bundle.getString("id");

        if (context.equals("com.official.sevasatva.studentScreen"))
            key = "mentorEmail";
        else
            key = "email";

        ((TextView) findViewById(R.id.studentTestsDetailsTitle)).setText(title);
        ((TextView) findViewById(R.id.studentTestsDetailsMarks)).setText("Marks: " + marks);
        ((TextView) findViewById(R.id.studentTestsDetailsDeadline)).setText("Deadline: " + deadline);
        studentTestsDetailsRecyclerView = findViewById(R.id.studentTestsDetailsRecyclerView);

        getGradesStatusNSub();
        getDocuments();

        findViewById(R.id.studentTestsDetailsAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grades.equals("Not graded yet"))
                    Toast.makeText(studentTestsDetails.this, "Grading is already done!", Toast.LENGTH_SHORT).show();
                else
                    resultLauncher.launch(new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT));
            }
        });

        findViewById(R.id.studentTestsDetailsMarkedEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).getText().toString().equals("Not graded yet"))
                {
                    ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).setText("");
                    InputMethodManager imm = (InputMethodManager)   getSystemService(studentTestsDetails.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });

        findViewById(R.id.studentTestsDetailsMarkedEditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).getText().toString().isEmpty())) {

                    if (Integer.parseInt(((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).getText().toString()) <=
                            marks) {
                        Toast.makeText(studentTestsDetails.this, "Graded", Toast.LENGTH_SHORT).show();
                        databaseReference.child("tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(key, "temp").replaceAll("\\.", "_"))
                                .child(id).child("students").child(studentEmail.replaceAll("\\.", "_")).child("grades")
                                .setValue(((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).getText().toString());
                    } else {
                        Toast.makeText(studentTestsDetails.this, "Cannot grade more than total marks i.e. " + marks, Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).setText("");
                    }
                } else {
                    Toast.makeText(studentTestsDetails.this, "Not graded", Toast.LENGTH_SHORT).show();
                    databaseReference.child("tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                            .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(key, "temp").replaceAll("\\.", "_"))
                            .child(id).child("students").child(studentEmail.replaceAll("\\.", "_")).child("grades")
                            .setValue("Not graded yet");
                }
            }
        });
    }

    private void getGradesStatusNSub() {
        databaseReference.child("tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(key, "temp").replaceAll("\\.", "_"))
                .child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("students")) {
                    if (snapshot.child("students").hasChild(studentEmail.replaceAll("\\.", "_"))) {
                        grades = snapshot.child("students").child(studentEmail.replaceAll("\\.", "_"))
                                .child("grades").getValue(String.class);
                        ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).setText(grades);

                        String status = snapshot.child("students").child(studentEmail.replaceAll("\\.", "_"))
                                .child("status").getValue(String.class);
                        ((TextView) findViewById(R.id.studentTestsDetailsStatusOn)).setText(status);

                        if (!status.equals("On time"))
                            ((TextView) findViewById(R.id.studentTestsDetailsStatusOn)).setTextColor(ContextCompat.getColor(studentTestsDetails.this, R.color.error));
                        else
                            ((TextView) findViewById(R.id.studentTestsDetailsStatusOn)).setTextColor(ContextCompat.getColor(studentTestsDetails.this, R.color.success));

                        ((TextView) findViewById(R.id.studentTestsDetailsSubOn)).setText(snapshot.child("students").child(studentEmail.replaceAll("\\.", "_"))
                                .child("submissions").getValue(String.class));


                    } else {
                        ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).setText("Not graded yet");
                        ((TextView) findViewById(R.id.studentTestsDetailsStatusOn)).setText("Not done yet");
                        ((TextView) findViewById(R.id.studentTestsDetailsSubOn)).setText("Not submitted yet");
                    }
                } else {
                    ((EditText) findViewById(R.id.studentTestsDetailsMarkedEditText)).setText("Not graded yet");
                    ((TextView) findViewById(R.id.studentTestsDetailsStatusOn)).setText("Not done yet");
                    ((TextView) findViewById(R.id.studentTestsDetailsSubOn)).setText("Not submitted yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(studentTestsDetails.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDocuments() {
        studentTestsDetailsRecyclerView.setHasFixedSize(true);
        studentTestsDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout lottieAnimationView = findViewById(R.id.studentTestsDetailsDocsEmptyAnimation);

        Dialog loadingDialog = new Dialog(this);
        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        String finalKey = key;
        databaseReference.child("tests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentsList.clear();

                if (snapshot.child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                        .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(finalKey, "temp").replaceAll("\\.", "_")).child(id).child("students")
                        .child(studentEmail.replaceAll("\\.", "_"))
                        .hasChild("documents")
                ) {
                    lottieAnimationView.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                            .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(finalKey, "SV10").replaceAll("\\.", "_")).child(id).child("students")
                            .child(studentEmail.replaceAll("\\.", "_"))
                            .child("documents")
                            .getChildren()) {
                        final String fileName = dataSnapshot.getValue(String.class);

                        documentsList.add(fileName);

                        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRealtimeLoading", true)) {
                            loadingDialog.dismiss();
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRealtimeLoading", false).apply();
                        }
                    }
                } else {
                    loadingDialog.dismiss();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }

                studentTestsDetailsAdapter studentTestsDetailsAdapter = new studentTestsDetailsAdapter(documentsList, studentEmail, grades, context, id);
                studentTestsDetailsRecyclerView.setAdapter(studentTestsDetailsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                fileUri = intent.getData();
                Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                try {
                    if (cursor != null && cursor.moveToFirst())
                        fileName = cursor.getString(nameIndex);
                } finally {
                    cursor.close();
                }
                if (fileName == null) {
                    fileName = fileUri.getPath();
                    int cut = fileName.lastIndexOf('/');
                    if (cut != -1) {
                        fileName = fileName.substring(cut + 1);
                    }
                }

                boolean alreadyContains = false;

                students.put("name", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "temp"));
                students.put("branch", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("branch", "temp"));
                students.put("class", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("class", "temp"));
                students.put("uid", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("uid", "temp"));
                students.put("grades", grades);
                if (!documentsList.contains(fileName)) {
                    documentsList.add(fileName);
                    Toast.makeText(studentTestsDetails.this, "Uploading " + fileName, Toast.LENGTH_SHORT).show();
                } else {
                    alreadyContains = true;
                    Toast.makeText(studentTestsDetails.this, "Updating " + fileName, Toast.LENGTH_SHORT).show();
                }
                students.put("documents", documentsList);

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
                StorageReference storageReference = firebaseStorage.getReference().child("Tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                        .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "temp").replaceAll("\\.", "_"))
                        .child(id)
                        .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                        .child(fileName);
                boolean finalAlreadyContains = alreadyContains;
                storageReference.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
                                response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String date = jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");
                                        String time = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), true);

                                        Date current = new SimpleDateFormat("HH:mm:ss a dd MMMM yyyy", Locale.ENGLISH).parse(time + " " + date);
                                        Date deadlineDate = new SimpleDateFormat("HH:mm a dd MMMM yyyy", Locale.ENGLISH).parse(deadline);

                                        students.put("submissions", time + " " + date);
                                        if (current.before(deadlineDate))
                                            students.put("status", "On time");
                                        else
                                            students.put("status", "Done late by " + getDifference(deadlineDate, current));

                                        databaseReference.child("tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "temp").replaceAll("\\.", "_"))
                                                .child(id).child("students")
                                                .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                                .setValue(students);

//                                        if (firstTime)
//                                            databaseReference.child("tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
//                                                    .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(key, "temp").replaceAll("\\.", "_"))
//                                                    .child(id).child("submitted").setValue(submitted + 1);

                                        if (!finalAlreadyContains)
                                            Toast.makeText(studentTestsDetails.this, "Uploaded " + fileName + " successfully!", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(studentTestsDetails.this, "Updated " + fileName + " successfully!", Toast.LENGTH_SHORT).show();

                                    } catch (JSONException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                },

                                error -> {
                                    Toast.makeText(studentTestsDetails.this, "Error in fetching TimeAPI", Toast.LENGTH_SHORT).show();
                                }
                        );

                        int socketTimeOut = 50000;
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        stringRequest.setRetryPolicy(policy);
                        RequestQueue queue = Volley.newRequestQueue(studentTestsDetails.this);
                        queue.add(stringRequest);

                    }
                });

            } else {
                Toast.makeText(studentTestsDetails.this, R.string.error_401, Toast.LENGTH_SHORT).show();
            }

        }
    });

    public String getDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : "+ endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

//        System.out.printf(
//                "%d days, %d hours, %d minutes, %d seconds%n",
//                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays + " days, " + elapsedHours + " hrs, " + elapsedMinutes + " min & " + elapsedSeconds + " sec";
    }

//    private String replaceLastDot(String fileName, boolean retrieve) {
//        if (retrieve) {
//            int index = fileName.lastIndexOf('_');
//            if (index >= 0 && index < fileName.length())
//                return fileName.substring(0, index) + "." + fileName.substring(index + 1, fileName.length());
//            else
//                return fileName;
//        } else {
//            int index = fileName.lastIndexOf('.');
//            if (index >= 0 && index < fileName.length())
//                return fileName.substring(0, index) + "_" + fileName.substring(index + 1, fileName.length());
//            else
//                return fileName;
//        }
//    }
}