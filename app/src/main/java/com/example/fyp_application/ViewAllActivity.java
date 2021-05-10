package com.example.fyp_application;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridView gridview;
    public static List<WishlistModel> wishlistModelList;
    public static List<HorizontalProductScrollModel> horizontalProductScrollModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        gridview = findViewById(R.id.grid_view);

        int layout_code = getIntent().getIntExtra("layout_code", -1);

        if (layout_code == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            WishlistAdaptor wishlistAdaptor = new WishlistAdaptor(wishlistModelList, false);
            recyclerView.setAdapter(wishlistAdaptor);
            wishlistAdaptor.notifyDataSetChanged();
        }

        else if (layout_code == 1) {
            gridview.setVisibility(View.VISIBLE);
            GridProductLayoutAdaptor gridProductLayoutAdaptor = new GridProductLayoutAdaptor(horizontalProductScrollModels);
            gridview.setAdapter(gridProductLayoutAdaptor);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
