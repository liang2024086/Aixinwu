package com.aixinwu.axw.model;

import java.io.Serializable;

/**
 * Created by zhangyan on 2015/10/29.
 */
public class ShoppingCartEntity implements Serializable {

    private String id ;
    private String name;
    private String category;
    private int price;
    private int number ;
    private String imgUrl;

    public ShoppingCartEntity(String id, String name, String category, int price, int number,
                              String imgUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.number = number;
        this.imgUrl = imgUrl;
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

    public int getPrice() {
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
}
