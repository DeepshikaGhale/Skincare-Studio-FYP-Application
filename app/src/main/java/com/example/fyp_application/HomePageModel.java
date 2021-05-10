package com.example.fyp_application;

import java.util.List;

public class HomePageModel {

    //static, so that value can be changed or accessed from anywhere : final, cause values won't be changed
    public static final int HORIZONTAL_PRODUCT_VIEW = 0;
    public static final int GRID_PRODUCT_VIEW = 1;

    private int type;
    private String backgroundColor;

    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    //Horizontal Product Layout

    //for viewall product
    private List<WishlistModel> viewAllProductList;

    public HomePageModel(int type, String title, String backgroundColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList, List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    //Horizontal Product Layout

    //Grid Product Layout
    public HomePageModel(int type, String title, String backgroundColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }
    //Grid Product Layout
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    public int getType() {
        return type;
    }
    //Horizontal Product Layout


}
