package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class studentTestsDetailsAdapter extends RecyclerView.Adapter<studentTestsDetailsAdapter.MyViewHolder> {

    List<String> documentsLists;
    String studentEmail;
    String context;
    String id;

    public studentTestsDetailsAdapter(List<String> documentsLists, String studentEmail, String context, String id) {
        this.documentsLists = documentsLists;
        this.studentEmail = studentEmail;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public studentTestsDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new studentTestsDetailsAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_student_tests_details_recycler_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull studentTestsDetailsAdapter.MyViewHolder holder, int position) {
        holder.studentTestsDetailsSubmissionTitle.setText(documentsLists.get(position));
    }

    @Override
    public int getItemCount() {
        return documentsLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView studentTestsDetailsSubmissionTitle;
        ImageButton studentTestsDetailsSubmissionDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            studentTestsDetailsSubmissionTitle = itemView.findViewById(R.id.studentTestsDetailsSubmissionTitle);
            studentTestsDetailsSubmissionDelete = itemView.findViewById(R.id.studentTestsDetailsSubmissionDelete);

            if (!context.equals("com.official.sevasatva.studentScreen"))
                studentTestsDetailsSubmissionDelete.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.xtra_download_icon));

            studentTestsDetailsSubmissionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.equals("com.official.sevasatva.studentScreen")) {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("submissions", documentsLists.remove(new String(documentsLists.get(getAdapterPosition()))));


//                        databaseReference.child("tests").child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"))
//                                .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp").replaceAll("\\.", "_"))
//                                .child(timeStamp).setValue(map);

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        storageReference.child("Tests")
                                .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "SV10").replaceAll("\\.", "_"))
                                .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10").replaceAll("\\.", "_"))
                                .child(documentsLists.get(getAdapterPosition())).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        documentsLists.remove(documentsLists.get(getAdapterPosition()));
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("tests")
                                .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                                .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("mentorEmail", "SV10").replaceAll("\\.", "_"))
                                .child(id).child("students")
                                .child((studentEmail).replaceAll("\\.", "_"))
                                .child("submissions").setValue(documentsLists);

                        Toast.makeText(itemView.getContext(), "Document deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(itemView.getContext(), "Download will start soon", Toast.LENGTH_SHORT).show();
                        downloadFile(documentsLists.get(getAdapterPosition()), getExtension(documentsLists.get(getAdapterPosition())));
                    }
                }
            });

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

        private void downloadFile(String fileName, String fileExtension) {
            DownloadManager downloadmanager = (DownloadManager) itemView.getContext().
                    getSystemService(Context.DOWNLOAD_SERVICE);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("Tests").child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "SV10"))
                    .child(itemView.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "SV10")).child(id)
                    .child((studentEmail).replaceAll("\\.", "_"))
                    .child(fileName)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(itemView.getContext(), DIRECTORY_DOWNLOADS, removeExtension(fileName) + fileExtension);

                    downloadmanager.enqueue(request);
                    Toast.makeText(itemView.getContext(), "Download started", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("DOWNLOAD", "onFailure: " + e);
                        }
                    });

        }

//        private String replaceLastDot(String fileName, boolean retrieve) {
//            if (retrieve) {
//                int index = fileName.lastIndexOf('_');
//                if (index >= 0 && index < fileName.length())
//                    return fileName.substring(0, index) + "." + fileName.substring(index + 1, fileName.length());
//                else
//                    return fileName;
//            } else {
//                int index = fileName.lastIndexOf('.');
//                if (index >= 0 && index < fileName.length())
//                    return fileName.substring(0, index) + "_" + fileName.substring(index + 1, fileName.length());
//                else
//                    return fileName;
//            }
//        }
    }

}
