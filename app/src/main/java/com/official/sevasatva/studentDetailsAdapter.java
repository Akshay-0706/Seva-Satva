package com.official.sevasatva;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class studentDetailsAdapter extends RecyclerView.Adapter<studentDetailsAdapter.ViewHolder> {

    private static final String TAG = "Position";
    ArrayList<HashMap<String, String>> list;
    String[] studentDetails = new String[3];
    String[] colors = {"#F8D0A6", "#FAFBD4", "#B2EBF9", "#F9D2EF"};
    RadioButton currentSelected;
    int lastPos = -1;
    int index = 0, colorIndex = 0;

    public void setStudentDetails(String[] studentDetails) {
        this.studentDetails = studentDetails;
    }

//    int currentPosition = -1;
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
//        int currentIndex = position + index;
        Log.i(TAG, "size"+String.valueOf(list.size()));
        Log.i(TAG, String.valueOf(position*2)+" "+String.valueOf(position*2+1));
//        Log.i(TAG, String.valueOf(lastPos)+" "+String.valueOf(position));

            HashMap<String, String> hashMap1 = list.get(position * 2);
            holder.courseCode1.setText(hashMap1.get("code"));
            holder.courseName1.setText(hashMap1.get("name"));

            if (position * 2 + 1 < list.size()) {
                holder.constraintLayout2.setVisibility(View.VISIBLE);
                HashMap<String, String> hashMap2 = list.get(position * 2 + 1);
                holder.courseCode2.setText(hashMap2.get("code"));
                holder.courseName2.setText(hashMap2.get("name"));
            }
            else {
                holder.constraintLayout2.setVisibility(View.INVISIBLE);
            }

//        holder.constraintLayout1.setBackgroundColor(Color.parseColor(colors[colorIndex]));
//        holder.constraintLayout2.setBackgroundColor(Color.parseColor(colors[colorIndex + 1]));
        colorIndex += 2;
        if (colorIndex % 4 == 0)
            colorIndex = 0;
//        if (lastPos < holder.getAdapterPosition())
//            index += position + 1;
//        else
//            index -= position;
//        lastPos = position;
//        colorIndex++;
//        holder.courseDescription.setText(hashMap.get("desc"));
//        String isExpanded = hashMap.get("isExpanded");
//        holder.courseLayoutDescription.setVisibility(isExpanded.equals("true") ? View.VISIBLE : View.GONE);
//        if (isExpanded.equals("true"))
//            currentPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return list.size()/2 + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

//        Dialog loadingDialog = new Dialog(viewGroup.getContext());
        ConstraintLayout courseLayoutDescription;
        ConstraintLayout constraintLayout1, constraintLayout2;
        TextView courseName1, courseCode1, courseName2, courseCode2;
        RadioButton courseSelected1, courseSelected2;
//        AppCompatButton courseSelectionButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName1 = itemView.findViewById(R.id.courseName1);
            courseCode1 = itemView.findViewById(R.id.courseCode1);
            courseName2 = itemView.findViewById(R.id.courseName2);
            courseCode2 = itemView.findViewById(R.id.courseCode2);
            courseSelected1 = itemView.findViewById(R.id.courseSelected1);
            courseSelected2 = itemView.findViewById(R.id.courseSelected2);
            constraintLayout1 = itemView.findViewById(R.id.courseItemLayout1);
            constraintLayout2 = itemView.findViewById(R.id.courseItemLayout2);

            courseSelected1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentSelected != null && currentSelected.isChecked())
                        currentSelected.setChecked(false);
                    currentSelected = courseSelected1;
                }
            });
            courseSelected2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentSelected != null && currentSelected.isChecked())
                        currentSelected.setChecked(false);
                    currentSelected = courseSelected2;
                }
            });

//            courseDescription = itemView.findViewById(R.id.courseDescription);
//            courseLayoutDescription = itemView.findViewById(R.id.courseLayoutDescription);
//            courseSelectionButton = itemView.findViewById(R.id.courseSelectionButton);

//            constraintLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    HashMap<String, String> hashMap = list.get(getAdapterPosition());
//                    String isExpanded = hashMap.get("isExpanded");
//                    if (isExpanded.equals("false"))
//                        hashMap.put("isExpanded", "true");
//                    else
//                        hashMap.put("isExpanded", "false");
//                    notifyItemChanged(currentPosition);
//                    notifyItemChanged(getAdapterPosition());
//                }
//            });

//            courseSelectionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Dialog confirmationDialog = new Dialog(viewGroup.getContext());
//                    confirmationDialog.setContentView(R.layout.fragment_course_selection_confirmation_dialog);
//                    confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    confirmationDialog.setCancelable(false);
//                    confirmationDialog.show();
//
//                    confirmationDialog.findViewById(R.id.courseCancelButton).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            confirmationDialog.dismiss();
//                        }
//                    });
//
//                    confirmationDialog.findViewById(R.id.courseConfirmButton).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            confirmationDialog.dismiss();
//                            addItems(studentDetails, courseCode.getText().toString(), courseName.getText().toString());
//                        }
//                    });
//
//                }
//            });


        }

//        public void addItems(String[] studentDetails, String cc, String cn) {
//            loadingDialog.setContentView(R.layout.loading);
//            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            loadingDialog.setCancelable(false);
//            loadingDialog.show();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwlaKsBo6JoKSO2Ww6d5359UGEW07uIBOLxYrkiZ0WMw0k5b0c-alh-Ha20SfTRz7zs/exec?action=addItems",
//                    response -> {
//                        Toast.makeText(viewGroup.getContext(),response,Toast.LENGTH_LONG).show();
//                        loadingDialog.dismiss();
//                        Intent intent = new Intent(viewGroup.getContext(), mainScreen.class);
//                        viewGroup.getContext().startActivity(intent);
//                    },
//                    error -> Toast.makeText(viewGroup.getContext(), R.string.error_102, Toast.LENGTH_SHORT).show()
//
//            ){
//                @Nullable
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> parmas = new HashMap<>();
//
//                    parmas.put("action","addItem");
//                    parmas.put("uid",studentDetails[0]);
//                    parmas.put("branch",studentDetails[1]);
//                    parmas.put("name",studentDetails[2]);
//                    parmas.put("cc",cc);
//                    parmas.put("cn",cn);
//
//                    return parmas;
//                }
//            };
//
//            int socketTimeOut = 50000;
//            RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//
//            stringRequest.setRetryPolicy(policy);
//
//            RequestQueue queue = Volley.newRequestQueue(viewGroup.getContext());
//            queue.add(stringRequest);
//        }
    }

}
