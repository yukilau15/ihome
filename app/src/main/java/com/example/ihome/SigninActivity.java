package com.example.ihome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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
        mAuth.signInWithEmailAndPassword(email, password)
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

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
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

    public void resetBtn(View view) {
        EditText resetEt = new EditText(view.getContext());

        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Reset Password");
        resetDialog.setMessage("Enter your email and we'll send you a link to reset your password");
        resetDialog.setView(resetEt);

        resetEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        resetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = resetEt.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter email",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid email address",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "Email sent to\n" + email,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Error sending email\ncouldn't find your account",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            }
        });

        resetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        resetDialog.create().show();
    }

    public void signupBtn(View view) {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
        finish();
    }
}
