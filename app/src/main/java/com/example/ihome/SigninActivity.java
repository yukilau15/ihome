package com.example.ihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private TextInputEditText emailEt, passwordEt;
    private Button signinBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        signinBtn = findViewById(R.id.signin);

        mAuth = FirebaseAuth.getInstance();

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = emailEt.getText().toString();
                password = passwordEt.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter email",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter password",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    signin(email, password);
                }
            }
        });
    }

    public void signin(final String email, final String password) {
        mAuth.
                signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser.isEmailVerified()) {
                                Toast.makeText(getApplicationContext(),
                                        "Sign in successfully",
                                        Toast.LENGTH_SHORT)
                                        .show();

                                Intent i = new Intent(getApplicationContext(),
                                        MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Please verify your email",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Sign in failed",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    public void signupBtn(View view) {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
        finish();
    }
}
