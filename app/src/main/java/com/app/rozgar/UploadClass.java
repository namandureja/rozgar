package com.app.rozgar;

import com.google.firebase.database.Exclude;

public class UploadClass {
    public String prodName;
    public String imageURL;
    public String prodQty;
    public String prodPrice;
    public String category;
    public String mKey;

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getProdQty() {
        return prodQty;
    }

    public void setProdQty(String prodQty) {
        this.prodQty = prodQty;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }

    public UploadClass(String prodName, String imageURL, String prodQty, String prodPrice, String category) {
        this.prodName = prodName;
        this.imageURL = imageURL;
        this.prodQty = prodQty;
        this.prodPrice = prodPrice;
        this.category = category;

    }

    public UploadClass(){

    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
