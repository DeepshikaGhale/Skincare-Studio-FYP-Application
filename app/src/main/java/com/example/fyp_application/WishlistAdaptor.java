package com.example.fyp_application;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdaptor extends RecyclerView.Adapter<WishlistAdaptor.ViewHolder> {

    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;
    private boolean fromSearch;

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdaptor(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productId = wishlistModelList.get(position).getProductId();
        String resources = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        String rating = wishlistModelList.get(position).getRating();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();
        holder.setData(productId, resources, title, rating, productPrice, paymentMethod, position, inStock);
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private TextView ratings;
        private TextView productPrice;
        private TextView paymentMethod;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            ratings = itemView.findViewById(R.id.tv_product_rating_miniview);
            productPrice = itemView.findViewById(R.id.product_price);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
        private void setData(final String productId, String resource, String title, String averageRating, String price, boolean COD, final int index, boolean inStock){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.home)).into(productImage);
            productTitle.setText(title);
            LinearLayout linearLayout = (LinearLayout) ratings.getParent();

            if (inStock) {
                ratings.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                ratings.setText(averageRating);
                productPrice.setText("$" + price);
                if (COD) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }else {
                linearLayout.setVisibility(View.INVISIBLE);
                ratings.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
                paymentMethod.setVisibility(View.INVISIBLE);
            }
            if (wishlist){
                deleteBtn.setVisibility(View.VISIBLE);
            }else {
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_wishlist_query){
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBqueries.removeFromWishlist(index, itemView.getContext());
                    }

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromSearch){
                        ProductDetailsActivity.fromSearch = true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
