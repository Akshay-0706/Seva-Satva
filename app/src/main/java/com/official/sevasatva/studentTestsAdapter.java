package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class studentTestsAdapter extends RecyclerView.Adapter<studentTestsAdapter.MyViewHolder> {

    List<studentTestsModel> testLists;
    private final Context context;

    public studentTestsAdapter(List<studentTestsModel> testLists, Context context) {
        this.context = context;
        this.testLists = testLists;
    }

    @NonNull
    @Override
    public studentTestsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_student_tests_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull studentTestsAdapter.MyViewHolder holder, int position) {
        studentTestsModel studentTestsModel = testLists.get(position);
        holder.title.setText(studentTestsModel.getTitle());
        holder.marks.setText("Marks: " + studentTestsModel.getMarks());
        holder.deadline.setText("Deadline: " + studentTestsModel.getDeadline());

        if (studentTestsModel.getOnlineStatus())
            holder.testStatus.setAnimation("test_online.json");
        else
            holder.testStatus.setAnimation("test_offline.json");

        if (context.getClass().equals(mentorScreen.class)) {
            {
                holder.submitted.setText(studentTestsModel.getSubmitted());
                holder.submittedText.setVisibility(View.GONE);
            }
        } else {
            holder.submittedText.setVisibility(View.VISIBLE);
            boolean found = false;
            int index = 0;
            if (studentTestsModel.getStudents() != null) {
                Set<String> students = studentTestsModel.getStudents().keySet();
                for (String keys : students) {
                    if (keys.equals(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", "temp").replaceAll("\\.", "_"))) {
                        found = true;
                        break;
                    }
                    index++;
                }
            }
            if (found) {
                String status = "";
                Map<String, Object> studentsDetails = (Map<String, Object>) studentTestsModel.getStudents()
                        .get(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                .getString("email", "temp").replaceAll("\\.", "_"));

                for (Map.Entry<String, Object> entry : studentsDetails.entrySet()) {
                    if (entry.getKey().equals("status")) {
                        status = entry.getValue().toString();
                        break;
                    }
                }
//                Spanned coloredStatus;
                if (status.equals("On time"))
                    holder.submittedText.setTextColor(ContextCompat.getColor(context, R.color.success));
                else
                    holder.submittedText.setTextColor(ContextCompat.getColor(context, R.color.error));

                holder.submittedText.setText(status);
            } else {
                holder.submittedText.setText("Not submitted");
                holder.submittedText.setTextColor(ContextCompat.getColor(context, R.color.error));
            }
        }
    }

    @Override
    public int getItemCount() {
        return testLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout studentTestsRecyclerItemsLayout;
        TextView title, marks, submitted, submittedText, deadline;
        ImageButton studentTestsDelete;
        LottieAnimationView testStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            studentTestsRecyclerItemsLayout = itemView.findViewById(R.id.studentTestsRecyclerItemsLayout);
            title = itemView.findViewById(R.id.studentTestsTitle);
            marks = itemView.findViewById(R.id.studentTestsMarks);
            submitted = itemView.findViewById(R.id.studentTestsStatus);
            submittedText = itemView.findViewById(R.id.studentTestsStatusText);
            deadline = itemView.findViewById(R.id.studentTestsDeadline);
            studentTestsDelete = itemView.findViewById(R.id.studentTestsDelete);
            testStatus = itemView.findViewById(R.id.studentTestsOnline);

            studentTestsDelete.setVisibility(context.getClass().equals(studentScreen.class) ? View.GONE : View.VISIBLE);

            studentTestsRecyclerItemsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.getClass().equals(studentScreen.class)) {
                        Intent intent = new Intent(context, studentTestsDetails.class);
                        intent.putExtra("context", context.getClass().getName());
                        intent.putExtra("title", testLists.get(getAdapterPosition()).getTitle());
                        intent.putExtra("marks", testLists.get(getAdapterPosition()).getMarks());
                        intent.putExtra("deadline", testLists.get(getAdapterPosition()).getDeadline());
                        intent.putExtra("id", testLists.get(getAdapterPosition()).getId());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, mentorTestsDetails.class);
                        intent.putExtra("context", context.getClass().getName());
                        intent.putExtra("title", testLists.get(getAdapterPosition()).getTitle());
                        intent.putExtra("marks", testLists.get(getAdapterPosition()).getMarks());
                        intent.putExtra("deadline", testLists.get(getAdapterPosition()).getDeadline());
                        intent.putExtra("id", testLists.get(getAdapterPosition()).getId());
                        context.startActivity(intent);
                    }
                }
            });

            studentTestsDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog confirmationDialog = new Dialog(context);
                    confirmationDialog.setContentView(R.layout.fragment_confirmation);
                    confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    confirmationDialog.setCancelable(false);
                    confirmationDialog.show();

                    ((TextView) confirmationDialog.findViewById(R.id.confirmInfo)).setText("You are about to delete a test, all the data will be erased!");
                    ((TextView) confirmationDialog.findViewById(R.id.confirmCourse)).setText("Selected test: ");
                    ((TextView) confirmationDialog.findViewById(R.id.confirmCourseCode)).setText(testLists.get(getAdapterPosition()).getTitle());

                    confirmationDialog.findViewById(R.id.confirmNoButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationDialog.dismiss();
                        }
                    });

                    confirmationDialog.findViewById(R.id.confirmYesButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationDialog.dismiss();
                            deleteTest();
                        }
                    });
                }
            });
        }

        private void deleteTest() {
            Toast.makeText(context, "Deleting test, please wait...", Toast.LENGTH_SHORT).show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("tests").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                    .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                   DataSnapshot dataSnapshot = null;
                                                   Map<String, Object> data;
                                                   if (task.getResult() != null)
                                                       dataSnapshot = task.getResult();
                                                   data = (Map<String, Object>) dataSnapshot.getValue();

                                                   if (data != null && !testLists.isEmpty()) {

                                                       Map<String, Object> comingTest = (Map<String, Object>) data.get("comingTest");
                                                       if (comingTest != null) {
                                                           String id = (String) comingTest.get("id");

                                                           if (id.equals(testLists.get(getAdapterPosition()).getId())) {

                                                               databaseReference.child("tests").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                                                                       .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                                                       .child(testLists.get(getAdapterPosition()).getId()).removeValue();

                                                               List<Date> dateList = new ArrayList<>();
                                                               Map<Date, String> ids = new HashMap<>();
                                                               Map<Date, String> deadlines = new HashMap<>();
                                                               for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                                                   if (!dataSnapshot2.getKey().equals("comingTest")) {

                                                                       final String deadline = dataSnapshot2.child("deadline").getValue(String.class);
                                                                       final String id2 = dataSnapshot2.getKey();
                                                                       Date deadlineDate = null, currentDeadlineDate = null;
                                                                       try {
                                                                           deadlineDate = new SimpleDateFormat("hh:mm a dd MMMM yyyy", Locale.ENGLISH).parse(deadline);
                                                                           currentDeadlineDate = new SimpleDateFormat("hh:mm a dd MMMM yyyy", Locale.ENGLISH).parse(testLists.get(getAdapterPosition()).getDeadline());
                                                                           if (deadlineDate.after(currentDeadlineDate)) {

                                                                               dateList.add(deadlineDate);
                                                                               ids.put(deadlineDate, id2);
                                                                               deadlines.put(deadlineDate, deadline);
                                                                           }
                                                                       } catch (ParseException e) {
                                                                           e.printStackTrace();
                                                                       }
                                                                   }
                                                               }

                                                               Collections.sort(dateList, new Comparator<Date>() {
                                                                   public int compare(Date date1, Date date2) {
                                                                       return date1.compareTo(date2);
                                                                   }
                                                               });


                                                               if (!dateList.isEmpty()) {
                                                                   String id3 = ids.get(dateList.get(0));
                                                                   String[] deadline2 = deadlines.get(dateList.get(0)).split(" ");
                                                                   String time = deadline2[0] + " " + deadline2[1];
                                                                   String date = deadline2[2] + " " + deadline2[3] + " " + deadline2[4];
                                                                   Map<String, Object> map = new HashMap<>();
                                                                   map.put("time", time);
                                                                   map.put("date", date);
                                                                   map.put("id", id3);
                                                                   databaseReference.child("tests").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                                                                           .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                                                           .child("comingTest").setValue(map);
                                                               } else {
                                                                   databaseReference.child("tests").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
                                                                           .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                                                           .child("comingTest").removeValue();
                                                               }
                                                           }
                                                       }
                                                   }
                                               }
                                           }
                    );

            if (testLists.get(getAdapterPosition()).getStudents() != null) {

                Set<Map.Entry<String, Object>> students = testLists.get(getAdapterPosition()).getStudents().entrySet();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                for (Map.Entry<String, Object> entry : students) {
                    Map<String, Object> map = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : map.entrySet()) {
                        if (entry2.getKey().equals("documents")) {
                            ArrayList<String> documents = (ArrayList<String>) entry2.getValue();
                            for (String docs : documents) {
                                storageReference.child("Tests").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                        .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
                                        .child(testLists.get(getAdapterPosition()).getId())
                                        .child(entry.getKey())
                                        .child(docs).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        }

                    }
                }
            }
            Toast.makeText(context, "Test deleted", Toast.LENGTH_SHORT).

                    show();
        }
    }
}
