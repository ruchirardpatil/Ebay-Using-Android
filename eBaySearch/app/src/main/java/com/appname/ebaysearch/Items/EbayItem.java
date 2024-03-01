package com.appname.ebaysearch.Items;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

public class EbayItem implements Serializable {

    private List<String> Id;
    private List<String> image;
    private String title;
    private String price;
    private String itemUrl;

    private String itemShipping;
    private String zipCode;
    private String condition;

    public String getItemShipping() {
        return itemShipping;
    }

    public void setItemShipping(String itemShipping) {
        this.itemShipping = itemShipping;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }



    public List<String> getId() {
        return Id;
    }

    public void setId(List<String> id) {
        Id = id;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

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


    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }
}
