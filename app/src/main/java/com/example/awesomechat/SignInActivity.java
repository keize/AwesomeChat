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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
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
        usersDatabaseReference = database.getReference().child("users");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        textLoginSignUpTextView = findViewById(R.id.googleLoginSignUpTextView);
        buttonSignUp = findViewById(R.id.loginSignUpButton);
        confirmPasswordEditText = findViewById(R.id.repeatPasswordEditText);


        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, UserListActivity.class));

        }


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSignUpUser(emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());

            }


        });


    }

    private void loginSignUpUser(String email, String password) {


        if (loginModeActive) {
            if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be leaast 7 charecters", Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please, input email", Toast.LENGTH_LONG).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim() );
                                    startActivity(intent);
                                    //     updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //   updateUI(null);
                                    // ...
                                }

                                // ...
                            }
                        });
            }

        } else {

            if (!passwordEditText.getText().toString().trim().equals(
                    confirmPasswordEditText.getText().toString().trim()
            )) {
                Toast.makeText(this, "Password don't match", Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be leaast 7 charecters", Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please, input email", Toast.LENGTH_LONG).show();
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim() );
                                    startActivity(intent);

                                    // updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }

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

    public void toggleLoginMode (View view){

            if (loginModeActive) {
                loginModeActive = false;
                buttonSignUp.setText("Sign up");
                confirmPasswordEditText.setVisibility(View.VISIBLE);
                nameEditText.setVisibility(View.VISIBLE);
                textLoginSignUpTextView.setText("Or, log in");
            } else {
                loginModeActive = true;
                buttonSignUp.setText("Log in");
                textLoginSignUpTextView.setText("Or, sign up");
                nameEditText.setVisibility(View.GONE);
                confirmPasswordEditText.setVisibility(View.GONE);
            }
        }

    }
