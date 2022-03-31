package com.official.sevasatva;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class mentorHomeAns extends AppCompatActivity {

    String fileName;
    Uri fileUri;
    ListView attachLists;
    Boolean newAnsAvailable = true;
    ArrayList<String> attachments = new ArrayList<>();
    ArrayList<Uri> attachUris = new ArrayList<>();
    ConstraintLayout createAnsLayout;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_home_ans);

        attachLists = findViewById(R.id.mentorHomeAnsAttachLists);
        RecyclerView ansMentorRecyclerView = findViewById(R.id.ansMentorRecyclerView);
        studentHomeAns studentHomeAns = new studentHomeAns();
        studentHomeAns.getAnnouncements(ansMentorRecyclerView, this);

        findViewById(R.id.homeMentorAnsBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createAnsLayout = findViewById(R.id.mentorAlcAddLayout);

        findViewById(R.id.mentorAlcAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newAnsAvailable)
                    createAnsLayout.setVisibility(View.VISIBLE);
                else
                    Toast.makeText(mentorHomeAns.this, "Creating previous announcement, please wait", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.mentorHomeAnsSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAnsAvailable = false;
                Toast.makeText(mentorHomeAns.this, "Creating announcement", Toast.LENGTH_SHORT).show();
                createAnsLayout.setVisibility(View.GONE);
                findViewById(R.id.mentorHomeAnsAttachListsText).setVisibility(View.GONE);
                attachLists.setVisibility(View.GONE);
                if (attachments.size() == 0)
                    sendAnnouncement("My ans", "Welcome to this course", false);
                else
                    sendAnnouncement("My ans", "Welcome to this course", true);
            }
        });

        findViewById(R.id.mentorHomeAnsCancelText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAnsLayout.setVisibility(View.GONE);
                findViewById(R.id.mentorHomeAnsAttachListsText).setVisibility(View.GONE);
                attachLists.setVisibility(View.GONE);
                attachments.clear();
                attachUris.clear();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        mentorHomeAns.this,
                        R.layout.fragment_mentor_home_ans_attach_list_items,
                        attachments);
                attachLists.setAdapter(arrayAdapter);
                newAnsAvailable = true;
            }
        });

        findViewById(R.id.mentorHomeAddAttachBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLauncher.launch(new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT));
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

                findViewById(R.id.mentorHomeAnsAttachListsText).setVisibility(View.VISIBLE);
                attachments.add(fileName);
                attachUris.add(fileUri);
                attachLists.setVisibility(View.VISIBLE);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        mentorHomeAns.this,
                        R.layout.fragment_mentor_home_ans_attach_list_items,
                        attachments);
                attachLists.setAdapter(arrayAdapter);


            } else {
                Toast.makeText(mentorHomeAns.this, R.string.error_401, Toast.LENGTH_SHORT).show();
            }
        }
    });

    private void sendFiles(String timeStamp) {
        Log.d("FILES", "sendFiles: " + attachments);
        for (int i = 0; i < attachments.size(); i++) {
            firebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
            storageReference = firebaseStorage.getReference().child("Announcements").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10")).child(timeStamp).child(attachments.get(i));
            storageReference.putFile(attachUris.get(i)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                }
            });
        }
        attachments.clear();
        attachUris.clear();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                mentorHomeAns.this,
                R.layout.fragment_mentor_home_ans_attach_list_items,
                attachments);
        attachLists.setAdapter(arrayAdapter);
        Toast.makeText(mentorHomeAns.this, "Announcement created", Toast.LENGTH_SHORT).show();
        newAnsAvailable = true;
    }

    private void sendAnnouncement(String title, String desc, Boolean hasAttachments) {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        map.put("title", title);
        map.put("desc", desc);
        map.put("hasAttach", hasAttachments);
        map.put("attach", attachments);
        Log.d(TAG, "sendAnnouncement: " + map);

        databaseReference.child("announcements").child(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10")).child(timeStamp).setValue(map);
        sendFiles(timeStamp);
    }
}