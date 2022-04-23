package com.official.sevasatva;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class studentTestsDetails extends AppCompatActivity {

    RecyclerView studentTestsDetailsRecyclerView;

    Map<String, Object> students = new HashMap<>();
    ArrayList<String> documentsList = new ArrayList<>();
    boolean isStudentSide = false;
    int marks;
    Uri fileUri;
    String title, deadline, fileName;
    String context;
    String studentEmail;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tests_details);

        Bundle bundle = getIntent().getExtras();
        context = bundle.getString("context");

        if (context.equals("com.official.sevasatva.studentScreen"))
            isStudentSide = true;

        marks = bundle.getInt("marks");
        deadline = bundle.getString("deadline");

        if (isStudentSide) {
            studentEmail = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp");
            title = bundle.getString("title");
        } else {
            studentEmail = bundle.getString("studentEmail");
            title = bundle.getString("studentName");
            ((ImageButton) findViewById(R.id.studentTestsDetailsAddBtn)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.studentTestsDetailsSubTitle)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.studentTestsDetailsSubTitle)).setText(bundle.getString("studentUID") + " - " +
                    bundle.getString("studentClass") + bundle.getString("studentBranch"));
        }

        id = bundle.getString("id");

        ((TextView) findViewById(R.id.studentTestsDetailsTitle)).setText(title);
        ((TextView) findViewById(R.id.studentTestsDetailsMarks)).setText("Marks: " + marks);
        ((TextView) findViewById(R.id.studentTestsDetailsDeadline)).setText("Deadline: " + deadline);
        studentTestsDetailsRecyclerView = findViewById(R.id.studentTestsDetailsRecyclerView);

        getDocuments();

        findViewById(R.id.studentTestsDetailsAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLauncher.launch(new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT));
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

        String key = "";
        if (context.equals("com.official.sevasatva.studentScreen"))
            key = "mentorEmail";
        else
            key = "email";

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String finalKey = key;
        databaseReference.child("tests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentsList.clear();

                if (snapshot.child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                        .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(finalKey, "temp").replaceAll("\\.", "_")).child(id).child("students")
                        .child(studentEmail.replaceAll("\\.", "_"))
                        .hasChild("submissions")
                ) {
                    lottieAnimationView.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                            .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString(finalKey, "SV10").replaceAll("\\.", "_")).child(id).child("students")
                            .child(studentEmail.replaceAll("\\.", "_"))
                            .child("submissions")
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

                studentTestsDetailsAdapter studentTestsDetailsAdapter = new studentTestsDetailsAdapter(documentsList, studentEmail, context, id);
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
                students.put("Grades", "Not given yet");
                students.put("status", "Remaining");
                if (!documentsList.contains(fileName)) {
                    documentsList.add(fileName);
                    Toast.makeText(studentTestsDetails.this, "Uploading " + fileName, Toast.LENGTH_SHORT).show();
                } else {
                    alreadyContains = true;
                    Toast.makeText(studentTestsDetails.this, "Updating " + fileName, Toast.LENGTH_SHORT).show();
                }
                students.put("submissions", documentsList);

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
                        if (!finalAlreadyContains)
                            Toast.makeText(studentTestsDetails.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(studentTestsDetails.this, "Updated successfully!", Toast.LENGTH_SHORT).show();

                    }
                });

                DatabaseReference databaseReference;
                databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.child("tests").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                        .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "temp").replaceAll("\\.", "_"))
                        .child(id).child("students")
                        .child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                        .setValue(students);
            } else {
                Toast.makeText(studentTestsDetails.this, R.string.error_401, Toast.LENGTH_SHORT).show();
            }
        }
    });

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