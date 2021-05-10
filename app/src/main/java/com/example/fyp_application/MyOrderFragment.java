package com.example.fyp_application;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrderFragment extends Fragment {

    public MyOrderFragment() {
        // Required empty public constructor
    }


    private RecyclerView myOrderRecyclerView;
    public static MyOrderAdaptor myOrderAdaptor;
    private Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        //loading diaglog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        myOrderRecyclerView = view.findViewById(R.id.my_order_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myOrderRecyclerView.setLayoutManager(layoutManager);

        myOrderAdaptor = new MyOrderAdaptor(DBqueries.myOrderItemModelList, loadingDialog);
        myOrderRecyclerView.setAdapter(myOrderAdaptor);

        DBqueries.loadOrders(getContext(), myOrderAdaptor, loadingDialog);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myOrderAdaptor.notifyDataSetChanged();
    }
}
