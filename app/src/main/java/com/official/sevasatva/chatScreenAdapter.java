package com.official.sevasatva;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class chatScreenAdapter extends RecyclerView.Adapter<chatScreenAdapter.MyViewHolder> {

    private List<chatScreenModel> chatList;
    private final Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String todaysDate = dateFormat.format(new Date());

    public chatScreenAdapter(List<chatScreenModel> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public chatScreenAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_screen_message_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull chatScreenAdapter.MyViewHolder holder, int position) {

        chatScreenModel list = chatList.get(position);

        chatScreenModel list2 = null;

        if (position > 0)
            list2 = chatList.get(position - 1);

        if (list2 == null || !list.getDate().equals(list2.getDate())) {
            holder.chatDateLayout.setVisibility(View.VISIBLE);
            if (list.getDate().equals(todaysDate))
                holder.chatDate.setText("Today");
            else
                holder.chatDate.setText(list.getDate());
        } else {
            holder.chatDateLayout.setVisibility(View.GONE);
        }

        if (list.getEmail().equals(context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("email", "temp"))) {
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.receiverLayout.setVisibility(View.GONE);

            holder.senderName.setText("You");
            holder.senderMessage.setText(list.getMsg());

            if (list.getEmail().equals("akshay.vhatkar@spit.ac.in")
                    || list.getEmail().equals("divyesh.shah@spit.ac.in")
                    || list.getEmail().equals("meet.shrimankar@spit.ac.in")) {
                holder.senderName.setTextColor(ContextCompat.getColor(context, R.color.chatDeveloperTextColor));
                holder.senderTime.setText("Developer " + Html.fromHtml("&#9679;") + " " + list.getTime());
            } else if (!list.getIsStudent()) {
                holder.senderName.setTextColor(ContextCompat.getColor(context, R.color.chatMentorTextColor));
                holder.senderTime.setText("Mentor " + Html.fromHtml("&#9679;") + " " + list.getTime());
            } else
                holder.senderTime.setText(list.getTime());

        } else {
            holder.senderLayout.setVisibility(View.GONE);
            holder.receiverLayout.setVisibility(View.VISIBLE);

            holder.receiverName.setText(list.getName());
            holder.receiverMessage.setText(list.getMsg());

            if (list.getEmail().equals("akshay.vhatkar@spit.ac.in")
                    || list.getEmail().equals("divyesh.shah@spit.ac.in")
                    || list.getEmail().equals("meet.shrimankar@spit.ac.in")) {
                holder.receiverName.setTextColor(ContextCompat.getColor(context, R.color.chatDeveloperTextColor));
                holder.receiverTime.setText(list.getTime() + " " + Html.fromHtml("&#9679;") + " Developer");
            } else if (!list.getIsStudent()) {
                holder.receiverName.setTextColor(ContextCompat.getColor(context, R.color.chatMentorTextColor));
                holder.receiverTime.setText(list.getTime() + " " + Html.fromHtml("&#9679;") + " Mentor");
            } else
                holder.receiverTime.setText(list.getTime());
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChatList(List<chatScreenModel> chatList) {
        this.chatList = chatList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout chatDateLayout, receiverLayout, senderLayout;
        private TextView chatDate, receiverName, receiverMessage, receiverTime, senderName, senderMessage, senderTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatDateLayout = itemView.findViewById(R.id.chatDateLayout);
            chatDate = itemView.findViewById(R.id.chatDate);

            receiverLayout = itemView.findViewById(R.id.receiverLayout);
            receiverName = itemView.findViewById(R.id.receiverName);
            receiverMessage = itemView.findViewById(R.id.receiverMessage);
            receiverTime = itemView.findViewById(R.id.receiverTime);

            senderLayout = itemView.findViewById(R.id.senderLayout);
            senderName = itemView.findViewById(R.id.senderName);
            senderMessage = itemView.findViewById(R.id.senderMessage);
            senderTime = itemView.findViewById(R.id.senderTime);

        }
    }
}
