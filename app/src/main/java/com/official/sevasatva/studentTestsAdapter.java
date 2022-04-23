package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            holder.submitted.setText(studentTestsModel.getSubmitted());
        } else {
            boolean found = false;
            int index = 0;
            if (studentTestsModel.getStudents() != null) {
                Set<String> students = studentTestsModel.getStudents().keySet();
                for (int i = 0; i < students.size(); i++) {
                    if (students.equals(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", "temp"))) {
                        found = true;
                        index = i;
                        break;
                    }
                }
            }
            if (found) {
                String status = "";
                Map<String, Object> studentsDetails = (Map<String, Object>) studentTestsModel.getStudents().get(index);
                for (Map.Entry<String, Object> entry : studentsDetails.entrySet()) {
                    if (entry.getKey().equals("status")) {
                        status = entry.getValue().toString();
                        break;
                    }
                }
                holder.submitted.setText("Status: " + status);
            } else
                holder.submitted.setText("Status: Not submitted");
        }
    }

    @Override
    public int getItemCount() {
        return testLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout studentTestsRecyclerItemsLayout;
        TextView title, marks, submitted, deadline;
        LottieAnimationView testStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            studentTestsRecyclerItemsLayout = itemView.findViewById(R.id.studentTestsRecyclerItemsLayout);
            title = itemView.findViewById(R.id.studentTestsTitle);
            marks = itemView.findViewById(R.id.studentTestsMarks);
            submitted = itemView.findViewById(R.id.studentTestsStatus);
            deadline = itemView.findViewById(R.id.studentTestsDeadline);
            testStatus = itemView.findViewById(R.id.studentTestsOnline);

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


        }
    }
}
