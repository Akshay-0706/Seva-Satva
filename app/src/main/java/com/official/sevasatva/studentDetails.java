package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class studentDetails extends AppCompatActivity {

    Dialog loadingDialog, redirectingDialog;
    RecyclerView recyclerView;
    studentDetailsAdapter studentDetailsAdapter;
    ArrayList<HashMap<String, String>> list;
    TextInputLayout searchInput;
    TextInputEditText searchTextInput;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_student_details);
        redirectingDialog = new Dialog(this);

        if (!isInternetAvailable()) {
            redirectingDialog.setContentView(R.layout.fragment_redirecting);
            redirectingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            redirectingDialog.setCancelable(false);
            redirectingDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    redirectingDialog.dismiss();
                    startActivity(new Intent(
                            Settings.ACTION_WIFI_SETTINGS));
                }
            }, 3000);
        }

        loadingDialog = new Dialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.studentDetailsRecycler);

        getItems();

        searchInput = findViewById(R.id.searchInput);
        searchTextInput = findViewById(R.id.searchTextInput);

        searchTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    private void filter(String text) {
        ArrayList<HashMap<String, String>> newList = new ArrayList<>();
        for (HashMap<String, String> map : list) {
            if (map.get("code").toLowerCase().contains(text.toLowerCase()) || map.get("name").toLowerCase().contains(text.toLowerCase()))
                newList.add(map);
        }
        studentDetailsAdapter.filteredList(newList);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getItems() {

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

        list = new ArrayList<>();


        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("course_details");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String code = jo.getString("code");
                String name = jo.getString("name");
                String desc = jo.getString("desc");

                HashMap<String, String> item = new HashMap<>();
                item.put("code", code);
                item.put("name", name);
                item.put("desc", desc);
                item.put("isExpanded", "false");

                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        studentDetailsAdapter = new studentDetailsAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(studentDetailsAdapter);

        loadingDialog.dismiss();
    }
}