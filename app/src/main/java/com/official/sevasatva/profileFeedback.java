package com.official.sevasatva;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class profileFeedback extends AppCompatActivity {

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
        setContentView(R.layout.activity_student_profile_feedback);

        ((TextView) findViewById(R.id.text1)).setText(Html.fromHtml("<font color = #66687C>Give us your valuable</font> <font color=#1C2E46>FeedBack!</font>"));

        EditText feedbackText = findViewById(R.id.editText);

        findViewById(R.id.appCompatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feedbackText.getText().toString().isEmpty()) {
                    sendFeedback(feedbackText.getText().toString().trim());
                    Dialog successDialog = new Dialog(profileFeedback.this);
                    successDialog.setContentView(R.layout.fragment_success);
                    successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    successDialog.setCancelable(false);
                    successDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            successDialog.dismiss();
                            feedbackText.setText("");
                            feedbackText.setHint("Submit another feedback");
                        }
                    }, 3000);
                } else
                    Toast.makeText(profileFeedback.this, "Feedback should not be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFeedback(String feedback) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map.put("name", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "temp"));
        map.put("branch", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("branch", "temp"));
        map.put("class", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cls", "temp"));
        map.put("uid", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("uid", "temp"));
        map.put("cc", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"));
        map.put("year", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("year", "temp"));
        map.put("isMentor", !getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isUserStudent", true));
        map2.put("feedback", feedback);

        new serviceMail("akshay0706vhatkar@gmail.com", "Feedback from Seva/Satva App",
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "temp")
                        + " has sent feedback for Seva/Satva App, check the user details by following this link:\n" +
                        "https://console.firebase.google.com/project/seva-satva/firestore/data/~2FFeedbacks"
                        + "\n\nFeedback contains:\n" + feedback);

//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("akshay0706vhatkar@gmail.com", "yzryopdbttlxdfxm");
//            }
//        });
//
//        MimeMessage message = new MimeMessage(session);
//
//        try {
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress("akshay0706vhatkar@gmail.com"));
//
//            message.setSubject("Feedback from Seva/Satva App");
//            message.setText(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("name", "temp")
//                    + " has sent feedback for Seva/Satva App, check the user details by following this link:\n" +
//                    "https://console.firebase.google.com/project/seva-satva/firestore/data/~2FFeedbacks"
//                    + "\n\nFeedback contains:\n" + feedback);
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Transport.send(message);
//                    } catch (MessagingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            thread.start();
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.timeapi.io/api/Time/current/zone?timeZone=Asia/Kolkata",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String date = getDateNTime.getTime(jsonObject.getString("time"), jsonObject.getInt("seconds"), true) + ", " + jsonObject.getString("day") + " " + getDateNTime.getMonth(jsonObject.getInt("month")) + " " + jsonObject.getInt("year");

                        map.put(date, map2);

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("Feedbacks").document(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", "temp")).set(map, SetOptions.merge());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                error -> {
                    Toast.makeText(this, "Unable to access current date!", Toast.LENGTH_LONG).show();
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


}