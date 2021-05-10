package com.example.fyp_application;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatePasswordFragment extends Fragment {

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    private EditText oldPassword, newPassword, confirmnewPassword;
    private Button updateButton;
    private Dialog loadingDialog;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        //loading diaglog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        email = getArguments().getString("Email");

        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmnewPassword = view.findViewById(R.id.confirm_new_password);
        updateButton = view.findViewById(R.id.update_password_btn);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String puraopassword = oldPassword.getText().toString().trim();
                String nayapassword = newPassword.getText().toString().trim();
                String confrimnayapassword = confirmnewPassword.getText().toString().trim();

                if (TextUtils.isEmpty(puraopassword) && TextUtils.isEmpty(nayapassword) && TextUtils.isEmpty(confrimnayapassword)){
                    Toast.makeText(getContext(), "Please fill the fields", Toast.LENGTH_SHORT).show();
                }else if(nayapassword.equals(confrimnayapassword)){
                    //update passwor
                    loadingDialog.show();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, puraopassword);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(nayapassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    oldPassword.setText(null);
                                                    newPassword.setText(null);
                                                    confirmnewPassword.setText(null);
                                                    Toast.makeText(getContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });

                                    }else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(getContext(), "Passwords does not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
