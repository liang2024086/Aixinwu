package com.aixinwu.axw.model;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import java.io.Serializable;
/**
 * Created by dell1 on 2016/4/24.
 */
public class Product implements Serializable {
    private String product_name;
    //private int amount;
    private int price;
    private String description;
    private String shortdescription;
    private String image_url;
    private String descriptionUrl;
    private int id;


    public Product (int Id, String name, int price, String image_url, String description, String shortdescription, String descriptionUrl) {
        this.product_name = name;
        this.price = price;
        this.image_url = image_url;
        this.id = Id;
        this.description = description;
        this.shortdescription = shortdescription;
        this.descriptionUrl = descriptionUrl;
    }

    public String getProduct_name () {
        return product_name;
    }

    public int getPrice () {
        return price;
    }

    public String getImage_url () {
        return image_url;
    }

    public int getId () {return id;}

    public String getDescription () {return description;}

    public String getShortdescription () {return shortdescription;}

    public String getDescriptionUrl(){ return GlobalParameterApplication.getSurl() + "/" +  this.descriptionUrl;}

}
