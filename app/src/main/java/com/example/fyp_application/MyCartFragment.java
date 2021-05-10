package com.example.fyp_application;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCartFragment extends Fragment {

    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartRecyclerView;
    private Button cartContinue;
    private Dialog loadingDialog;
    public static CartAdaptor cartAdaptor;
    private TextView totalAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_cart, container, false);
        //loading diaglog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        cartRecyclerView = view.findViewById(R.id.cart_item_recycer_view);
        cartContinue = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_cart_amount);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecyclerView.setLayoutManager(linearLayoutManager);

        cartAdaptor = new CartAdaptor(DBqueries.cartItemModelList, totalAmount, true);
        cartRecyclerView.setAdapter(cartAdaptor);
        cartAdaptor.notifyDataSetChanged();

        cartContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;

                for (int x = 0; x < DBqueries.cartItemModelList.size(); x++){
                    CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }

                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                loadingDialog.show();
                if (DBqueries.addressesModelList.size() == 0) {
                    DBqueries.loadAddresses(getContext(), loadingDialog, true);
                }else {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (DBqueries.cartItemModelList.size() == 0){
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        }else {
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size()-1).getType() == CartItemModel.TOTAL_AMOUNT){
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

    }
}
