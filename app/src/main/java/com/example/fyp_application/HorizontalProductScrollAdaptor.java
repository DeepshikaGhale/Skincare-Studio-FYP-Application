package com.example.fyp_application;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HorizontalProductScrollAdaptor extends RecyclerView.Adapter<HorizontalProductScrollAdaptor.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdaptor(List<HorizontalProductScrollModel> horizontalProductScrollModels) {
        this.horizontalProductScrollModelList = horizontalProductScrollModels;
    }



    @NonNull
    @Override
    public HorizontalProductScrollAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdaptor.ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();
        String productID = horizontalProductScrollModelList.get(position).getProductID();
        holder.setData(productID, resource, title, description, price);
    }

    @Override
    public int getItemCount() {
        if (horizontalProductScrollModelList.size() >8){
            return 8;
        }
        else {
            return horizontalProductScrollModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productDescription;
        private TextView productPrice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_title);
            productDescription = itemView.findViewById(R.id.h_s_product_desc);
            productPrice = itemView.findViewById(R.id.h_s_product_price);
        }

        private void setData(final String product_ID, String resource, String title, String description, String price){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.moistorizer)).into(productImage);
            productTitle.setText(title);
            productDescription.setText(description);
            productPrice.setText("$"+ price + "/-");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", product_ID);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });

        }



//        private void setProductImage(String resource){
//            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.moistorizer)).into(productImage);
//        }
//
//        private void setProductTitle(String title){
//            productTitle.setText(title);
//        }
//
//        private void setProductDescription(String description){
//            productDescription.setText(description);
//        }
//
//        private void setProductPrice(String price){
//            productPrice.setText(price + "/-");
//        }

    }
}
