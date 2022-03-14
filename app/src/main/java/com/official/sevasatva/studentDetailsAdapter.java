package com.official.sevasatva;


import android.app.Dialog;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class studentDetailsAdapter extends RecyclerView.Adapter<studentDetailsAdapter.ViewHolder> {

    private static final String TAG = "Position";
    ArrayList<HashMap<String, String>> list;

    String email;

    public void setDetails(String email, String name, String branch, String cls, int uid, int year) {
        this.email = email;
        this.name = name;
        this.branch = branch;
        this.cls = cls;
        this.uid = uid;
        this.year = year;
    }

    String name;
    String branch;
    String cls;
    int uid, year;

    int currentPosition = -1;
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
        Log.i(TAG, String.valueOf(position)+" "+String.valueOf(currentPosition));
        HashMap<String, String> hashMap = list.get(position);
        holder.courseCode.setText(hashMap.get("code"));
        holder.courseName.setText(hashMap.get("name"));
        holder.courseDescription.setText(hashMap.get("desc"));
        String isExpanded = hashMap.get("isExpanded");
        assert isExpanded != null;
        holder.courseLayoutDescription.setVisibility(isExpanded.equals("true") ? View.VISIBLE : View.GONE);
        if (isExpanded.equals("true"))
            currentPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return list.size();
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
                    notifyItemChanged(currentPosition);
                    notifyItemChanged(getAdapterPosition());
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
                            addItems(courseCode.getText().toString(), courseName.getText().toString());
                        }
                    });

                }
            });


        }

        public void addItems(String cc, String cn) {
            loadingDialog.setContentView(R.layout.fragment_loading);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwlaKsBo6JoKSO2Ww6d5359UGEW07uIBOLxYrkiZ0WMw0k5b0c-alh-Ha20SfTRz7zs/exec?action=addItems",
                    response -> {
                        Toast.makeText(viewGroup.getContext(),response,Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                        Intent intent = new Intent(viewGroup.getContext(), home.class).putExtra("email", email).putExtra("uid",uid)
                                .putExtra("name", name).putExtra("branch",branch).putExtra("cls",cls).putExtra("year",year);
                        viewGroup.getContext().startActivity(intent);
                    },
                    error -> {
                        Toast.makeText(viewGroup.getContext(), "Here", Toast.LENGTH_SHORT).show();
                        Toast.makeText(viewGroup.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }
            ){
                @NonNull
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parmas = new HashMap<>();

                    parmas.put("action","addItems");
                    parmas.put("uid",String.valueOf(uid));
                    parmas.put("email",email);
                    parmas.put("name",name);
                    parmas.put("branch",branch);
                    parmas.put("cls",cls);
                    parmas.put("year",String.valueOf(year));
                    parmas.put("cc",cc);
                    parmas.put("cn",cn);

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

}
