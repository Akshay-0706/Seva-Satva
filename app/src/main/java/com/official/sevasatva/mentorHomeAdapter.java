package com.official.sevasatva;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class mentorHomeAdapter extends RecyclerView.Adapter<mentorHomeAdapter.MyViewHolder> {
    List<mentorHomeModel> studentsList;

    public mentorHomeAdapter(List<mentorHomeModel> studentsList) {
        this.studentsList = studentsList;
    }

    @NonNull
    @Override
    public mentorHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mentor_home_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mentorHomeAdapter.MyViewHolder holder, int position) {
        mentorHomeModel mentorHomeModel = studentsList.get(position);
        holder.name.setText(mentorHomeModel.getName());
        holder.uid.setText(mentorHomeModel.getUid());
        holder.yearNBranch.setText(mentorHomeModel.getYearNBranch());
        Picasso.get().load(Uri.parse(mentorHomeModel.getImage())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, uid, yearNBranch;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mentorHomeStudentName);
            uid = itemView.findViewById(R.id.mentorHomeStudentUID);
            yearNBranch = itemView.findViewById(R.id.mentorHomeStudentYearNBranch);
            image = itemView.findViewById(R.id.mentorHomeStudentImage);
        }
    }
}
