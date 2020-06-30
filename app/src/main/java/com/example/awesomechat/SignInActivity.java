package com.example.awesomechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomechat.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final String DB_NAME_USERS = "users";
    private static final String LOG_MESSAGE_SIGN_IN_SUCCESS = "signInWithEmail:success";
    private static final String LOG_MESSAGE_SIGN_IN_FAILURE = "signInWithEmail:failure";
    private boolean loginModeActive;

    private FirebaseAuth auth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText nameEditText;
    private TextView textLoginSignUpTextView;
    private Button buttonSignUp;

    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child(DB_NAME_USERS);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        textLoginSignUpTextView = findViewById(R.id.googleLoginSignUpTextView);
        buttonSignUp = findViewById(R.id.loginSignUpButton);
        confirmPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(
                    SignInActivity.this,
                    UserListActivity.class)
            );
        }

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSignUpUser(
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim()
                );
            }


        });
    }

    private void loginSignUpUser(String email, String password) {
        if (loginModeActive) {
            if (!passwordEditText.getText().toString().trim().equals(
                    confirmPasswordEditText.getText().toString().trim()
            )) {
                Toast.makeText(this, R.string.password_dont_match, Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, R.string.password_must_be_leaast_7_charecters, Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, R.string.please_input_email, Toast.LENGTH_LONG).show();
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = auth.getCurrentUser();
                                successLoginOrSignUp(task);
                                createUser(user);
                            }

                        });
            }

        } else {
            if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, R.string.password_must_be_leaast_7_charecters, Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, R.string.please_input_email, Toast.LENGTH_LONG).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = auth.getCurrentUser();
                                successLoginOrSignUp(task);
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(nameEditText.getText().toString().trim());
        usersDatabaseReference.push().setValue(user);
    }

    public void toggleLoginMode(View view) {
        if (loginModeActive) {
            loginModeActive = false;
            buttonSignUp.setText(R.string.log_in);
            confirmPasswordEditText.setVisibility(View.GONE);
            nameEditText.setVisibility(View.GONE);
            textLoginSignUpTextView.setText(R.string.or_sign_up);
        } else {
            loginModeActive = true;
            buttonSignUp.setText(R.string.sign_up);
            textLoginSignUpTextView.setText(R.string.or_log_in);
            nameEditText.setVisibility(View.VISIBLE);
            confirmPasswordEditText.setVisibility(View.VISIBLE);
        }
    }

    public void successLoginOrSignUp(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Log.d(TAG, LOG_MESSAGE_SIGN_IN_SUCCESS);
            Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
            intent.putExtra(ChatActivity.INTENT_USER_NAME, nameEditText.getText().toString().trim());
            startActivity(intent);
        } else {
            Log.w(TAG, LOG_MESSAGE_SIGN_IN_FAILURE, task.getException());
            Toast.makeText(SignInActivity.this, R.string.authentication_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
