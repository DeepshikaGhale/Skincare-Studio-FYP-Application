package com.example.fyp_application;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class navigationbar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;
    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int ACCOUNT_FRAGMENT = 4;
    public static boolean showCart = false;
    public static Activity navigation;
    public  static boolean resetMainActivity = false;


    private TextView actionBarLogo;
    private int currentFragment = -1;
    private NavigationView navigationView;

    public static DrawerLayout drawer;
    private Window window;
    private Dialog signInDailog;

    private TextView badgeCount;
    private FirebaseUser currentUser;

    private int scrollFlags;
    private AppBarLayout.LayoutParams params;
    private CircleImageView profileView;
    private  TextView fullName, emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        actionBarLogo = findViewById(R.id.actionbar_logo);
        setSupportActionBar(toolbar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.main_frame_layout);

        profileView = navigationView.getHeaderView(0).findViewById(R.id.main_profile_image);
        fullName = navigationView.getHeaderView(0).findViewById(R.id.full_name);
        emailAddress = navigationView.getHeaderView(0).findViewById(R.id.email_address);

        if (showCart) {
            navigation = this;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // write 1 to close if this does not work
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new mainhome(), HOME_FRAGMENT);
        }


        ///signIn dialogue
        signInDailog = new Dialog(navigationbar.this);
        signInDailog.setContentView(R.layout.sign_in_dialog);
        signInDailog.setCancelable(true);

        signInDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDailog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDailog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(navigationbar.this, RegsiterActivity.class);


        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableClosedButton = true;
                signInDailog.dismiss();
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment.disableClosedButton = true;
                signInDailog.dismiss();
                startActivity(registerIntent);
            }
        });
        ///signIn dialogue
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //for viewing and functioning of logout button
        if (currentUser == null){
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        }else {
            if (DBqueries.email == null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueries.fullName = task.getResult().getString("fullname");
                            DBqueries.email = task.getResult().getString("email");
                            DBqueries.profile = task.getResult().getString("profile");

                            fullName.setText(DBqueries.fullName);
                            emailAddress.setText(DBqueries.email);
                            Glide.with(navigationbar.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_icon)).into(profileView);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(navigationbar.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                fullName.setText(DBqueries.fullName);
                emailAddress.setText(DBqueries.email);
                Glide.with(navigationbar.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_icon)).into(profileView);
            }
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
        if (resetMainActivity){
            resetMainActivity = false;
            setFragment(new mainhome(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            } else{
                if (showCart){
                    navigation = null;
                    showCart = false;
                    finish();
                }else {
                    actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new mainhome(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.navigationbar, menu);
            MenuItem cartItem = menu.findItem(R.id.cart_icon);
                cartItem.setActionView(R.layout.badge_layout);
                ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
                badgeIcon.setImageResource(R.mipmap.cart_icon);
                badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

                if (currentUser != null){
                    if (DBqueries.cartList.size() == 0) {
                        DBqueries.loadCartList(navigationbar.this, new Dialog(navigationbar.this), false, badgeCount, new TextView(navigationbar.this));
                    }
                    else {
                        badgeCount.setVisibility(View.VISIBLE);
                        if (DBqueries.cartList.size() < 99) {
                            badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                        }
                        else {
                            badgeCount.setText("99");
                        }
                    }
                }

                cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUser == null){
                            signInDailog.show();
                        }else {
                            gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                        }
                    }
                });
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        if (id == R.id.search_icon){
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }
        else if (id == R.id.cart_icon){
            if (currentUser == null){
                signInDailog.show();
            }else {
                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
            }
            return true;
        } else if (id == android.R.id.home){
            if (showCart){
                navigation = null;
                showCart = false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNumber) {
        actionBarLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu(); //to hide other menu's as it restarts onCreatemenu
        setFragment(fragment, fragmentNumber);
        if (fragmentNumber == CART_FRAGMENT || showCart){
            navigationView.getMenu().getItem(3).setChecked(true);
            params.setScrollFlags(0);
        }else {
            params.setScrollFlags(scrollFlags);
        }
    }

    MenuItem menuItem;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem= item;

        if (currentUser != null) {
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    //HANDLES NAVIGATION VIEW ITEMS CLICKED
                    int id = menuItem.getItemId();
                    if (id == R.id.mall) {
                        actionBarLogo.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                        setFragment(new mainhome(), HOME_FRAGMENT);
                    } else if (id == R.id.orders) {
                        gotoFragment("My Orders", new MyOrderFragment(), ORDERS_FRAGMENT);
                    } else if (id == R.id.cart) {
                        gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                    } else if (id == R.id.wishlist) {
                        gotoFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);
                    } else if (id == R.id.account) {
                        gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                    } else if (id == R.id.logout) {
                        FirebaseAuth.getInstance().signOut();
                        DBqueries.clearData();
                        Intent registerIntent = new Intent(navigationbar.this, RegsiterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }
                    drawer.removeDrawerListener(this);
                }
            });
            return true;
        }else {
            signInDailog.show();
            return false;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setFragment(Fragment fragment, int fragmentNumber){
        if (fragmentNumber != currentFragment) {
            currentFragment = fragmentNumber;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }
}
