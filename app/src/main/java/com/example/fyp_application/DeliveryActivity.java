package com.example.fyp_application;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.fyp_application.DBqueries.cartItemModelList;
import static com.example.fyp_application.DBqueries.cartList;
import static com.example.fyp_application.DBqueries.firebaseFirestore;

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRV;
    private Button changeOrAddNewAddressButton;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;

    private TextView fullName;
    private TextView fullAddress;
    private TextView pincode;

    private Button continueBtn;

    private Dialog loadingDialog;
    private Dialog paymentMethodDialog;

    private ImageButton onlinebtn;
    private ImageButton codbtn;

    private String paymentMethod = "Online";

    private String name, mobileNo;
    private String totalPrice;

    private boolean successResponse = false;
    public static boolean fromCart;

    private ConstraintLayout confirmOrderlayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private String order_id;
    public static boolean codOrderConfirmed = false;

    private FirebaseFirestore firebaseFirestore;
    private boolean allProductsAvailable = true;
    public static boolean getQtyIDs = true;

    //for payment dialogs
    private TextView codTitle, onlineTitle;
    private View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        //loading diaglog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //payment method diaglog
        paymentMethodDialog = new Dialog(this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.color.white));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        onlineTitle = paymentMethodDialog.findViewById(R.id.online_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.divider);
        //payment method dialog

        firebaseFirestore = FirebaseFirestore.getInstance();

        order_id = UUID.randomUUID().toString().substring(0, 28);

        deliveryRV = findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressButton = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);

        fullName = findViewById(R.id.fullName);
        fullAddress =findViewById(R.id.address);
        pincode = findViewById(R.id.pinCode);
        onlinebtn = paymentMethodDialog.findViewById(R.id.online);
        codbtn = paymentMethodDialog.findViewById(R.id.cod);
        continueBtn = findViewById(R.id.cart_continue_btn);
        confirmOrderlayout =findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shhopping_btn);
        orderId = findViewById(R.id.order_id);

        deliveryRV = findViewById(R.id.delivery_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRV.setLayoutManager(linearLayoutManager);

        CartAdaptor cartAdaptor = new CartAdaptor(cartItemModelList, totalAmount, false);
        deliveryRV.setAdapter(cartAdaptor);
        cartAdaptor.notifyDataSetChanged();

        changeOrAddNewAddressButton.setVisibility(View.VISIBLE);
        changeOrAddNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs= false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressActivity.class);
                myAddressesIntent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductsAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList){
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            codbtn.setEnabled(false);
                            codbtn.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            codbtn.setEnabled(true);
                            codbtn.setAlpha(1f);
                            codTitle.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductsAvailable){
                    paymentMethodDialog.show();
                }
            }
        });

        codbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                placeOrderDetails();
            }
        });

        onlinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "Online";
                placeOrderDetails();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //accessing quantity
        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y = 0; y < cartItemModelList.get(x).getProductQuatity(); y++){
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0,20);
                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());

                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DBqueries.cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);
                                    if (finalY + 1  == cartItemModelList.get(finalX).getProductQuatity()){
                                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            List<String> serverQuantity = new ArrayList<>();
                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                            }

                                                            for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()){
                                                                if (!serverQuantity.contains(qtyId)){
                                                                    Toast.makeText(DeliveryActivity.this, "Sorry, all the products may not be available in required quantity.", Toast.LENGTH_SHORT).show();
                                                                    allProductsAvailable = false;
                                                                }
                                                                if (serverQuantity.size() >= cartItemModelList.get(finalX).getStockQuantity()){
                                                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).update("in_stock", false);
                                                                }
                                                            }
                                                        }else {
                                                            //error
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }

                                }
                            });
                }
            }
        }else {
            getQtyIDs = true;
        }
        //accessing quantity

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternativeMobileNo().equals("")) {
            fullName.setText(name + " - " + mobileNo);
        }
        else {
            fullName.setText(name + " - " + mobileNo + " OR " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternativeMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFaltNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String district = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landmark.equals("")){
            fullAddress.setText(flatNo + " - " + locality + " - " + city + " - " + district);
        }else{
            fullAddress.setText(flatNo + " - " + locality + " - " + landmark + " - " + city + " - " + district);
        }

        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

        if (codOrderConfirmed){
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))){
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                if (task.getResult().getDocuments().size() < cartItemModelList.get(finalX).getStockQuantity()){
                                                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).update("in_stock", true);
                                                                }
                                                            }else {
                                                                //error
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                }else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout(){
        successResponse = true;
        codOrderConfirmed = false;
        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++){
            for(String qtyID : cartItemModelList.get(x).getQtyIDs()){
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
                cartItemModelList.get(x).getQtyIDs().remove(qtyID);
            }
        }

        if (navigationbar.navigation != null){
            navigationbar.navigation.finish();
            navigationbar.navigation = null;
            navigationbar.showCart = false;
        }else {
            navigationbar.resetMainActivity = true;
        }
        if (ProductDetailsActivity.productDetailsActvity != null){
            ProductDetailsActivity.productDetailsActvity.finish();
            ProductDetailsActivity.productDetailsActvity = null;
        }

        if (fromCart){
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < cartList.size(); x++){
                if (!cartItemModelList.get(x).isInStock()){
                    updateCartList.put("product_ID_"+ cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                }else {
                    indexList.add(x);
                }

            }
            updateCartList.put("list_size", cartListSize );

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_CART").set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        for (int x = 0; x < indexList.size(); x++){
                            cartList.remove(indexList.get(x).intValue());
                            cartItemModelList.remove(indexList.get(x).intValue());
                            cartItemModelList.remove(cartItemModelList.size()-1);
                        }
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }

        continueBtn.setEnabled(false);
        changeOrAddNewAddressButton.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order ID" + order_id);
        confirmOrderlayout.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void placeOrderDetails(){
        String userId = FirebaseAuth.getInstance().getUid();
        loadingDialog.show();
        for (CartItemModel cartItemModel : cartItemModelList){
            if (cartItemModel.getType() == CartItemModel.CART_ITEM){

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER_ID", order_id);
                orderDetails.put("PRODUCT_ID", cartItemModel.getProductID());
                orderDetails.put("PRODUCT_IMAGE", cartItemModel.getProductImage());
                orderDetails.put("PRODUCT_TITLE", cartItemModel.getProductTitle());
                orderDetails.put("USER_ID", userId);
                orderDetails.put("PRODUCT_QUANTITY", cartItemModel.getProductQuatity());
                orderDetails.put("PRODUCT_PRICE", cartItemModel.getProductPrice());
                orderDetails.put("ORDER_DATE", FieldValue.serverTimestamp());
                orderDetails.put("PACKED_DATE", FieldValue.serverTimestamp());
                orderDetails.put("SHIPPED_DATE", FieldValue.serverTimestamp());
                orderDetails.put("DELIVERED_DATE", FieldValue.serverTimestamp());
                orderDetails.put("CANCELLED_DATE", FieldValue.serverTimestamp());
                orderDetails.put("ORDER_STATUS", "ORDERED");
                orderDetails.put("PAYMENT_METHOD", paymentMethod);
                orderDetails.put("ADDRESS", fullAddress.getText());
                orderDetails.put("FULLNAME", fullName.getText());
                orderDetails.put("PINCODE", pincode.getText());
                orderDetails.put("DELIVERYPRICE", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("Cancellation requested", false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total_Items", cartItemModel.getTotalItems());
                orderDetails.put("Total_Items_Price", cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery_Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total_Amount", cartItemModel.getTotalAmount());
                orderDetails.put("PAYMENT_STATUS", "Not Paid");
                orderDetails.put("ORDER_STATUS", "Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (paymentMethod.equals("Online")){
                                online();
                            }else {
                                cod();
                            }
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void online(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Toast.makeText(DeliveryActivity.this, "Online Payment", Toast.LENGTH_SHORT).show();
        Intent intent  = new Intent(DeliveryActivity.this, CheckoutActivity.class);
        intent.putExtra("totalAmount", totalAmount.getText().toString().substring(1,totalAmount.getText().length()-2));
        intent.putExtra("orderID", order_id);
        startActivity(intent);
    }

    private void cod(){
        getQtyIDs =false;
        paymentMethodDialog.dismiss();
        Toast.makeText(DeliveryActivity.this, "Cash on Delivery", Toast.LENGTH_SHORT).show();
        Intent intent  = new Intent(DeliveryActivity.this, OTP_Verification_Activity.class);
        intent.putExtra("mobileNo", mobileNo.substring(0, 10));
        intent.putExtra("orderID", order_id);
        startActivity(intent);
    }
}
