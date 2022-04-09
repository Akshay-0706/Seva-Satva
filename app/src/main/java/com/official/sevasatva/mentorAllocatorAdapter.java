package com.official.sevasatva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mentorAllocatorAdapter extends RecyclerView.Adapter<mentorAllocatorAdapter.MyViewHolder> {

    List<mentorAllocatorModel> mentorList;
    ViewGroup viewGroup;

    public mentorAllocatorAdapter(List<mentorAllocatorModel> mentorList) {
        this.mentorList = mentorList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mentor_allocator_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        mentorAllocatorModel allocatorModel = mentorList.get(position);
        holder.name.setText(allocatorModel.getName());
        holder.email.setText(allocatorModel.getEmail());
        holder.pass.setText(allocatorModel.getPass());
        holder.studentCount.setText(allocatorModel.getStudentCount());

        if (allocatorModel.getExpanded()) {
            holder.email.setVisibility(View.VISIBLE);
            holder.pass.setVisibility(View.VISIBLE);
            holder.mentorAllocatorEditBtn.setVisibility(View.VISIBLE);
        } else {
            holder.email.setVisibility(View.GONE);
            holder.pass.setVisibility(View.GONE);
            holder.mentorAllocatorEditBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mentorList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mentorAllocatorLayout;
        TextView name, email, pass, studentCount;
        ImageButton mentorAllocatorEditBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mentorAllocatorLayout = itemView.findViewById(R.id.mentorAllocatorLayout);
            name = itemView.findViewById(R.id.mentorAllocatorName);
            email = itemView.findViewById(R.id.mentorAllocatorEmail);
            pass = itemView.findViewById(R.id.mentorAllocatorPass);
            studentCount = itemView.findViewById(R.id.mentorAllocatorStudentCount);
            mentorAllocatorEditBtn = itemView.findViewById(R.id.mentorAllocatorEditBtn);

            mentorAllocatorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mentorList.get(getAdapterPosition()).setExpanded(!mentorList.get(getAdapterPosition()).getExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
