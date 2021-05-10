package com.example.fyp_application;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWishlistFragment extends Fragment {

    private RecyclerView wishlistRecyclerView;
    private Dialog loadingDialog;
    public static WishlistAdaptor wishlistAdaptor;
    public MyWishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_wishlist, container, false);

        //loading diaglog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        wishlistRecyclerView = view.findViewById(R.id.my_wishlist_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        wishlistRecyclerView.setLayoutManager(linearLayoutManager);

        if (DBqueries.wishlistModelList.size() == 0){
            DBqueries.wishList.clear();
            DBqueries.loadWishList(getContext(), loadingDialog, true);
        }else {
            loadingDialog.dismiss();
        }
        wishlistAdaptor  = new WishlistAdaptor(DBqueries.wishlistModelList, true);
        wishlistRecyclerView.setAdapter(wishlistAdaptor);
        wishlistAdaptor.notifyDataSetChanged();

        return view;
    }
}
