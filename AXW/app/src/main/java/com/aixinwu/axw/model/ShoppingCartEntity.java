package com.aixinwu.axw.model;

import java.io.Serializable;

/**
 * Created by zhangyan on 2015/10/29.
 */
public class ShoppingCartEntity implements Serializable {

    private String id ;
    private String name;
    private String category;
    private double price;
    private int number ;
    private String imgUrl;
    private int stock;

    public ShoppingCartEntity(String id, String name, String category, double price, int number,
                              String imgUrl, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.number = number;
        this.imgUrl = imgUrl;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getStock(){return stock;}
}
