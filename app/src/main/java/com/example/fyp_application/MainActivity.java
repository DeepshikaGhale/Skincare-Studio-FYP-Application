package com.example.fyp_application;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        SystemClock.sleep(3000);
    }

    //TODO: make this main activity a splash screen and then make uncomment this, remember the splashacitvity is supposed to be mainactivity and
    // the one mainacitvity of code is supposed to be navigation bar
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            Intent registerIntent = new Intent(MainActivity.this, RegsiterActivity.class);
            startActivity(registerIntent);
            finish();
        }else{
            Intent mainIntent = new Intent(MainActivity.this, navigationbar.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
