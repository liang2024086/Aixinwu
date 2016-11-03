package com.aixinwu.axw.model;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import java.io.Serializable;
/**
 * Created by dell1 on 2016/4/24.
 */
public class Product implements Serializable {
    private String product_name;
    //private int amount;
    private double price;
    private double past_price;
    private String description;
    private String shortdescription;
    private String image_url;
    private String descriptionUrl;
    private int id;
    private int stock;
    private int limit;
    private int already_buy;


    public Product (int Id, String name, double price, int stock, String image_url, String description, String shortdescription, String descriptionUrl) {
        this.product_name = name;
        this.price = price;
        this.stock = stock;
        this.image_url = image_url;
        this.id = Id;
        this.description = description;
        this.shortdescription = shortdescription;
        this.descriptionUrl = descriptionUrl;
    }

    public Product (int Id, String name, double price, int stock, String image_url, String description, String shortdescription, String descriptionUrl, int limit, int already_buy) {
        this.product_name = name;
        this.price = price;
        this.stock = stock;
        this.image_url = image_url;
        this.id = Id;
        this.description = description;
        this.shortdescription = shortdescription;
        this.descriptionUrl = descriptionUrl;
        this.limit = limit;
        this.already_buy = already_buy;
    }

    public String getProduct_name () {
        return product_name;
    }

    public double getPrice () {
        return price;
    }

    public double getPast_price(){
        return past_price;
    }

    public String getImage_url () {
        return image_url;
    }

    public int getId () {return id;}

    public String getDescription () {return description;}

    public String getShortdescription () {return shortdescription;}

    public String getDescriptionUrl(){ return GlobalParameterApplication.getSurl() + "/" +  this.descriptionUrl;}

    public int getStock() {return stock;}

    public int getLimit(){
        return this.limit;
    }

    public int getAlready_buy(){
        return this.already_buy;
    }
}
