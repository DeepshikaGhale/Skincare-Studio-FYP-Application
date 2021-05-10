package com.example.fyp_application;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }

    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private EditText emailaddress;
    private EditText fullname;
    private EditText signuppassword;
    private EditText ConfirmPassword;

    private ImageButton closeBtn;
    private Button signUpBtn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public static boolean disableClosedButton = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAnAccount = view.findViewById(R.id.alreadyhaveanaccount);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        emailaddress = view.findViewById(R.id.signup_email);
        fullname = view.findViewById(R.id.signup_fullname);
        signuppassword = view.findViewById(R.id.signup_password);
        ConfirmPassword = view.findViewById(R.id.signup_confirmpass);

        closeBtn = view.findViewById(R.id.signup_close_btn);
        signUpBtn = (Button) view.findViewById(R.id.SignUpBtn);

        progressBar = view.findViewById(R.id.signupprogressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailaddress.getText().toString().trim();
                String password = signuppassword.getText().toString().trim();
                String username =  fullname.getText().toString().trim();
                String confirmpassword = ConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmpassword)){
                    Toast.makeText(getActivity(), "Please fill the required index", Toast.LENGTH_SHORT).show();
                }
                else if (password.equals(confirmpassword)){
                    createAccount(email, password, username);
                }
                else {
                    Toast.makeText(getActivity(), "Confirm Password field and Password field does not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void createAccount(final String email, String password, final String username) {
        progressBar.setVisibility(View.VISIBLE);
        signUpBtn.setEnabled(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Map<String, Object> userdata = new HashMap<>();
                            userdata.put("fullname", username);
                            userdata.put("email", email);
                            userdata.put("profile", "");
                            firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                    .set(userdata)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                                                //MAPS
                                                Map<String, Object> wishlistMap = new HashMap<>();
                                                wishlistMap.put("list_size", (long) 0);

                                                Map<String, Object> ratingsMap = new HashMap<>();
                                                ratingsMap.put("list_size", (long) 0);

                                                Map<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("list_size", (long) 0);

                                                Map<String, Object> myAddressesMap = new HashMap<>();
                                                myAddressesMap.put("list_size", (long) 0);
                                                //MAPS

                                                final List<String> documentNames = new ArrayList<>();
                                                documentNames.add("MY_WISHLIST");
                                                documentNames.add("MY_RATINGS");
                                                documentNames.add("MY_CART");
                                                documentNames.add("MY_ADDRESSES");

                                                List<Map<String, Object>> documentFields = new ArrayList<>();
                                                documentFields.add(wishlistMap);
                                                documentFields.add(ratingsMap);
                                                documentFields.add(cartMap);
                                                documentFields.add(myAddressesMap);

                                                for (int x = 0; x < documentNames.size(); x++){

                                                    final int finalX = x;
                                                    userDataReference.document(documentNames.get(x)).set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                if (finalX == documentNames.size() -1) {
                                                                    mainIntent();
                                                                }
                                                            }else {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                signUpBtn.setEnabled(true);
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            signUpBtn.setEnabled(true);
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
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

