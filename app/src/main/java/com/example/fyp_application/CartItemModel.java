package com.example.fyp_application;

import java.util.ArrayList;
import java.util.List;

public class CartItemModel {
    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //cart item
    private String productID;
    private String productImage;
    private String productTitle;
    private String productPrice;
    private Long productQuatity;
    private Long maxQuantity;
    private Long stockQuantity;
    private boolean inStock;
    private List<String> qtyIDs;
    private boolean COD;

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public CartItemModel(boolean COD,int type, String productID, String productImage, String productTitle, String productPrice, Long productQuatity, boolean inStock, Long maxQuantity, Long stockQuantity) {
        this.type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productQuatity = productQuatity;
        this.maxQuantity = maxQuantity;
        this.stockQuantity = stockQuantity;
        this.inStock = inStock;
        qtyIDs = new ArrayList<>();
        this.COD = COD;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public List<String> getQtyIDs(){
        return qtyIDs;
    }

    public void setQtyIDs(List<String> qtyIDs){
        this.qtyIDs = qtyIDs;
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductID() {
        return productID;
    }

    public void setproductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuatity() {
        return productQuatity;
    }

    public void setProductQuatity(Long productQuatity) {
        this.productQuatity = productQuatity;
    }

    //cart item
    private int totalItems, totalItemsPrice, totalAmount;
    private String deliveryPrice;

    //cart total
    public CartItemModel(int type){
        this.type = type;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalItemsPrice() {
        return totalItemsPrice;
    }

    public void setTotalItemsPrice(int totalItemsPrice) {
        this.totalItemsPrice = totalItemsPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    //cart total
}