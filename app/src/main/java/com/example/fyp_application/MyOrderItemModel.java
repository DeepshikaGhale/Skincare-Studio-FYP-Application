package com.example.fyp_application;

import java.util.Date;

public class MyOrderItemModel {

    private String productID;
    private String productTitle;
    private String productImage;
    private String deliveryStatus;

    private String address;
    private String fullName;
    private Date orderedDate;
    private Date deliveredDate;
    private Date cancelledDate;
    private Date packedDate;
    private Date shippedDate;
    private String order_id;
    private String paymentMethod;
    private String pincode;
    private String productPrice;
    private Long productQuantity;
    private String userId;
    private String deliveryPrice;
    private boolean cancellationEquested;
    
    private int rating = 0;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public MyOrderItemModel(String productID, String deliveryStatus, String address, String fullName, Date orderedDate,
                            Date packedDate, Date shippedDate, Date deliveredDate, Date cancelledDate, String order_id,
                            String paymentMethod, String pincode, String productPrice, Long productQuantity, String userId,
                            String productImage, String productTitle, String deliveryPrice, boolean cancellationEquested) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productID = productID;
        this.deliveryStatus = deliveryStatus;
        this.address = address;
        this.fullName = fullName;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.deliveredDate = deliveredDate;
        this.cancelledDate = cancelledDate;
        this.order_id = order_id;
        this.paymentMethod = paymentMethod;
        this.pincode = pincode;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.userId = userId;
        this.deliveryPrice = deliveryPrice;
        this.cancellationEquested = cancellationEquested;
    }

    public boolean isCancellationEquested() {
        return cancellationEquested;
    }

    public void setCancellationEquested(boolean cancellationEquested) {
        this.cancellationEquested = cancellationEquested;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
