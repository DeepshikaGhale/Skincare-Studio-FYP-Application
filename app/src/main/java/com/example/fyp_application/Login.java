package com.example.fyp_application;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    ProgressBar progressBar;
    CheckBox checkBox;
    Button loginButton;


    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
   // private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.loginemail);
        loginPassword = findViewById(R.id.signup_password);
        progressBar = findViewById(R.id.progressbar);

        loginButton = findViewById(R.id.SignUpBtn);

        checkBox = findViewById(R.id.rememberMe);
        firebaseUser = mAuth.getCurrentUser();

    }

   public void Login(View view) {

        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }else {
            signin(email, password);
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    private void signin(final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(Login.this, navigationbar.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                          //  progressBar.setVisibility(View.INVISIBLE);
                            loginEmail.setText("");
                            loginPassword.setText("");
                            Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



//    public void forgetpassword(View view) {
//        startActivity(new Intent(Login.this, ForgetPassword.class));
//    }

//    public void back(View view) {
//        startActivity(new Intent(Login.this, Welcome.class));
//    }

    public void Register(View view) {
        startActivity(new Intent(Login.this, MainActivity.class));
    }

}


