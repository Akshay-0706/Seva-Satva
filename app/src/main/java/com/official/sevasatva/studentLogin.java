package com.official.sevasatva;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class studentLogin extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "Google sign in tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        signIn();
    }

    public void signIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("426988667812-meoa78skojkt8d3u0rs5mi9dd4i5nok3.apps.googleusercontent.com") // R.string.default_web_client_id
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        Log.d(TAG, "signIn: Begin Google Sign In");
        resultLauncher.launch(new Intent(googleSignInClient.getSignInIntent()));

    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                    String email = account.getEmail();
                    assert email != null;
                    if (email.contains("@spit.ac.in"))
                        authenticate(account);
                    else {
                        googleSignInClient.signOut();
                        Toast.makeText(studentLogin.this, R.string.error_501, Toast.LENGTH_SHORT).show();
                        signIn();
                    }
                }
                catch (Exception e) {
                    Log.d(TAG, "onActivityResult: "+e.getMessage());
                }
            }
        }
    });

    private void authenticate(GoogleSignInAccount account) {
        Log.d(TAG, "authenticate: Begin firebase auth with google account");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: Logged in");
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG, "onSuccess: Email: "+email);
                        Log.d(TAG, "onSuccess: UID: "+uid);

                        boolean isFirstRunStudentDetails = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                .getBoolean("isFirstRunStudentDetails", true);

                        assert email != null;
                        String[] name = getNameSurname(email);
                        Toast.makeText(studentLogin.this, name[0] + " "+name[1], Toast.LENGTH_SHORT).show();
                        if (authResult.getAdditionalUserInfo().isNewUser() || isFirstRunStudentDetails) {
                            Log.d(TAG, "onSuccess: Account created\n"+email);
                            Toast.makeText(studentLogin.this, "Account created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(studentLogin.this, studentDetails.class).putExtra("name", name[0]).putExtra("surname", name[1]));
                            finish();
                        }
                        else {
                            Log.d(TAG, "onSuccess: Account already exists\n"+email);
                            Toast.makeText(studentLogin.this, "Account already exists", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(studentLogin.this, home.class).putExtra("name", name[0]).putExtra("surname", name[1]));
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: login failed"+e.getMessage());
                    }
                });

    }

    private String[] getNameSurname(String email) {
        String name = email.substring(0, email.indexOf("."));
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        String surname = email.substring(email.indexOf(".") + 1, email.indexOf("@"));
        surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
        return new String[]{name, surname};
    }
}