package com.example.fyp_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSpecificationFragment extends Fragment {

    public ProductSpecificationFragment() {
        // Required empty public constructor
    }

    private RecyclerView productSpecificationRecyclerView;
    public List<ProductSpecificationModel> productSpecificationModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =   inflater.inflate(R.layout.fragment_product_specification, container, false);

       productSpecificationRecyclerView = view.findViewById(R.id.product_specification_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);



//        productSpecificationModelList.add(new ProductSpecificationModel("Melting Point", "105.0~109.0"));
//        productSpecificationModelList.add(new ProductSpecificationModel("Melting Point", "105.0~109.0"));
//        productSpecificationModelList.add(new ProductSpecificationModel("Melting Point", "105.0~109.0"));
//        productSpecificationModelList.add(new ProductSpecificationModel("Melting Point", "105.0~109.0"));

        ProductSpecificationAdaptor productSpecificationAdaptor = new ProductSpecificationAdaptor(productSpecificationModelList);
        productSpecificationRecyclerView.setAdapter(productSpecificationAdaptor);
        productSpecificationAdaptor.notifyDataSetChanged();
       return view;
    }
}
