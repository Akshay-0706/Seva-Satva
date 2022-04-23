package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mentorAllocation extends AppCompatActivity {

    internetCheckListener internetCheckListener = new internetCheckListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckListener);
        super.onStop();
    }

    Dialog loadingDialog;
    RecyclerView mentorAlcRecyclerView;
    final List<mentorAllocationModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_allocation);

        loadingDialog = new Dialog(this);
        mentorAlcRecyclerView = findViewById(R.id.mentorAlcRecyclerView);
        setRecyclerView();
    }

    private void setRecyclerView() {

        loadingDialog.setContentView(R.layout.fragment_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbx47PuVbX9xCSQyX3x9CPyEp54Ir_qtaTvNYv5jOY2qbyerviweB6z12QlYU8P0l6M9mA/exec?action=getItems",
                this::parseItems,

                error -> {
                    loadingDialog.dismiss();
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("course_details");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String name = jo.getString("name");
                String code = jo.getString("code");
                String desc = jo.getString("desc");

                mentorAllocationModel mentorAllocationModel = new mentorAllocationModel(name, code, desc);
                list.add(mentorAllocationModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mentorAllocationAdapter mentorAllocationAdapter = new mentorAllocationAdapter(list);
        mentorAlcRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mentorAlcRecyclerView.setAdapter(mentorAllocationAdapter);

        loadingDialog.dismiss();
    }
}