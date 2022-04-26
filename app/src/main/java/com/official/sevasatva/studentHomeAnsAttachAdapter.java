package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class studentHomeAnsAttachAdapter extends RecyclerView.Adapter<studentHomeAnsAttachAdapter.MyAttachViewHolder> {

    List<studentHomeAnsAttachModel> list;
    private final Context context;

    public studentHomeAnsAttachAdapter(List<studentHomeAnsAttachModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public studentHomeAnsAttachAdapter.MyAttachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new studentHomeAnsAttachAdapter.MyAttachViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_student_ans_attach_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull studentHomeAnsAttachAdapter.MyAttachViewHolder holder, int position) {
        holder.attachName.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyAttachViewHolder extends RecyclerView.ViewHolder {

        TextView attachName;
        ImageButton attachButton;

        public MyAttachViewHolder(@NonNull View itemView) {
            super(itemView);

            attachName = itemView.findViewById(R.id.attachName);
            attachButton = itemView.findViewById(R.id.attachButton);

            attachButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Download will start soon", Toast.LENGTH_SHORT).show();
                    downloadFile(list.get(getAdapterPosition()).getTitle(), getExtension(list.get(getAdapterPosition()).getTitle()), list.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    private String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i);
        }
        return extension;
    }

    private String removeExtension(String fileName) {
        String newFileName = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            newFileName = fileName.substring(0, i);
        }
        return newFileName;
    }

    private void downloadFile(String fileName, String fileExtension, String id) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);

        String mentorEmail = "";
        if (context.getClass().equals(mentorScreen.class))
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10");
        else
            mentorEmail = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "SV10");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Announcements").child(context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                .child(mentorEmail.replaceAll("\\.", "_")).child(id).child(fileName)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager.Request request = new DownloadManager.Request(uri);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, removeExtension(fileName) + fileExtension);

                downloadmanager.enqueue(request);
                Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
