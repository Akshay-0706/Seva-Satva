package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class studentHomeAnsAdapter extends RecyclerView.Adapter<studentHomeAnsAdapter.MyViewHolder> {

    List<studentHomeAnsModel> ansList;
    private final Context context;
    ArrayList<String> attach;

    public studentHomeAnsAdapter(List<studentHomeAnsModel> ansList, Context context) {
        this.ansList = ansList;
        this.context = context;
    }

    @NonNull
    @Override
    public studentHomeAnsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isUserStudent", true) ?
                        R.layout.fragment_student_home_ans_recycler_items :
                        R.layout.fragment_mentor_home_ans_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull studentHomeAnsAdapter.MyViewHolder holder, int position) {

        studentHomeAnsModel list = ansList.get(position);
        attach = list.getAttach();
        holder.ansTitle.setText(list.getTitle());

        if (list.getExpanded()) {
            holder.ansDesc.setVisibility(View.VISIBLE);
            holder.ansDesc.setText(list.getDesc());
        } else holder.ansDesc.setVisibility(View.GONE);

        if (list.getHasAttach() && list.getExpanded())
            holder.hasAttach.setVisibility(View.VISIBLE);
        else
            holder.hasAttach.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return ansList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        final private TextView ansTitle, ansDesc, hasAttach;
        private ImageButton ansMentorDelete = null;
        RecyclerView ansAttachRecyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ansTitle = itemView.findViewById(R.id.ansTitle);
            ansDesc = itemView.findViewById(R.id.ansDesc);
            hasAttach = itemView.findViewById(R.id.ansAttach);
            ansAttachRecyclerView = itemView.findViewById(R.id.mentorHomeAnsAttachRecyclerView);

            if (context.getClass().equals(mentorHomeAns.class))
                ansMentorDelete = itemView.findViewById(R.id.ansMentorDelete);

            ansTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ansList.get(getAdapterPosition()).setExpanded(!ansList.get(getAdapterPosition()).getExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            if (context.getClass().equals(mentorHomeAns.class))
                ansMentorDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("announcements").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10").replaceAll("\\.", "_")).child(ansList.get(getAdapterPosition()).getId()).removeValue();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        if (attach != null)
                            for (int i = 0; i < attach.size(); i++) {
                                storageReference.child("Announcements").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                        .child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10").replaceAll("\\.", "_")).child(ansList.get(getAdapterPosition()).getId())
                                        .child(attach.get(i)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            hasAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ansAttachRecyclerView.getVisibility() == View.VISIBLE) {
                        ansAttachRecyclerView.setVisibility(View.GONE);
                        hasAttach.setText("View attachments");
                    } else if (ansAttachRecyclerView.getVisibility() == View.GONE) {
                        ansAttachRecyclerView.setVisibility(View.VISIBLE);
                        hasAttach.setText("Hide attachments");
                        final List<studentHomeAnsAttachModel> attachLists = new ArrayList<>();

                        for (int i = 0; i < attach.size(); i++) {
                            studentHomeAnsAttachModel ansAttachModel = new studentHomeAnsAttachModel(attach.get(i), ansList.get(getAdapterPosition()).getId());
                            attachLists.add(ansAttachModel);
                        }

                        studentHomeAnsAttachAdapter ansAttachAdapter = new studentHomeAnsAttachAdapter(attachLists, context);
                        ansAttachRecyclerView.setHasFixedSize(true);
                        ansAttachRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        ansAttachRecyclerView.setAdapter(ansAttachAdapter);
                    }
                }
            });

        }
    }
}
