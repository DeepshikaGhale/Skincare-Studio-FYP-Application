package com.example.fyp_application;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAnAccount;

    private FrameLayout parentFrameLayout;

    private TextView loginemailaddress;
    private TextView loginpassword;

    private ImageButton closeBtn;
    private Button signInBtn;
    private ProgressBar progressBar;

    private TextView forgotPassword;
    private FirebaseAuth firebaseAuth;

    public static boolean disableClosedButton = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAnAccount = view.findViewById(R.id.tv_go_to_login);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);

        loginemailaddress = view.findViewById(R.id.loginemail);
        loginpassword = view.findViewById(R.id.login_password);
        forgotPassword = view.findViewById(R.id.sign_in_forget_password);

        closeBtn = view.findViewById(R.id.signin_close_btn);
        signInBtn = view.findViewById(R.id.SignInBtn);
        progressBar = view.findViewById(R.id.signinprogressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        if (disableClosedButton){
            closeBtn.setVisibility(View.GONE);
        }else {
            closeBtn.setVisibility(View.VISIBLE);
        }

        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ResetPasswordFragment());
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginemailaddress.getText().toString().trim();
                String password = loginpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter Email Address", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                }else {
                    signin(email, password);
                }
            }
        });

    }

    private void signin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mainIntent();

                        } else {
                            // If sign in fails, display a message to the user.
                            //  progressBar.setVisibility(View.INVISIBLE);
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void mainIntent(){
        if (disableClosedButton){
            disableClosedButton = false;
        }
        else {
            Intent mainIntent = new Intent(getActivity(), navigationbar.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }
}
