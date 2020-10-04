package dev.moutamid.earnreal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityLogin extends AppCompatActivity {
    private static final String TAG = "ActivityLogin";

    private static final String USER_EMAIL = "userEmail";
    private static final String USER_ID = "userReferralCode";

    private EditText emailEditText, passwordEditText;

    private Utils utils = new Utils();

    private ProgressDialog mLoadingDialog;

    private Boolean isOnline = false;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "onCreate: started");

        mAuth = FirebaseAuth.getInstance();

        // CHECKING LOGIN STATUS
        checkLoginStatus();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        // CHECKING ONLINE STATUS
        checkOnlineStatus();

        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setMessage("Signing you in...");

        initDeclareViews();

    }

    private void checkLoginStatus(){

        // IF USER IS SIGNED IN
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(ActivityLogin.this, "Sign in successful", Toast.LENGTH_SHORT).show();

            // REMOVING ALL ACTIVITIES AND STARTING MAIN ACTIVITY
            Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    private void checkOnlineStatus() {
        Log.i(TAG, "checkOnlineStatus: ");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "onDataChange: online status");

                isOnline = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    private View.OnClickListener loginBtnListener() {
        Log.i(TAG, "loginBtnListener: ");
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: login button");

                if (isOnline) {
                    mLoadingDialog.show();
                    checkStatusOfEditTexts();
                } else {
                    utils.showOfflineDialog(ActivityLogin.this, "", "");
                }
            }
        };
    }

    private void checkStatusOfEditTexts() {
        Log.i(TAG, "checkStatusOfEditTexts: ");

        String emailStr = emailEditText.getText().toString().trim();
        String passwordStr = passwordEditText.getText().toString().trim();

        if (!isOnline) {
            mLoadingDialog.dismiss();
            utils.showOfflineDialog(ActivityLogin.this, "", "");
            return;
        }

        if (TextUtils.isEmpty(emailStr)) {
            mLoadingDialog.dismiss();
            emailEditText.setError("Please enter your email!");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passwordStr)) {
            mLoadingDialog.dismiss();
            passwordEditText.setError("Please enter a password!");
            passwordEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            mLoadingDialog.dismiss();
            emailEditText.setError("Email is invalid!");
            emailEditText.requestFocus();
        }

        signInUserWithNameAndPassword(emailStr, passwordStr);
    }

    private void signInUserWithNameAndPassword(final String email, String password) {
        Log.i(TAG, "signInUserWithNameAndPassword: ");

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "onComplete: user signed in");

                    storeUserInformationOffline(email);

                } else {
                    mLoadingDialog.dismiss();
                    Log.w(TAG, "onComplete: task is not completed " + task.getException());
                    Toast.makeText(ActivityLogin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    private void storeUserInformationOffline(String email) {
        Log.i(TAG, "storeUserInformationOffline: ");

        utils.storeString(ActivityLogin.this, USER_EMAIL, email);

        utils.storeString(ActivityLogin.this, USER_ID, mAuth.getCurrentUser().getUid());

        mLoadingDialog.dismiss();
        Toast.makeText(ActivityLogin.this, "Sign in successful", Toast.LENGTH_SHORT).show();

        // REMOVING ALL ACTIVITIES AND STARTING MAIN ACTIVITY
        Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void initDeclareViews() {
        Log.i(TAG, "initViews: ");

        emailEditText = findViewById(R.id.email_login_editText);
        passwordEditText = findViewById(R.id.password_login_editText);
        Button loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(loginBtnListener());
    }

}
