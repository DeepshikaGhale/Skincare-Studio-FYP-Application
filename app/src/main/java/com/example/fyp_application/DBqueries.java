package com.example.fyp_application;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.fyp_application.ProductDetailsActivity.addToWishlistBtn;
import static com.example.fyp_application.ProductDetailsActivity.initialRating;
import static com.example.fyp_application.ProductDetailsActivity.productID;

public class DBqueries {

    public static boolean addressSelected = false;

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email, fullName, profile;

    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRateIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static List<AddressesModel> addressesModelList = new ArrayList<>();
    public static int selectedAddress = -1;

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    public static void loadCategories(final CategoryAdaptor categoryAdaptor, final Context context){
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot :task.getResult()){
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            categoryAdaptor.notifyDataSetChanged();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final HomePageAdaptor adaptor, final Context context, final int index, String categoryName){
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot :task.getResult()){
                                if ((long)documentSnapshot.get("view_type") == 0){
                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

                                    ArrayList<String> productIds = (ArrayList<String>) documentSnapshot.get("products");
                                    for (String productId : productIds) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(productId,
                                                "",
                                                "",
                                                "",
                                                ""
                                        ));

                                        viewAllProductList.add(new WishlistModel(productId,
                                                "",
                                                "",
                                                "",
                                                "",
                                                false,
                                                false));
                                    }
                                     lists.get(index).add(new HomePageModel(0, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));
                                }else if ((long)documentSnapshot.get("view_type") == 1){
                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    ArrayList<String> productIds = (ArrayList<String>) documentSnapshot.get("products");
                                    for (String productId : productIds) {
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(productId,
                                                "",
                                                "",
                                                "",
                                                ""
                                        ));
                                    }
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridLayoutModelList));
                                }
                            }
                            adaptor.notifyDataSetChanged();
                            mainhome.swipeRefreshLayout.setRefreshing(false);
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadProductData){
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_WISHLIST").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    for (long x = 0; x < (long)task.getResult().get("list_size"); x++){
                        wishList.add(task.getResult().get("product_ID_"+x).toString());
                        if (DBqueries.wishList.contains(ProductDetailsActivity.productID)){
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if (addToWishlistBtn != null) {
                                addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimaryDark, context.getApplicationContext().getTheme()));
                            }
                        } else {
                            if (addToWishlistBtn != null) {
                                addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        }
                        if (loadProductData) {
                            wishlistModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        wishlistModelList.add(new WishlistModel(productId,
                                                task.getResult().get("product_image_1").toString(),
                                                task.getResult().get("product_subtitle").toString(),
                                                task.getResult().get("average_rating").toString(),
                                                task.getResult().get("product_price").toString(),
                                                (boolean) task.getResult().get("COD"),
                                                (boolean) task.getResult().get("in_stock")));

                                        MyWishlistFragment.wishlistAdaptor.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromWishlist(final int index, final Context context){
        final String removeProductId = wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishlist = new HashMap<>();
        for (int x = 0; x <wishList.size(); x++){
            updateWishlist.put("product_ID_"+x, wishList.get(x));
        }
        updateWishlist.put("list_size", (long)wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_WISHLIST").set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (wishlistModelList.size() != 0){
                        wishlistModelList.remove(index);
                        MyWishlistFragment.wishlistAdaptor.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    if (addToWishlistBtn != null) {
                        addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimaryDark, context.getApplicationContext().getTheme()));
                    }
                    wishList.add(index, removeProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_wishlist_query = false;
            }
        });
    }

    public static void loadRatingList(final Context context){
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRateIds.clear();
            myRating.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_RATINGS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        List<String> orderProductIds = new ArrayList<>();
                        for (int x =0; x < myOrderItemModelList.size(); x++) {
                            orderProductIds.add(myOrderItemModelList.get(x).getProductID());
                        }
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRateIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));
                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(initialRating);
                                }
                            }

                            if (orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())){
                                myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                            }
                        }
                        if (MyOrderFragment.myOrderAdaptor != null){
                            MyOrderFragment.myOrderAdaptor.notifyDataSetChanged();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount){
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_CART").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    for (long x = 0; x < (long)task.getResult().get("list_size"); x++){
                        cartList.add(task.getResult().get("product_ID_"+x).toString());
                        if (DBqueries.cartList.contains(ProductDetailsActivity.productID)){
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }
                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                            int index = 0;
                                        if (cartList.size() >= 2){
                                            index = cartList.size() -2;
                                        }
                                        cartItemModelList.add(index, new CartItemModel((boolean)task.getResult().get("COD") ,CartItemModel.CART_ITEM
                                                ,productId,
                                                task.getResult().get("product_image_1").toString(),
                                                task.getResult().get("product_subtitle").toString(),
                                                task.getResult().get("product_price").toString(),
                                                (long) 1,
                                                (boolean)task.getResult().get("in_stock"),
                                                (long)task.getResult().get("max_quantity"),
                                                (long)task.getResult().get("stock_quantity")));

                                        if (cartList.size() == 1){
                                            cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                            parent.setVisibility(View.VISIBLE);
                                        }
                                        if (cartList.size() == 0){
                                            cartItemModelList.clear();
                                        }
                                        MyCartFragment.cartAdaptor.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                    if (cartList.size() != 0){
                        badgeCount.setVisibility(View.VISIBLE);
                    }
                    else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }
                    else {
                        badgeCount.setText("99");
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount){
        final String removeProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();

        for (int x = 0; x <cartList.size(); x++){
            updateCartList.put("product_ID_"+x, cartList.get(x));
        }
        updateCartList.put("list_size", (long)cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_CART").set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (cartItemModelList.size() != 0){
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdaptor.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0){
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }
                    Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    cartList.add(index, removeProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, boolean gotoDeliveryActivity){

        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_ADDRESSES").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Intent deliveryIntent = null;
                    if ((long)task.getResult().get("list_size") == 0){
                        deliveryIntent = new Intent(context, AddAdressActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                    }else {

                        for (long x = 1; x < (long)task.getResult().get("list_size") + 1; x++){
                            addressesModelList.add(new AddressesModel(task.getResult().getBoolean("selected_"+x),
                                    task.getResult().getString("city_"+x),
                                    task.getResult().getString("locality_"+x),
                                    task.getResult().getString("flat_no_"+x),
                                    task.getResult().getString("pincode_"+x),
                                    task.getResult().getString("landmark_"+x),
                                    task.getResult().getString("fullname_"+x),
                                    task.getResult().getString("mobile_no_"+x),
                                    task.getResult().getString("alternate_mobile_no_"+x),
                                    task.getResult().getString("district_"+x)));
                            if ((boolean)task.getResult().get("selected_"+x)){
                                selectedAddress = Integer.parseInt(String.valueOf (x - 1));
                            }
                        }
                        if (gotoDeliveryActivity) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if (gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public static void loadOrders(final Context context, @Nullable final MyOrderAdaptor myOrderAdaptor, Dialog loadingdialog){
        myOrderItemModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {

                                                        MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("PRODUCT_ID"),
                                                                orderItems.getString("ORDER_STATUS"),
                                                                orderItems.getString("ADDRESS"),
                                                                orderItems.getString("FULLNAME"),
                                                                orderItems.getDate("ORDER_DATE"),
                                                                orderItems.getDate("PACKED_DATE"),
                                                                orderItems.getDate("SHIPPED_DATE"),
                                                                orderItems.getDate("DELIVERED_DATE"),
                                                                orderItems.getDate("CANCELLED_DATE"),
                                                                orderItems.getString("ORDER_ID"),
                                                                orderItems.getString("PAYMENT_METHOD"),
                                                                orderItems.getString("PINCODE"),
                                                                orderItems.getString("PRODUCT_PRICE"),
                                                                orderItems.getLong("PRODUCT_QUANTITY"),
                                                                orderItems.getString("USER_ID"),
                                                                orderItems.getString("PRODUCT_IMAGE"),
                                                                orderItems.getString("PRODUCT_TITLE"),
                                                                orderItems.getString("DELIVERYPRICE"),
                                                                orderItems.getBoolean("Cancellation requested"));
                                                        myOrderItemModelList.add(myOrderItemModel);
                                                    }
                                                    loadRatingList(context);
                                                    if (myOrderAdaptor!= null) {
                                                        myOrderAdaptor.notifyDataSetChanged();
                                                    }
                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                }
                                                loadingdialog.dismiss();
                                            }
                                        });
                            }
                        }else {
                            loadingdialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void clearData(){
        categoryModelList.clear();
        lists.clear();
        loadCategoriesNames.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRateIds.clear();
        myRating.clear();
        addressesModelList.clear();
        myOrderItemModelList.clear();
    }

}
