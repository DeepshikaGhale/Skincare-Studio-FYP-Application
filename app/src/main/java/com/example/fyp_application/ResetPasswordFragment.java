package com.example.fyp_application;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText registeredEmailAddress;
    private Button resetPasswordButton;
    private TextView goBack;

    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        registeredEmailAddress = view.findViewById(R.id.forget_password_email_password);
        resetPasswordButton = view.findViewById(R.id.reset_password_btn);
        goBack = view.findViewById(R.id.forget_password_go_back);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registeredEmailAddress.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity(), "Registered email address required.", Toast.LENGTH_SHORT).show();
                }
                else {
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Email sent successfully.", Toast.LENGTH_SHORT).show();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}
