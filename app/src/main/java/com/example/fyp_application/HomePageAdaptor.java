package com.example.fyp_application;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomePageAdaptor extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public HomePageAdaptor(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()){
            case 0:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 1:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewholder(horizontalProductView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()){
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String layoutColor = homePageModelList.get(position).getBackgroundColor();
                String horizontalLayoutTitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                ((HorizontalProductViewholder)holder).setHorizontalProductLayout(horizontalProductScrollModelList, horizontalLayoutTitle,layoutColor, viewAllProductList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridLayoutcolor = homePageModelList.get(position).getBackgroundColor();
                String title = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder)holder).setGridProductLayout(gridProductScrollModelList, title, gridLayoutcolor);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class HorizontalProductViewholder extends RecyclerView.ViewHolder{
        private ConstraintLayout container;
        private TextView horizontalLayoutTitle;
        private Button horizontalLayoutViewAllBtn;
        private RecyclerView horizontalRecyclerView;

        public HorizontalProductViewholder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontalLayoutViewAllBtn = itemView.findViewById(R.id.horizontal_scroll_view_all_button);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_scroll_layout_recyclerview);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModels, final String title, String color, final List<WishlistModel> viewAllProductList){
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalLayoutTitle.setText(title);

            for (HorizontalProductScrollModel model: horizontalProductScrollModels){
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()){
                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                model.setProductTitle(task.getResult().getString("product_subtitle"));
                                model.setProductDescription(task.getResult().getString("product_title"));
                                model.setProductImage(task.getResult().getString("product_image_1"));
                                model.setProductPrice(task.getResult().getString("product_price"));

                                WishlistModel wishlistModel = viewAllProductList.get(horizontalProductScrollModels.indexOf(model));
                                wishlistModel.setRating(task.getResult().getString("average_rating"));
                                wishlistModel.setProductTitle(task.getResult().getString("product_subtitle"));
                                wishlistModel.setProductPrice(task.getResult().getString("product_price"));
                                wishlistModel.setProductImage(task.getResult().getString("product_image_1"));
                                wishlistModel.setCOD(task.getResult().getBoolean("COD"));
                                wishlistModel.setInStock(task.getResult().getBoolean("in_stock"));

                                if (horizontalProductScrollModels.indexOf(model) == horizontalProductScrollModels.size() -1){
                                    if (horizontalRecyclerView.getAdapter()!= null){
                                        horizontalRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            }else {
                                //nth
                            }
                        }
                    });
                }
            }

            if (horizontalProductScrollModels.size() > 8){
                horizontalLayoutViewAllBtn.setVisibility(View.VISIBLE);
                horizontalLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code", 0);
                        viewAllIntent.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            }
            else {
                horizontalLayoutViewAllBtn.setVisibility(View.INVISIBLE);
            }

            HorizontalProductScrollAdaptor horizontalProductScrollAdaptor = new HorizontalProductScrollAdaptor(horizontalProductScrollModels);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerView.setLayoutManager(linearLayoutManager);

            horizontalRecyclerView.setAdapter(horizontalProductScrollAdaptor);
            horizontalProductScrollAdaptor.notifyDataSetChanged();
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout constraintLayout;
        private TextView gridLayoutTitle;
        private Button gridLayoutViewAllBtn;
        private GridLayout gridLayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.container);
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_layout_title);
            gridLayoutViewAllBtn = itemView.findViewById(R.id.grid_product_layout_viewall_button);
            gridLayout = itemView.findViewById(R.id.grid_product_layout_gridview);
        }

        private void setGridProductLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModels, final String title, String color){
            constraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            gridLayoutTitle.setText(title);

            for (HorizontalProductScrollModel model: horizontalProductScrollModels){
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()){
                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                model.setProductTitle(task.getResult().getString("product_subtitle"));
                                model.setProductImage(task.getResult().getString("product_image_1"));
                                model.setProductDescription(task.getResult().getString("product_title"));
                                model.setProductPrice(task.getResult().getString("product_price"));


                                if (horizontalProductScrollModels.indexOf(model) == horizontalProductScrollModels.size() -1){

                                    setGridData(title, horizontalProductScrollModels);
                                    if (!title.equals("")) {
                                        gridLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ViewAllActivity.horizontalProductScrollModels = horizontalProductScrollModels;
                                                Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                                                viewAllIntent.putExtra("layout_code", 1);
                                                viewAllIntent.putExtra("title", title);
                                                itemView.getContext().startActivity(viewAllIntent);

                                            }
                                        });
                                    }
                                }
                            }else {
                                //nth
                            }
                        }
                    });
                }
            }
            setGridData(title, horizontalProductScrollModels);
        }

        private void setGridData(String title, final List<HorizontalProductScrollModel> horizontalProductScrollModels){
            for (int x = 0; x < 4; x++){
                ImageView productImage = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_image);
                TextView productTitle = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_title);
                TextView productDescription = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_desc);
                TextView productPrice = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_price);

                Glide.with(itemView.getContext()).load(horizontalProductScrollModels.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.home)).into(productImage);
                productTitle.setText(horizontalProductScrollModels.get(x).getProductTitle());
                productDescription.setText(horizontalProductScrollModels.get(x).getProductDescription());
                productPrice.setText( "$"+horizontalProductScrollModels.get(x).getProductPrice() +"/-" );
                gridLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));

                if (!title.equals("")) {
                    final int finalX = x;
                    gridLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            intent.putExtra("PRODUCT_ID", horizontalProductScrollModels.get(finalX).getProductID());
                            itemView.getContext().startActivity(intent);
                        }
                    });
                }
            }

        }
    }
}
