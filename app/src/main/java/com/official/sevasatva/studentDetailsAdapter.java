package com.official.sevasatva;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class studentDetailsAdapter extends RecyclerView.Adapter<studentDetailsAdapter.ViewHolder> {

    private static final String TAG = "Position";
    ArrayList<HashMap<String, String>> list;
    SharedPreferences sharedPreferences;
    String selectedCourseCode, selectedCourseName;

    int previousExpanded = -1;
    ViewGroup viewGroup;

    public studentDetailsAdapter(ArrayList<HashMap<String, String>> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_student_details_recycler_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> hashMap = list.get(position);
        holder.courseCode.setText(hashMap.get("code"));
        holder.courseName.setText(hashMap.get("name"));
        holder.courseDescription.setText(hashMap.get("desc"));
        String isExpanded = hashMap.get("isExpanded");
        assert isExpanded != null;
        holder.courseLayoutDescription.setVisibility(isExpanded.equals("true") ? View.VISIBLE : View.GONE);

        if (isExpanded.equals("true"))
            previousExpanded = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filteredList(ArrayList<HashMap<String, String>> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Dialog loadingDialog = new Dialog(viewGroup.getContext());
        ConstraintLayout courseLayoutDescription;
        ConstraintLayout constraintLayout;
        TextView courseName, courseCode, courseDescription;
        AppCompatButton courseSelectionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseName);
            courseCode = itemView.findViewById(R.id.courseCode);
            courseDescription = itemView.findViewById(R.id.courseDesc);
            courseLayoutDescription = itemView.findViewById(R.id.courseLayoutDesc);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            courseSelectionButton = itemView.findViewById(R.id.courseSelectBtn);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> hashMap = list.get(getAdapterPosition());

                    String isExpanded = hashMap.get("isExpanded");
                    assert isExpanded != null;
                    if (isExpanded.equals("false"))
                        hashMap.put("isExpanded", "true");
                    else
                        hashMap.put("isExpanded", "false");
                    notifyItemChanged(getAdapterPosition());

                    if (previousExpanded == getAdapterPosition())
                        previousExpanded = -1;

                    HashMap<String, String> hashMap2 = null;
                    if (previousExpanded != -1 && list.size() > 1)
                        hashMap2 = list.get(previousExpanded);

                    String isExpanded2;
                    if (hashMap2 != null) {
                        isExpanded2 = hashMap2.get("isExpanded");
                        assert isExpanded2 != null;
                        if (isExpanded2.equals("false"))
                            hashMap2.put("isExpanded", "true");
                        else
                            hashMap2.put("isExpanded", "false");
                        notifyItemChanged(previousExpanded);
                    }
                }
            });

            courseSelectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog confirmationDialog = new Dialog(viewGroup.getContext());
                    confirmationDialog.setContentView(R.layout.fragment_confirmation);
                    confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    confirmationDialog.setCancelable(false);
                    confirmationDialog.show();
                    HashMap<String, String> hashMap = list.get(getAdapterPosition());

                    ((TextView) confirmationDialog.findViewById(R.id.confirmCourseCode)).setText(hashMap.get("code"));

                    confirmationDialog.findViewById(R.id.confirmNoButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationDialog.dismiss();
                        }
                    });

                    confirmationDialog.findViewById(R.id.confirmYesButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationDialog.dismiss();
                            addItems(courseCode.getText().toString(), courseName.getText().toString(), courseDescription.getText().toString());
                        }
                    });

                }
            });

        }

        public void addItems(String cc, String cn, String desc) {
            sharedPreferences = viewGroup.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
            sharedPreferences.edit().putString("cc", cc).apply();
            sharedPreferences.edit().putString("cn", cn).apply();
            sharedPreferences.edit().putString("desc", desc).apply();
            selectedCourseCode = cc;
            selectedCourseName = cn;

            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwlaKsBo6JoKSO2Ww6d5359UGEW07uIBOLxYrkiZ0WMw0k5b0c-alh-Ha20SfTRz7zs/exec?action=addItems",
                    response -> {
                        loadingDialog.dismiss();
                        Map<String, Object> map = new HashMap<>();
                        Map<String, Object> map2 = new HashMap<>();
//                        Map<String, Object> map3 = new HashMap<>();

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

//                        firestore.collection("Courses").document("Enrolled").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                DocumentSnapshot documentSnapshot = task.getResult();
//                                Map<String, Object> data = documentSnapshot.getData();

//                                if (data != null)
//                                    for (Map.Entry<String, Object> entry : data.entrySet()) {

//                                        if (entry.getKey().equals("count")) {
//                                            int count = Integer.parseInt(entry.getValue().toString());
//                                            count++;
//                                            map3.put("count", count);
                        map2.put("email", sharedPreferences.getString("email", "temp"));
                        map2.put("cc", cc);
                        map2.put("cn", cn);
                        map2.put("desc", desc);
                        map.put(String.valueOf(System.currentTimeMillis()).substring(0, 10), map2);
                        firestore.collection("Courses").document("Students").set(map, SetOptions.merge());

//                                        }
//                                    }
//                            }
//                        });

//                        firestore.collection("Courses").document(cc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                DocumentSnapshot documentSnapshot = task.getResult();
//                                Map<String, Object> data = documentSnapshot.getData();
//
//                                if (data != null)
//                                    for (Map.Entry<String, Object> entry : data.entrySet()) {
//
//                                        if (entry.getKey().equals("Students")) {
//                                            Map<String, Object> students = (Map<String, Object>) entry.getValue();
//
//                                            for (Map.Entry<String, Object> dataEntry : students.entrySet()) {
//
//                                                if (dataEntry.getKey().equals("count")) {
//                                                    count[0] = Integer.parseInt(dataEntry.getValue().toString());
//                                                    count[0]++;
//                                                    Log.d("TAG", String.valueOf(count[0]));
//
//                                                }
//                                            }
//                                        }
//                                    }
                        map.clear();
                        map.put("email", sharedPreferences.getString("email", "temp"));
                        map.put("uid", sharedPreferences.getString("uid", "temp"));
                        map.put("name", sharedPreferences.getString("name", "temp"));
                        map.put("branch", sharedPreferences.getString("branch", "temp"));
                        map.put("cls", sharedPreferences.getString("class", "temp"));
                        map.put("year", sharedPreferences.getString("year", "temp"));
                        map.put("image", sharedPreferences.getString("image", "temp"));
                        map.put("mentorName", "Not allocated yet");

//                                map2.put("count", count[0]);
//                                map2.put("email_" + count[0], sharedPreferences.getString("email", "temp"));
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put(String.valueOf(System.currentTimeMillis()).substring(0, 10), map);
                        Map<String, Object> map4 = new HashMap<>();
                        map4.put("Students", map3);
                        firestore.collection("Courses").document(cc).set(map4, SetOptions.merge());

                        getCategory();
//                            }
//                        });


                    },
                    error -> {
                        Toast.makeText(viewGroup.getContext(), "An error occured: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }
            ) {
                @NonNull
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parmas = new HashMap<>();

                    parmas.put("action", "addItems");
                    parmas.put("uid", sharedPreferences.getString("uid", "temp"));
                    parmas.put("email", sharedPreferences.getString("email", "temp"));
                    parmas.put("name", sharedPreferences.getString("name", "temp"));
                    parmas.put("branch", sharedPreferences.getString("branch", "temp"));
                    parmas.put("cls", sharedPreferences.getString("class", "temp"));
                    parmas.put("year", sharedPreferences.getString("year", "temp"));
                    parmas.put("cc", cc);
                    parmas.put("cn", cn);

                    return parmas;
                }
            };

            int socketTimeOut = 50000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            stringRequest.setRetryPolicy(policy);

            RequestQueue queue = Volley.newRequestQueue(viewGroup.getContext());
            queue.add(stringRequest);
        }

    }

    private void getCategory() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("news").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = null;
                if (task.getResult() != null)
                    dataSnapshot = task.getResult();
                String category = "All";
                if (dataSnapshot != null)
                    category = (String) dataSnapshot.child(viewGroup.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).getValue();

                viewGroup.getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("category", category).apply();

                new serviceMail(sharedPreferences.getString("email", "temp"), "Welcome!",
                        "Welcome to the course " + selectedCourseName + " (" + selectedCourseCode + ")\n" +
                                "You have successfully enrolled into this course!");

                Intent intent = new Intent(viewGroup.getContext(), studentScreen.class);
                viewGroup.getContext().startActivity(intent);
                ((Activity) viewGroup.getContext()).finish();
            }
        });
    }

}