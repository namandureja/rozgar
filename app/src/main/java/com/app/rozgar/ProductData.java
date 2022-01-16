package com.app.rozgar;

public class ProductData {

    String prodName;
    String qty;
    String dueDate;
    int ImageResId;

    public ProductData(String prodName, String qty, String dueDate, int imageResId) {
        this.prodName = prodName;
        this.qty = qty;
        this.dueDate = dueDate;
        ImageResId = imageResId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getImageResId() {
        return ImageResId;
    }

    public void setImageResId(int imageResId) {
        ImageResId = imageResId;
    }
}
