package com.example.fyp_application;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private Button viewAllAddressBtn;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton settingsBtn;
    public static final int MANAGE_ADDRESS = 1;
    private LinearLayout layoutContainer, recentOrdersContainer;
    private CircleImageView profileView, currentOrderImage;
    private TextView name, email, tvCurrentOrderStatus;
    private Dialog loadingDialog;
    private ImageView orderIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_ProgressBar, P_S_ProgressBar, S_D_ProgressBar;
    private TextView yourRecentOrdersTitle;
    private TextView address, Name, Pincode;
    private Button signOutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        layoutContainer = view.findViewById(R.id.layout_container);

        //loading diaglog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        profileView = view.findViewById(R.id.profile_image);
        email = view.findViewById(R.id.user_email);
        name = view.findViewById(R.id.username);
        viewAllAddressBtn = view.findViewById(R.id.view_all_address);
        currentOrderImage = view.findViewById(R.id.current_order_imae);
        tvCurrentOrderStatus = view.findViewById(R.id.current_order_status);
        orderIndicator = view.findViewById(R.id.ordered_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippedIndicator = view.findViewById(R.id.shipped_in);
        deliveredIndicator = view.findViewById(R.id.delivered_indicator);
        O_P_ProgressBar = view.findViewById(R.id.order_packed_progress);
        S_D_ProgressBar = view.findViewById(R.id.shipped_delivered_progress);
        P_S_ProgressBar = view.findViewById(R.id.packed_shipped_indicator);
        yourRecentOrdersTitle = view.findViewById(R.id.tv_recent_orders);
        recentOrdersContainer = view.findViewById(R.id.recent_order_container);
        address = view.findViewById(R.id.address);
        Name = view.findViewById(R.id.address_fullname);
        Pincode = view.findViewById(R.id.address_pincode);
        signOutBtn = view.findViewById(R.id.sign_out_btn);
        settingsBtn = view.findViewById(R.id.setting_btn);

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (MyOrderItemModel orderItemModel : DBqueries.myOrderItemModelList){
                    if (!orderItemModel.isCancellationEquested()){
                        if (!orderItemModel.getDeliveryStatus().equals("DELIVERED") && !orderItemModel.getDeliveryStatus().equals("Cancelled")){
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.mymall)).into(currentOrderImage);
                            tvCurrentOrderStatus.setText(orderItemModel.getDeliveryStatus());
                            switch (orderItemModel.getDeliveryStatus()){
                                case "ORDERED":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    break;

                                case "PACKED":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    O_P_ProgressBar.setProgress(100);
                                    break;

                                case "SHIPPED":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    O_P_ProgressBar.setProgress(100);
                                    P_S_ProgressBar.setProgress(100);
                                    break;

                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

                                    O_P_ProgressBar.setProgress(100);
                                    P_S_ProgressBar.setProgress(100);
                                    S_D_ProgressBar.setProgress(100);
                                    break;
                            }
                        }
                    }
                }
                int i = 0;
                for (MyOrderItemModel myOrderItemModel : DBqueries.myOrderItemModelList) {
                    if (i < 4) {
                        if (myOrderItemModel.getDeliveryStatus().equals("DELIVERED")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.mymall)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                            i++;
                        }
                    }else {
                        break;
                    }
                }
                if (i == 0){
                    yourRecentOrdersTitle.setText("No Recent Orders.");
                }
                if (i < 3){
                    for (int x = i; x < 4; x++){
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DBqueries.addressesModelList.size() == 0){
                            Name.setText("No Address");
                            address.setText("-");
                            Pincode.setText("-");
                        }else {
                            setAddress();
                        }
                    }
                });
                DBqueries.loadAddresses(getContext(), loadingDialog, false);
            }
        });
        DBqueries.loadOrders(getContext(), null, loadingDialog);

        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressesIntent = new Intent(getContext(), MyAddressActivity.class);
                myAddressesIntent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                Intent registerIntent = new Intent(getContext(), RegsiterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name", name.getText());
                updateUserInfo.putExtra("Email", email.getText());
                updateUserInfo.putExtra("Profile", DBqueries.profile);
                startActivity(updateUserInfo);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        name.setText(DBqueries.fullName);
        email.setText(DBqueries.email);
        Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_icon)).into(profileView);

        if (!loadingDialog.isShowing()){
            if (DBqueries.addressesModelList.size() == 0){
                Name.setText("No Address");
                address.setText("-");
                Pincode.setText("-");
            }else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String nameText, mobileNo;
        nameText = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternativeMobileNo().equals("")) {
            Name.setText(nameText + " - " + mobileNo);
        }
        else {
            Name.setText(nameText + " - " + mobileNo + " OR " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternativeMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFaltNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String district = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landmark.equals("")){
            address.setText(flatNo + " - " + locality + " - " + city + " - " + district);
        }else{
            address.setText(flatNo + " - " + locality + " - " + landmark + " - " + city + " - " + district);
        }

        Pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());
    }
}
