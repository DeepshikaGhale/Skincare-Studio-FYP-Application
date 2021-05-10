package com.example.fyp_application;

public class CategoryModel {
    private String CategoryIconLink;
    private String categoryname;

    public CategoryModel(String categoryIconLink, String categoryname) {
        CategoryIconLink = categoryIconLink;
        this.categoryname = categoryname;
    }

    public String getCategoryIconLink() {
        return CategoryIconLink;
    }

    public void setCategoryIconLink(String categoryIconLink) {
        CategoryIconLink = categoryIconLink;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }
}
