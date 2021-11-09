package com.example.ihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void signinBtn(View view) {
        Intent i = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(i);
        finish();
    }

    public void signupBtn(View view) {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
        finish();
    }
}
