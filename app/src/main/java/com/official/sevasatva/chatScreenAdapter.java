package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class chatScreenAdapter extends RecyclerView.Adapter<chatScreenAdapter.MyViewHolder> {

    private List<chatScreenModel> chatList;
    private final Context context;
    String todaysDate;

    public chatScreenAdapter(List<chatScreenModel> chatList, Context context, String todaysDate) {
        this.chatList = chatList;
        this.context = context;
        this.todaysDate = todaysDate;
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
                holder.chatDate.setText(R.string.chat_today);
            else
                holder.chatDate.setText(list.getDate());
        } else {
            holder.chatDateLayout.setVisibility(View.GONE);
        }

        if (list.getEmail().equals(context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("email", "mentor@spit.ac.in"))) {
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.receiverLayout.setVisibility(View.GONE);

            holder.senderName.setText(R.string.chat_you);
            holder.senderMessage.setText(list.getMsg());

            if (list.getEmail().equals("akshay.vhatkar@spit.ac.in")
                    || list.getEmail().equals("divyesh.shah@spit.ac.in")
                    || list.getEmail().equals("meet.shrimankar@spit.ac.in")) {
                holder.senderName.setTextColor(ContextCompat.getColor(context, R.color.chatDeveloperTextColor));
                holder.senderTime.setText("Developer " + Html.fromHtml("&#9679;") + " " + list.getTime());
            } else if (!list.getIsStudent()) {
                holder.senderName.setTextColor(ContextCompat.getColor(context, R.color.chatMentorTextColor));
                holder.senderTime.setText("Mentor " + Html.fromHtml("&#9679;") + " " + list.getTime());
            } else {
                holder.senderTime.setText(list.getTime());
                holder.senderName.setTextColor(ContextCompat.getColor(context, R.color.textColorLight));
            }

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
            } else {
                holder.receiverTime.setText(list.getTime());
                holder.receiverName.setTextColor(ContextCompat.getColor(context, R.color.textColorLight));
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private boolean longClickActive = false;

        final private ConstraintLayout chatDateLayout, receiverLayout, senderLayout;
        final private TextView chatDate, receiverName, receiverMessage, receiverTime, senderName, senderMessage, senderTime;

        @SuppressLint("ClickableViewAccessibility")
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            String mentorEmail = "";
            if (context.getClass().equals(mentorScreen.class))
                mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "email");
            else
                mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "email");

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

            String finalMentorEmail = mentorEmail;
            senderMessage.setOnTouchListener(new View.OnTouchListener() {

                private static final int COPY_DURATION = 1000;
                private static final int DELETE_DURATION = 3000;
                private long startClickTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (longClickActive) {
                                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                                if (clickDuration >= DELETE_DURATION) {
                                    DatabaseReference databaseReference;
                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("messages").child(v.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                            .child(finalMentorEmail.replaceAll("\\.", "_")).child(chatList.get(getAdapterPosition()).getId()).removeValue();
                                    Toast.makeText(v.getContext(), "Message deleted successfully!", Toast.LENGTH_SHORT).show();
                                } else if (clickDuration >= COPY_DURATION) {
                                    ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copied", senderMessage.getText().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(v.getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            longClickActive = false;
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (!longClickActive) {
                                longClickActive = true;
                                startClickTime = Calendar.getInstance().getTimeInMillis();
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                    return true;
                }

            });

            receiverMessage.setOnTouchListener(new View.OnTouchListener() {
                private static final int COPY_DURATION = 1000;
                private long startClickTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (longClickActive) {
                                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                                if (clickDuration >= COPY_DURATION) {
                                    ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copied", receiverMessage.getText().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(v.getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            longClickActive = false;
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (!longClickActive) {
                                longClickActive = true;
                                startClickTime = Calendar.getInstance().getTimeInMillis();
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                    return true;
                }
            });
        }
    }
}
