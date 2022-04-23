package com.official.sevasatva;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mentorTestsDetailsAdapter extends RecyclerView.Adapter<mentorTestsDetailsAdapter.MyViewHolder> {

    List<mentorTestsDetailsModel> studentsList;
    String id, deadline;
    int marks;

    public mentorTestsDetailsAdapter(List<mentorTestsDetailsModel> studentsList, String id, String deadline, int marks) {
        this.studentsList = studentsList;
        this.id = id;
        this.deadline = deadline;
        this.marks = marks;
    }

    @NonNull
    @Override
    public mentorTestsDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mentor_tests_details_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mentorTestsDetailsAdapter.MyViewHolder holder, int position) {
        holder.studentTestsName.setText(studentsList.get(position).studentName);
        holder.studentTestsMarks.setText("Marks: " + studentsList.get(position).studentMarks);
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mentorTestsDetailsRecyclerLayout;
        TextView studentTestsName, studentTestsMarks;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mentorTestsDetailsRecyclerLayout = itemView.findViewById(R.id.mentorTestsDetailsRecyclerLayout);
            studentTestsName = itemView.findViewById(R.id.studentTestsName);
            studentTestsMarks = itemView.findViewById(R.id.studentTestsMarks);

            mentorTestsDetailsRecyclerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), studentTestsDetails.class);
                    intent.putExtra("context", itemView.getContext().getClass().getName());
                    intent.putExtra("id", id);
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
