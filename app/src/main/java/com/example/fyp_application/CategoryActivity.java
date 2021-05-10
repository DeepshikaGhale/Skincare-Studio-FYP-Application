package com.example.fyp_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.fyp_application.DBqueries.lists;
import static com.example.fyp_application.DBqueries.loadCategoriesNames;
import static com.example.fyp_application.DBqueries.loadFragmentData;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private HomePageAdaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryRecyclerView = findViewById(R.id.category_rv);

//Recycler View

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);

        int listPosition = 0;
        for (int x = 0; x < loadCategoriesNames.size(); x++){
            if (loadCategoriesNames.get(x).equals(title.toUpperCase())){
                listPosition = x;
            }
        }
        if (listPosition == 0){
            loadCategoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            adaptor = new HomePageAdaptor(lists.get(loadCategoriesNames.size() - 1));
            loadFragmentData(adaptor, this, loadCategoriesNames.size() - 1, title.toUpperCase());
        }else {
            adaptor = new HomePageAdaptor(lists.get(listPosition));
        }
        categoryRecyclerView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        if (id == R.id.search_icon) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
