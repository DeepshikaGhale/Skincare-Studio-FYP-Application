package com.example.fyp_application;

import android.app.Dialog;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CartAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CartItemModel> cartItemModelList;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public CartAdaptor(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount =cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                String price = cartItemModelList.get(position).getProductPrice();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuatity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean cod = cartItemModelList.get(position).isCOD();

                ((CartItemViewHolder)holder).setItemDetails(productID, resource, title, price, position, inStock,  String.valueOf(productQuantity), maxQuantity, cod);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemsPrice = 0;
                String deliveryPrice;
                int totalAmount;

                for (int x = 0; x < cartItemModelList.size(); x++){
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                        totalItems++;
                        totalItemsPrice = totalItemsPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice());
                    }
                }

                if (totalItemsPrice < 100){
                    deliveryPrice = "FREE";
                    totalAmount = totalItemsPrice;
                }else {
                    deliveryPrice = "5";
                    totalAmount = totalItemsPrice + 5;
                }

                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemsPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                ((CartTotalAmountViewHolder)holder).setTotalAmount(totalItems, totalItemsPrice, deliveryPrice, totalAmount);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }


    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView productQuantity;
        private LinearLayout deleteBtn;
        private ImageView codIndicator;


        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity_container);

            deleteBtn = itemView.findViewById(R.id.remove_item_button);
            codIndicator = itemView.findViewById(R.id.cod_indicator);
        }

        private void setItemDetails(String productID, String resource, String title, String productPriceText, final int position, boolean inStock, final String quantity,
                                    final Long maxQuantity, boolean cod) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.home)).into(productImage);
            productTitle.setText(title);
            if (cod){
                codIndicator.setVisibility(View.VISIBLE);
            }else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock){
                productPrice.setText("$" + productPriceText);
                productQuantity.setText("Qty:" + quantity);
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);

                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_count);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max Limit" + String.valueOf(maxQuantity));

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {
                                        if (itemView.getContext() instanceof navigationbar){
                                            DBqueries.cartItemModelList.get(position).setProductQuatity(Long.valueOf(quantityNo.getText().toString()));
                                        }else {
                                            if (DeliveryActivity.fromCart) {
                                                DBqueries.cartItemModelList.get(position).setProductQuatity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuatity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty:" + quantityNo.getText());
                                    }else {
                                        Toast.makeText(itemView.getContext(), "Max Quantity:" + maxQuantity.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });

                        quantityDialog.show();
                    }
                });
            }else {
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.green));

                productQuantity.setVisibility(View.INVISIBLE);
            }

            if (showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else {
                deleteBtn.setVisibility(View.GONE);
            }


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {
        private TextView totaltems;
        private TextView totalItemsPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;


        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totaltems = itemView.findViewById(R.id.total_items);
            totalItemsPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_charge);
            totalAmount = itemView.findViewById(R.id.total_price);
        }

        private void setTotalAmount(int totalItemText, int totalItemsPriceText, String deliveryPriceText, int totalAmounttext) {
            totaltems.setText("Price("+totalItemText+" items)");
            totalItemsPrice.setText("$"+totalItemsPriceText+"/-");
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            }else {
                deliveryPrice.setText("$"+deliveryPriceText+"/-");
            }
            totalAmount.setText("$"+totalAmounttext+"/-");
            cartTotalAmount.setText("$"+totalAmounttext+"/-");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemsPriceText == 0){
                DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size()-1);
                parent.setVisibility(View.GONE);
            }else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}