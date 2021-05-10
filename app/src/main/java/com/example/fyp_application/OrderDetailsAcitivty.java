package com.example.fyp_application;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsAcitivty extends AppCompatActivity {

    private int position;

    private TextView title, price, quantity;
    private ImageView productImage, orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;
    private TextView orderedDate, packedDate, shippedDate, deliveredDate;
    private TextView orderedBody, packedBody, shippedBody, deliveredBody;
    private LinearLayout rateNowContainer;
    private int rating;
    private TextView fullName, Address, Pincode;
    private TextView totaItems, totalItemsPrice, deliveryPrice, totalAmount;
    private Dialog loadingDialog, cancelDialog;
    private SimpleDateFormat simpleDateFormat;
    private Button cancelOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_acitivty);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for backbutton

        //loading diaglog
        loadingDialog = new Dialog(OrderDetailsAcitivty.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //order cancel diaglog
        cancelDialog = new Dialog(OrderDetailsAcitivty.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(true);
        //cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.input_field));
        //cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //order cancel dialog

        position= getIntent().getIntExtra("Position", -1);

        MyOrderItemModel model = DBqueries.myOrderItemModelList.get(position);

        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        quantity = findViewById(R.id.product_quantity);

        productImage = findViewById(R.id.product_image);

        cancelOrderBtn = findViewById(R.id.cancel_btn);

        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipped_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        O_P_progress = (ProgressBar) findViewById(R.id.order_packed_progressbar);
        P_S_progress = (ProgressBar) findViewById(R.id.packed_shipping_progressbar);
        S_D_progress = (ProgressBar) findViewById(R.id.shipping_delovered_progressbar);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipping_title);
        deliveredTitle = findViewById(R.id.delivered_title);

        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipping_date);
        deliveredDate = findViewById(R.id.delivered_date);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipping_body);
        deliveredBody = findViewById(R.id.delivered_body);

        rateNowContainer = findViewById(R.id.rate_now_container);

        fullName = findViewById(R.id.fullName);
        Address = findViewById(R.id.address);
        Pincode = findViewById(R.id.pinCode);

        totaItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryPrice = findViewById(R.id.delivery_charge);
        totalAmount = findViewById(R.id.total_price);

        title.setText(model.getProductTitle());
        price.setText("$"+model.getProductPrice()+"/-");
        quantity.setText("Qty : "+ String .valueOf(model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);
        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");

        switch (model.getDeliveryStatus()){
            case "ORDERED":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));


                O_P_progress.setVisibility(View.GONE);
                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);


                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "PACKED":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                O_P_progress.setProgress(100);

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "SHIPPED":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);

                S_D_progress.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                deliveredTitle.setText("Out of Delivery");
                deliveredBody.setText("Your order is out for delivery.");
                break;

            case "DELIVERED":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                break;

            case "Cancelled":
                if (model.getPackedDate().after(model.getOrderedDate())){

                    if (model.getShippedDate().after(model.getPackedDate())){

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled.");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);

                    }else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled.");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);

                        S_D_progress.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                    }

                }else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled.");

                    O_P_progress.setProgress(100);

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);

                    shippedIndicator.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }
                break;
        }

        //rating layout
        rating = model.getRating();
        setRating(rating);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++){
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setRating(starPosition);
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductID());
                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                            if (rating != 0){
                                Long increase = documentSnapshot.getLong(starPosition+ 1 + "_star") +1 ;
                                Long decrease = documentSnapshot.getLong(rating+ 1 +"_star") -1;
                                transaction.update(documentReference, starPosition+ 1 + "_star", increase);
                                transaction.update(documentReference, rating+ 1 + "_star", decrease);
                            }else {
                                Long increase = documentSnapshot.getLong(starPosition+ 1 + "_star") +1 ;
                                transaction.update(documentReference, starPosition+ 1 + "_star", increase);
                            }
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Map<String, Object> myrating = new HashMap<>();
                            if (DBqueries.myRateIds.contains(model.getProductID())) {
                                myrating.put("rating_" + DBqueries.myRateIds.indexOf(model.getProductID()), (long) starPosition + 1);
                            } else {
                                myrating.put("list_size", (long) DBqueries.myRateIds.size() + 1);
                                myrating.put("product_ID_" + DBqueries.myRateIds.size(), model.getProductID());
                                myrating.put("rating_" + DBqueries.myRateIds.size(), (long) starPosition + 1);
                            }
                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                    .document("MY_RATINGS").update(myrating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        DBqueries.myOrderItemModelList.get(position).setRating(starPosition);
                                        if (DBqueries.myRateIds.contains(model.getProductID())) {
                                            DBqueries.myRating.set(DBqueries.myRateIds.indexOf(model.getProductID()), Long.parseLong(String.valueOf(starPosition + 1)));
                                        } else {
                                            DBqueries.myRateIds.add(model.getProductID());
                                            DBqueries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailsAcitivty.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        }

        //rating layout

        if (model.isCancellationEquested()){
            cancelOrderBtn.setVisibility(View.VISIBLE);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setText("Cancellation in process.");
            cancelOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }else{
            if (model.getDeliveryStatus().equals("ORDERED") || model.getDeliveryStatus().equals("PACKED")){
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.show();
                        cancelDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                Map<String, Object> map = new HashMap<>();
                                map.put("ORDER ID", model.getOrder_id());
                                map.put("Product ID", model.getProductID());
                                map.put("Order Cancelled", false);
                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrder_id()).collection("OrderItems")
                                                            .document(model.getProductID()).update("Cancellation requested", true)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        model.setCancellationEquested(true);
                                                                        cancelOrderBtn.setEnabled(false);
                                                                        cancelOrderBtn.setText("Cancellation in process.");
                                                                        cancelOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                                        cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                                                    }else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(OrderDetailsAcitivty.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });
                                                }else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsAcitivty.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        cancelDialog.show();
                    }
                });
            }
        }

        fullName.setText(model.getFullName());
        Address.setText(model.getAddress());
        Pincode.setText(model.getPincode());

        totaItems.setText("Price("+ model.getProductQuantity()+" items)");

        Long totalItemsPriceValue;
        totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getProductPrice());
        totalItemsPrice.setText(" $ " + totalItemsPriceValue + " /- ");
        deliveryPrice.setText("$" + model.getDeliveryPrice() + "/-");
//        totalAmount.setText("$"+ (totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice()))+ "/-");
        totalAmount.setText("$"+(Long.valueOf(model.getDeliveryPrice())+totalItemsPriceValue)+"/-");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++){
            ImageView starButton = (ImageView) rateNowContainer.getChildAt(x);
            starButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition){
                starButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }

        }
    }
}
