package com.example.fyp_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.fyp_application.DBqueries.firebaseFirestore;

public class CheckoutActivity extends AppCompatActivity {
    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = "https://protected-wave-68875.herokuapp.com/";

    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private String totalAmount;
    private TextView amountTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        amountTv = findViewById(R.id.amountTextView);
        totalAmount = getIntent().getStringExtra("totalAmount");
        amountTv.setText("The total payable amount is $"+ totalAmount);
        // Configure the SDK with your  Stripe publishable key so it can make requests to Stripe
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51IlnWiGXPMpLqVmhI5NtQbEvdN8ssNGrOPGy0HPpsGS7yMSfwVSUPz7u27sroY023k3sCu2oAfzmFXoyncBh270p007uDbEYYE")
        );
        startCheckout();
    }

    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency", "usd");
        itemMap.put("id", "photo_subscription");
        itemMap.put("amount", totalAmount);
        itemList.add(itemMap);
        payMap.put("items", itemList);
        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(CheckoutActivity.this));

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
               // DeliveryActivity.codOrderConfirmed = true;
            }
        });
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<CheckoutActivity> activityRef;

        PayCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<CheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent)
//                );
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
                                                Toast.makeText(CheckoutActivity.this, "Failed to update user's orderlist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(activity, "Order Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Toast.makeText(activity, "Payment Completed", Toast.LENGTH_SHORT).show();
                DeliveryActivity.codOrderConfirmed =true;
                finish();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }
}
