package com.official.sevasatva;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mentorTestsDetailsAdapter extends RecyclerView.Adapter<mentorTestsDetailsAdapter.MyViewHolder> {

    List<mentorTestsDetailsModel> studentsList;
    String id, title, deadline;
    ViewGroup viewGroup;
    String marks;

    public mentorTestsDetailsAdapter(List<mentorTestsDetailsModel> studentsList, String id, String title, String deadline, int marks) {
        this.studentsList = studentsList;
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.marks = String.valueOf(marks);
    }

    @NonNull
    @Override
    public mentorTestsDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mentor_tests_details_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mentorTestsDetailsAdapter.MyViewHolder holder, int position) {
        holder.studentTestsName.setText(studentsList.get(position).studentName);
        holder.studentTestsMarks.setText("Grades: " + studentsList.get(position).getStudentMarks());
        holder.mentorTestsDetailsStatusOn.setText(studentsList.get(position).getStudentStatus());

        if (studentsList.get(position).getStudentStatus().equals("On time")) {
            holder.gradientDrawable.setStroke(4, ContextCompat.getColor(viewGroup.getContext(), R.color.success));
            holder.mentorTestsDetailsStatusOn.setTextColor(ContextCompat.getColor(viewGroup.getContext(), R.color.success));
        } else {
            holder.gradientDrawable.setStroke(4, ContextCompat.getColor(viewGroup.getContext(), R.color.error));
            holder.mentorTestsDetailsStatusOn.setTextColor(ContextCompat.getColor(viewGroup.getContext(), R.color.error));
        }
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mentorTestsDetailsRecyclerLayout;
        GradientDrawable gradientDrawable;
        TextView studentTestsName, studentTestsMarks, mentorTestsDetailsStatusOn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mentorTestsDetailsRecyclerLayout = itemView.findViewById(R.id.mentorTestsDetailsRecyclerLayout);
            gradientDrawable = (GradientDrawable) mentorTestsDetailsRecyclerLayout.getBackground();
            studentTestsName = itemView.findViewById(R.id.studentTestsName);
            studentTestsMarks = itemView.findViewById(R.id.studentTestsMarks);
            mentorTestsDetailsStatusOn = itemView.findViewById(R.id.mentorTestsDetailsStatusOn);

            mentorTestsDetailsRecyclerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), studentTestsDetails.class);
                    intent.putExtra("context", itemView.getContext().getClass().getName());
                    intent.putExtra("id", id);
                    intent.putExtra("title", title);
                    intent.putExtra("deadline", deadline);
                    intent.putExtra("marks", marks);
                    intent.putExtra("studentEmail", studentsList.get(getAdapterPosition()).studentEmail);
                    intent.putExtra("studentName", studentsList.get(getAdapterPosition()).studentName);
                    intent.putExtra("studentBranch", studentsList.get(getAdapterPosition()).studentBranch);
                    intent.putExtra("studentClass", studentsList.get(getAdapterPosition()).studentClass);
                    intent.putExtra("studentUID", studentsList.get(getAdapterPosition()).studentUID);
                    intent.putExtra("studentMarks", studentsList.get(getAdapterPosition()).studentMarks);
                    intent.putExtra("studentStatus", studentsList.get(getAdapterPosition()).studentStatus);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
