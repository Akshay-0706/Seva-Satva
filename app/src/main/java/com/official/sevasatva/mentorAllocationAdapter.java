package com.official.sevasatva;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mentorAllocationAdapter extends RecyclerView.Adapter<mentorAllocationAdapter.MyViewHolder> {

    List<mentorAllocationModel> list;
    ViewGroup viewGroup;

    public mentorAllocationAdapter(List<mentorAllocationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public mentorAllocationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mentor_allocation_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mentorAllocationAdapter.MyViewHolder holder, int position) {
        mentorAllocationModel allocationModel = list.get(position);
        holder.mentorAlcName.setText(allocationModel.getName());
        holder.mentorAlcCode.setText(allocationModel.getCode());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mentorAlcName, mentorAlcCode;
        ImageButton mentorAlcGoToButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mentorAlcName = itemView.findViewById(R.id.mentorAlcName);
            mentorAlcCode = itemView.findViewById(R.id.mentorAlcCode);
            mentorAlcGoToButton = itemView.findViewById(R.id.mentorAlcGoToButton);

            mentorAlcGoToButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(viewGroup.getContext(), mentorAllocator.class);
                    intent.putExtra("name", list.get(getAdapterPosition()).getName());
                    intent.putExtra("code", list.get(getAdapterPosition()).getCode());
                    viewGroup.getContext().startActivity(intent);
                }
            });
        }
    }
}
