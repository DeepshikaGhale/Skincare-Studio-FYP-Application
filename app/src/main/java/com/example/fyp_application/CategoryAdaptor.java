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

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.ViewHolder> {

    private List<CategoryModel> categoryModelLists;

    public CategoryAdaptor(List<CategoryModel> categoryModelList) {
        this.categoryModelLists = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdaptor.ViewHolder holder, int position) {
        String icon = categoryModelLists.get(position).getCategoryIconLink();
        String name = categoryModelLists.get(position).getCategoryname();
        holder.setCategory(name);
        holder.setCategoryIcon(icon);
    }

    @Override
    public int getItemCount() {
        return categoryModelLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryIcon;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);
        }

        private void setCategoryIcon(String iconUrl) {
          // TODO: set categoryicon here
            if (!iconUrl.equals("null")) {
                Glide.with(itemView.getContext()).load(iconUrl).apply(new RequestOptions().placeholder(R.mipmap.home)).into(categoryIcon);
            }
        }

        private void setCategory(final String name){
         categoryName.setText(name);

         itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent categoryIntent = new Intent(itemView.getContext(), CategoryActivity.class);
                 categoryIntent.putExtra("CategoryName", name);
                 itemView.getContext().startActivity(categoryIntent);

             }
         });
        }
    }
}

