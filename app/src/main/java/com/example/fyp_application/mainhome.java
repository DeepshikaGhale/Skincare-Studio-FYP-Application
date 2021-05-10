package com.example.fyp_application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.fyp_application.DBqueries.categoryModelList;
import static com.example.fyp_application.DBqueries.lists;
import static com.example.fyp_application.DBqueries.loadCategories;
import static com.example.fyp_application.DBqueries.loadCategoriesNames;
import static com.example.fyp_application.DBqueries.loadFragmentData;


/**
 * A simple {@link Fragment} subclass.
 */
public class mainhome extends Fragment {

    public mainhome() {
        // Required empty public constructor
    }

    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView homePageRecyclerView;
    private RecyclerView categoryRecyclerView;
    private CategoryAdaptor categoryAdaptor;

    private HomePageAdaptor adaptor;
    private ImageView noInternetConnection;
    private Button retry_btn;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mainhome, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        retry_btn = view.findViewById(R.id.retry_btn);

        //check internet connection
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true){
            noInternetConnection.setVisibility(View.GONE);
            navigationbar.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            retry_btn.setVisibility(View.GONE);
            categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            categoryRecyclerView.setLayoutManager(layoutManager);
            categoryAdaptor = new CategoryAdaptor(categoryModelList);
            categoryRecyclerView.setAdapter(categoryAdaptor);

            if (categoryModelList.size() == 0){
                loadCategories(categoryAdaptor, getContext());
            }
            else {
                categoryAdaptor.notifyDataSetChanged();
            }

            homePageRecyclerView = view.findViewById(R.id.recyclerView);
            LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
            testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            homePageRecyclerView.setLayoutManager(testingLayoutManager);

            if (lists.size() == 0){
                loadCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                adaptor = new HomePageAdaptor(lists.get(0));
                loadFragmentData(adaptor, getContext(), 0, "home");
            }
            else {
                adaptor = new HomePageAdaptor(lists.get(0));
                adaptor.notifyDataSetChanged(); //for refreshing
            }
            homePageRecyclerView.setAdapter(adaptor);

            //when network is available
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);
        }else {
            //when network is not available
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            navigationbar.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//to make sure user has internet connection
            Glide.with(this).load(R.drawable.nointernetconnection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retry_btn.setVisibility(View.VISIBLE);
        }
        // refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });
        // refresh layout

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reloadPage();
            }
        });
        return view;
    }

    private void reloadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();

        //clearing the cateogry for refreshing it
//        categoryModelList.clear();
//        lists.clear();
//        loadCategoriesNames.clear();

        DBqueries.clearData();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            navigationbar.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retry_btn.setVisibility(View.GONE);
            //when network is available
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            loadCategories(categoryAdaptor, getContext());

            loadCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(adaptor, getContext(), 0, "home");

        }else {
            //when network is not available
            navigationbar.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            //to make sure user has internet connection
            Glide.with(getContext()).load(R.drawable.nointernetconnection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retry_btn.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
