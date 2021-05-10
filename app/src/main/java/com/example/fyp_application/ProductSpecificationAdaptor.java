package com.example.fyp_application;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

class ProductSpecificationAdaptor extends RecyclerView.Adapter<ProductSpecificationAdaptor.ViewHolder> {

    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductSpecificationAdaptor(List<ProductSpecificationModel> productSpecificationModelList) {
        this.productSpecificationModelList = productSpecificationModelList;
    }


    @NonNull
    @Override
    public ProductSpecificationAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecificationAdaptor.ViewHolder holder, int position) {
        String featureTitle = productSpecificationModelList.get(position).getFeature_name();
        String featureDetails = productSpecificationModelList.get(position).getFeature_value();
        holder.setFeatures(featureTitle, featureDetails);
    }

    @Override
    public int getItemCount() {
        return productSpecificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView featureName;
        private TextView featureValue;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            featureName = itemView.findViewById(R.id.feature_name);
            featureValue = itemView.findViewById(R.id.feature_value);
        }

        private void setFeatures(String featureTitle, String featureDetails) {

            featureName.setText(featureTitle);
            featureValue.setText(featureDetails);
        }

    }

    private int setDp(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
