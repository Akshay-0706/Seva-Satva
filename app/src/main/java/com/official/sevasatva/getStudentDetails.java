package com.official.sevasatva;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.android.volley.AsyncCache;
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

public class getStudentDetails {

    Context context;
    String email;

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public String getCls() {
        return cls;
    }

    public int getUid() {
        return uid;
    }

    public int getYear() {
        return year;
    }

    String name;
    String branch;
    String cls;
    int uid, year;
    boolean isRunning = true;
    int i = 0;

    public getStudentDetails(String email, Context context) {
        this.email = email;
        this.context = context;
        Toast.makeText(context, "Started", Toast.LENGTH_SHORT).show();
//        fetchStudentDetails();
        Toast.makeText(context, "Ended", Toast.LENGTH_SHORT).show();
    }


}
