package com.example.fyp_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.fyp_application.DBqueries.cartItemModelList;
import static com.example.fyp_application.DBqueries.cartList;
import static com.example.fyp_application.DBqueries.firebaseFirestore;

public class OTP_Verification_Activity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyButton;
    private String userNumber;
    private Button sendVerificationBtn;
    private ProgressBar otpProgress;

    private FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationID;
    PhoneAuthProvider.ForceResendingToken token;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p__verification_);

        mAuth = FirebaseAuth.getInstance();
        phoneNo =findViewById(R.id.phone_number);
        otp = findViewById(R.id.OTP);
        verifyButton = findViewById(R.id.verify_btn);
        userNumber = getIntent().getStringExtra("mobileNo");
        sendVerificationBtn = findViewById(R.id.Send_OTP_Btn);
        otpProgress = findViewById(R.id.otp_verify_progress);


        sendVerificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpProgress.setVisibility(View.VISIBLE);
                String countryCode = "977";
                String phoneNum = "+" + countryCode + "" + userNumber;
                verifyPhoneNumber(phoneNum);
                phoneNo.setText("Verification code has been sent to +977 " + userNumber);
                verifyButton.setVisibility(View.VISIBLE);
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the OTP
                if(otp.getText().toString().isEmpty()){
                    otp.setError("Enter OTP First");
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp.getText().toString());
                authenticateUser(credential);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OTP_Verification_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationID = s;
                token = forceResendingToken;

                sendVerificationBtn.setVisibility(View.GONE);
                sendVerificationBtn.setEnabled(false);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }

    public void verifyPhoneNumber(String phoneNumber){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void authenticateUser(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

            @Override
            public void onSuccess(AuthResult authResult) {

                    Map<String, Object> updateStatus = new HashMap<>();
                    updateStatus.put("PAYMENT_STATUS", "Paid");
                    updateStatus.put("ORDER_STATUS", "Ordered");
                    String orderID = getIntent().getStringExtra("orderID");
                    firebaseFirestore.collection("ORDERS").document(orderID).update(updateStatus)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Map<String, Object> userOrder = new HashMap<>();
                                        userOrder.put("order_id", orderID);
                                        userOrder.put("time", FieldValue.serverTimestamp());
                                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS")
                                                .document(orderID).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    DeliveryActivity.codOrderConfirmed = true;
                                                    finish();
                                                }else {
                                                    Toast.makeText(OTP_Verification_Activity.this, "Failed to update user's orderlist", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(OTP_Verification_Activity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                Toast.makeText(OTP_Verification_Activity.this, "Verified", Toast.LENGTH_SHORT).show();
                DeliveryActivity.codOrderConfirmed =true;
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OTP_Verification_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
