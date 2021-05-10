package com.example.fyp_application;

public class ProductSpecificationModel {

    private String feature_name;
    private String feature_value;

    public ProductSpecificationModel(String feature_name, String feature_value) {
        this.feature_name = feature_name;
        this.feature_value = feature_value;
    }

    public String getFeature_name() {
        return feature_name;
    }

    public void setFeature_name(String feature_name) {
        this.feature_name = feature_name;
    }

    public String getFeature_value() {
        return feature_value;
    }

    public void setFeature_value(String feature_value) {
        this.feature_value = feature_value;
    }
}
