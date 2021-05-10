package com.example.fyp_application;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.fyp_application.navigationbar.showCart;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;
    public static Activity productDetailsActvity;

    public static boolean fromSearch = false;

    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView productPrice;
    private TextView codindicator;

    //Product Description
    private static String productDescription;
    private static String productOtherDetails;
    private TabLayout viewPagerIndicator;

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    //Product Description
    //rating layout
    public static int initialRating;
    private TextView averageRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsLayoutContainer;
    private LinearLayout ratingsProgressBarContainer;
    //rating layout

    private Dialog signInDailog;
    private TextView badgeCount;

    private Button buyNowBtn;
    private LinearLayout addToCartBtn;
    public static MenuItem cartItem;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton addToWishlistBtn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    public static String productID;

    private Dialog loadingDialog;

    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Product Details Activity");

        productImagesViewPager = findViewById(R.id.products_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishlistBtn = findViewById(R.id.add_to_wishlist_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        productPrice = findViewById(R.id.product_price);
        codindicator = findViewById(R.id.cod);
        productDetailsTabsContainer = findViewById(R.id.product_detals_tab_container);

        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tab_layout);

        initialRating = -1;
        totalRatings = findViewById(R.id.total_ratings);
        ratingsLayoutContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progress_bar_container);
        averageRating = findViewById(R.id.average_rating);

        buyNowBtn = findViewById(R.id.buy_now_btn);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        //loading diaglog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        final List<String> productImages = new ArrayList<>();
        productID = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    documentSnapshot = task.getResult();
                    for (long x = 1; x < (long)documentSnapshot.get("no_of_product_images") +1; x++){
                        productImages.add(documentSnapshot.get("product_image_" + x).toString());
                    }
                    ProductImagesAdaptor productImagesAdaptor = new ProductImagesAdaptor(productImages);
                    productImagesViewPager.setAdapter(productImagesAdaptor);

                    productTitle.setText(documentSnapshot.get("product_subtitle").toString());
                    averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                    productPrice.setText("$" + documentSnapshot.get("product_price").toString());
                    if ((boolean)documentSnapshot.get("COD")){
                        codindicator.setVisibility(View.VISIBLE);
                    }
                    else {
                        codindicator.setVisibility(View.INVISIBLE);
                    }

                    if ((boolean)documentSnapshot.get("use_tab_layout")){
                        productDetailsTabsContainer.setVisibility(View.VISIBLE);
                        productDescription = documentSnapshot.get("product_description").toString();
                        productSpecificationModelList.add(new ProductSpecificationModel(
                                documentSnapshot.get("spec_title_1_field_1_name").toString(), documentSnapshot.get("spec_title_1_field_1_value").toString()));
                        productOtherDetails = documentSnapshot.get("product_other_details").toString();
                    }else {
                        Toast.makeText(ProductDetailsActivity.this, "None", Toast.LENGTH_SHORT).show();
                    }
                    totalRatings.setText((long)documentSnapshot.get("total_ratings") + 1 + " Ratings");

                    for (int x = 0; x < 5; x++){
                        TextView rating = (TextView) ratingsLayoutContainer.getChildAt(x);
                        rating.setText(String.valueOf((long)documentSnapshot.get(5-x +"_star")));
//
                        ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                        int maxProgress = Integer.parseInt(String.valueOf((long)documentSnapshot.get("total_ratings")));
                        progressBar.setMax(maxProgress);
                        progressBar.setProgress(Integer.parseInt(String.valueOf((long)documentSnapshot.get( (5-x) +"_star"))));
                    }

                    totalRatingsFigure.setText(String.valueOf((long)documentSnapshot.get("total_ratings")));
                    productDetailsViewPager.setAdapter(new ProductDetailsAdaptor(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));
                    averageRating.setText(documentSnapshot.get("average_rating").toString());
                    //for wishlist
                    if  (currentUser != null) {
                        if (DBqueries.myRating.size() == 0){
                            DBqueries.loadRatingList(ProductDetailsActivity.this);
                        }
                        if (DBqueries.cartList.size() == 0){
                            DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                        }
                        if (DBqueries.wishList.size() == 0) {
                            DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                        } else {
                            loadingDialog.dismiss();
                        }
                    }else{
                        loadingDialog.dismiss();
                    }
                    if (DBqueries.myRateIds.contains(productID)) {
                        int index = DBqueries.myRateIds.indexOf(productID);
                        initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index)))-1;
                        setRating(initialRating);
                    }
                    if (DBqueries.cartList.contains(productID)){
                        ALREADY_ADDED_TO_CART = true;
                    }
                    else {
                        ALREADY_ADDED_TO_CART = false;
                    }
                    if (DBqueries.wishList.contains(productID)){
                        ALREADY_ADDED_TO_WISHLIST = true;
                        addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimaryDark, getApplicationContext().getTheme()));
                    }
                    else {
                        addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        ALREADY_ADDED_TO_WISHLIST = false;
                    }
                    if((boolean)documentSnapshot.get("in_stock")){
                        addToCartBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentUser == null){
                                    signInDailog.show();
                                }else {
                                    if (!running_cart_query) {
                                        running_cart_query = true;
                                        if (ALREADY_ADDED_TO_CART) {
                                            running_cart_query = false;
                                            Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Map<String, Object> addproduct = new HashMap<>();
                                            addproduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                            addproduct.put("list_size", (long) DBqueries.cartList.size() + 1);

                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                    .document("MY_CART").update(addproduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (DBqueries.cartItemModelList.size() != 0) {
                                                            DBqueries.cartItemModelList.add(0, new CartItemModel(
                                                                    documentSnapshot.getBoolean("COD")
                                                                    ,CartItemModel.CART_ITEM
                                                                    ,productID, documentSnapshot.get("product_image_1").toString(),
                                                                    documentSnapshot.get("product_subtitle").toString(),
                                                                    documentSnapshot.get("product_price").toString(),
                                                                    (long)1,
                                                                    (boolean)documentSnapshot.get("in_stock"),
                                                                    (long)documentSnapshot.get("max_quantity"),
                                                                    (long)documentSnapshot.get("stock_quantity")));
                                                        }
                                                        ALREADY_ADDED_TO_CART = true;
                                                        DBqueries.cartList.add(productID);
                                                        Toast.makeText(ProductDetailsActivity.this, "Added to Cart successfully!", Toast.LENGTH_SHORT).show();
                                                        invalidateOptionsMenu();
                                                        running_cart_query = false;
                                                    }
                                                    else {
                                                        running_cart_query = false;
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                        });
                    }else {
                        buyNowBtn.setVisibility(View.GONE);
                        TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                        outOfStock.setText("Out of Stock");
                        outOfStock.setTextColor(getResources().getColor(R.color.green));
                        outOfStock.setCompoundDrawables(null, null, null, null);
//////                        /
                    }
                }else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //connecting viewpager and indicator
        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    signInDailog.show();
                }else {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBqueries.wishList.indexOf(productID);
                            DBqueries.removeFromWishlist(index, ProductDetailsActivity.this);
                            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        } else {
                            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimaryDark, getApplicationContext().getTheme()));
                            Map<String, Object> addproduct = new HashMap<>();
                            addproduct.put("product_ID_" + String.valueOf(DBqueries.wishList.size()), productID);
                            addproduct.put("list_size", (long) DBqueries.wishList.size() + 1);
                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                    .document("MY_WISHLIST").update(addproduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        if (DBqueries.wishlistModelList.size() != 0) {
                                            DBqueries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString(),
                                                    documentSnapshot.get("product_subtitle").toString(),
                                                    documentSnapshot.get("average_rating").toString(),
                                                    documentSnapshot.get("product_price").toString(),
                                                    (boolean) documentSnapshot.get("COD"),
                                                    (boolean) documentSnapshot.get("in_stock")));
                                        }
                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimaryDark, getApplicationContext().getTheme()));
                                        DBqueries.wishList.add(productID);
                                        Toast.makeText(ProductDetailsActivity.this, "Added to wishlist successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;
                                }
                            });

                        }
                    }
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //rating layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++){
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDailog.show();
                    }else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;

                                setRating(starPosition);
                                Map<String, Object> updateRating = new HashMap<>();
                                if (DBqueries.myRateIds.contains(productID)) {

                                    TextView oldRating = (TextView) ratingsLayoutContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingsLayoutContainer.getChildAt(5 - starPosition - 1);

                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
                                } else {
                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }
                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myrating = new HashMap<>();
                                            if (DBqueries.myRateIds.contains(productID)) {
                                                myrating.put("rating_" + DBqueries.myRateIds.indexOf(productID), (long) starPosition + 1);
                                            } else {
                                                myrating.put("list_size", (long) DBqueries.myRateIds.size() + 1);
                                                myrating.put("product_ID_" + DBqueries.myRateIds.size(), productID);
                                                myrating.put("rating_" + DBqueries.myRateIds.size(), (long) starPosition + 1);
                                            }
                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                    .document("MY_RATINGS").update(myrating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (DBqueries.myRateIds.contains(productID)) {
                                                            DBqueries.myRating.set(DBqueries.myRateIds.indexOf(productID), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingsLayoutContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingsLayoutContainer.getChildAt(5 - starPosition - 1);
                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                        } else {
                                                            DBqueries.myRateIds.add(productID);
                                                            DBqueries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingsLayoutContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " Ratings");
                                                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));
                                                            Toast.makeText(ProductDetailsActivity.this, "Thank you for rating.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingfigures = (TextView) ratingsLayoutContainer.getChildAt(x);
                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);

                                                            int maxProgress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt(ratingfigures.getText().toString()));
                                                        }
                                                        initialRating = starPosition;
                                                        averageRating.setText(calculateAverageRating(0, true));
                                                        averageRatingMiniView.setText(calculateAverageRating(0, true));

                                                        if (DBqueries.wishList.contains(productID) && DBqueries.wishlistModelList.size() != 0) {
                                                            int index = DBqueries.wishList.indexOf(productID);
                                                            DBqueries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                        }
                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
        //rating layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    signInDailog.show();
                }else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActvity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel( documentSnapshot.getBoolean("COD"),
                            CartItemModel.CART_ITEM
                            ,productID, documentSnapshot.get("product_image_1").toString(),
                            documentSnapshot.get("product_subtitle").toString(),
                            documentSnapshot.get("product_price").toString(),
                            (long)1,
                            (boolean)documentSnapshot.get("in_stock"),
                            (long)documentSnapshot.get("max_quantity"),
                            (long)documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                    if (DBqueries.addressesModelList.size() == 0) {
                        DBqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog, true);
                    }else {
                        loadingDialog.dismiss();
                        Intent intent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });


        ///signIn Dialogue
        signInDailog = new Dialog(ProductDetailsActivity.this);
        signInDailog.setContentView(R.layout.sign_in_dialog);
        signInDailog.setCancelable(true);
        signInDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDailog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDailog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegsiterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableClosedButton = true;
                SignUpFragment.disableClosedButton = true;
                signInDailog.dismiss();
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment.disableClosedButton = true;
                SignInFragment.disableClosedButton = true;
                signInDailog.dismiss();
                startActivity(registerIntent);
            }
        });
    ///signInDialogue
    }

    @Override
    protected void onStart() {
        super.onStart();

        if  (currentUser != null) {
            if (DBqueries.myRating.size() == 0){
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            } else {
                loadingDialog.dismiss();
            }
        }else{
            loadingDialog.dismiss();
        }

        if (DBqueries.myRateIds.contains(productID)) {
        int index = DBqueries.myRateIds.indexOf(productID);
        initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) -1;
        setRating(initialRating);
        }
        if (DBqueries.cartList.contains(productID)){
            ALREADY_ADDED_TO_CART = true;
        }
        else {
            ALREADY_ADDED_TO_CART = false;
        }
        if (DBqueries.wishList.contains(productID)){
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimaryDark, getApplicationContext().getTheme()));
        }
        else {
            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    public static void setRating(int starPosition) {
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starButton = (ImageView) rateNowContainer.getChildAt(x);
                starButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (x <= starPosition) {
                    starButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
    }

    private String calculateAverageRating(long currentUserRating, boolean update){
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++){
            TextView ratingNo = (TextView) ratingsLayoutContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString())*x);
        }
        totalStars =totalStars + currentUserRating;
        if (update){
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFigure.getText().toString())).substring(0,3);
        }else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0,3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.cart_icon);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.mipmap.cart_icon);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null){
            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            }else {
                badgeCount.setVisibility(View.VISIBLE);
                if (DBqueries.cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                } else {
                    badgeCount.setText("99");
                }
            }
        }
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDailog.show();
                    } else {
                        Intent cartintent = new Intent(ProductDetailsActivity.this, navigationbar.class);
                        showCart = true;
                        startActivity(cartintent);
                    }
                }
            });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        if (id == android.R.id.home){
            productDetailsActvity = null;
            finish();
            return true;
        }
        if (id == R.id.search_icon){
            if (fromSearch){
                finish();
            }
            else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        }
        else if (id == R.id.cart_icon) {
            if (currentUser == null) {
                signInDailog.show();
            } else {
                Intent cartintent = new Intent(ProductDetailsActivity.this, navigationbar.class);
                showCart = true;
                startActivity(cartintent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }

    @Override
    public void onBackPressed() {
        productDetailsActvity = null;
        super.onBackPressed();
    }
}
