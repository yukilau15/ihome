package com.example.ihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private TextInputEditText usernameEt, emailEt, passwordEt;
    private Button signupBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEt = findViewById(R.id.username);
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, email, password;
                username = usernameEt.getText().toString();
                email = emailEt.getText().toString();
                password = passwordEt.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter username",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter email",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid email address",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter password",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(),
                            "Password must have 6 characters",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    signup(username, email, password);
                }
            }
        });
    }

    public void signup(final String username, final String email, final String password) {
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference = FirebaseDatabase.getInstance().getReference()
                                            .child("User").child(userid);

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("uid", userid);
                                    map.put("username", username.toLowerCase());
                                    map.put("email", email.toLowerCase());
                                    map.put("fullname", "");
                                    map.put("phone", "");
                                    map.put("address", "");
                                    map.put("image", "https://firebasestorage.googleapis.com/v0/b/ihome-68a79.appspot.com/o/uploads%2Fuser.png?alt=media&token=921ab32a-4a28-49e6-a4d9-c9d52b6191d8");

                                    databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Sign up successfully\nplease verify your email",
                                                        Toast.LENGTH_SHORT)
                                                        .show();

                                                Intent i = new Intent(getApplicationContext(),
                                                        SigninActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Sign up failed",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    public void signinBtn(View view) {
        Intent i = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(i);
        finish();
    }
}
