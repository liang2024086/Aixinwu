package com.aixinwu.axw.model;

/**
 * Created by dell1 on 2016/4/24.
 */
public class Product {
    private String product_name;
    private int amount;
    private int price;
    private String description;
    private int image_id;
    private String id;


    public Product (String name, int price, int image_id) {
        this.product_name = name;
        this.price = price;
        this.image_id = image_id;
        this.id = "0001";
        this.description = "des";
    }

    public String getProduct_name () {
        return product_name;
    }

    public int getPrice () {
        return price;
    }

    public int getImage_id () {
        return image_id;
    }

    public String getId () {return id;}
    public String getDescription () {return description;}

}
