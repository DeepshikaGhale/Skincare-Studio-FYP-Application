package com.example.fyp_application;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderAdaptor extends RecyclerView.Adapter<MyOrderAdaptor.ViewHolder> {

    private List<MyOrderItemModel> myOrderItemModelList;
    private Dialog loadingDialog;

    public MyOrderAdaptor(List<MyOrderItemModel> myOrderItemModelList, Dialog  loadingDialog) {
        this.myOrderItemModelList = myOrderItemModelList;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public MyOrderAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdaptor.ViewHolder holder, int position) {
        String resource = myOrderItemModelList.get(position).getProductImage();
        String productID = myOrderItemModelList.get(position).getProductID();
        int rating = myOrderItemModelList.get(position).getRating();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String orderStatus = myOrderItemModelList.get(position).getDeliveryStatus();
        Date date;
        switch (orderStatus){
            case "ORDERED":
                date = myOrderItemModelList.get(position).getOrderedDate();
                break;
            case "SHIPPED":
                date = myOrderItemModelList.get(position).getShippedDate();
                break;
            case "PACKED":
                date = myOrderItemModelList.get(position).getPackedDate();
                break;
            case "DELIVERED":
                date = myOrderItemModelList.get(position).getDeliveredDate();
                break;
            case "Cancelled":
                date = myOrderItemModelList.get(position).getCancelledDate();
                break;
            default:
                date = myOrderItemModelList.get(position).getOrderedDate();
        }
        String deliveredDate = myOrderItemModelList.get(position).getDeliveryStatus();
        holder.setData(resource, title,orderStatus, date, rating, productID, position);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private ImageView deliveryIndicator;
        private TextView productTitle;
        private TextView deliveryStatus;
        private LinearLayout rateNowContainer;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            productImage =itemView.findViewById(R.id.product_image);
            productTitle =itemView.findViewById(R.id.product_title);
            deliveryIndicator =itemView.findViewById(R.id.my_order_status_indicator);
            deliveryStatus = itemView.findViewById(R.id.order_delivered_date);
            rateNowContainer = itemView.findViewById(R.id.rate_now_container);

        }

        private void setData(String resource, String title, String orderStatus, Date date, int rating, final String productID, int position){
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);
            if (orderStatus.equals("Cancelled")) {
                deliveryIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
            }else {
                deliveryIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.green)));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
            deliveryStatus.setText(orderStatus + String.valueOf(simpleDateFormat.format(date)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(), OrderDetailsAcitivty.class);
                    orderDetailsIntent.putExtra("Position", position);
                    itemView.getContext().startActivity(orderDetailsIntent);
                }
            });

            //rating layout
            setRating(rating);
            for (int x = 0; x < rateNowContainer.getChildCount(); x++){
                final int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        setRating(starPosition);
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID);
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
                                if (DBqueries.myRateIds.contains(productID)) {
                                    myrating.put("rating_" + DBqueries.myRateIds.indexOf(productID), (long) starPosition + 1);
                                } else {
                                    myrating.put("list_size", (long) DBqueries.myRateIds.size() + 1);
                                    myrating.put("product_ID_" + DBqueries.myRateIds.size(), productID);
                                    myrating.put("rating_" + DBqueries.myRateIds.size(), (long) starPosition + 1);
                                }
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                        .document("MY_RATINGS").update(myrating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    DBqueries.myOrderItemModelList.get(position).setRating(starPosition);
                                                    if (DBqueries.myRateIds.contains(productID)){
                                                        DBqueries.myRating.set(DBqueries.myRateIds.indexOf(productID), Long.parseLong(String.valueOf(starPosition+1)));
                                                    }else {
                                                        DBqueries.myRateIds.add(productID);
                                                        DBqueries.myRating.add(Long.parseLong(String.valueOf(starPosition+1)));
                                                    }
                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
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
}
