package com.appname.ebaysearch.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductItem implements Serializable {

    private String title;
    private String price;
    private String shipping;
    private String brand;
    private ArrayList<String> images;
    private ArrayList<String> itemSpecifics;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getItemSpecifics() {
        return itemSpecifics;
    }

    public void setItemSpecifics(ArrayList<String> itemSpecifics) {
        this.itemSpecifics = itemSpecifics;
    }
}
