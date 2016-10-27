package com.aixinwu.axw.tools;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.util.Iterator;

/**
 * Created by Cross_Life on 2016/4/18.
 */
public class Bean {
    private String picId;
    private String type;
    private String doc;
    private int itemId;

    //added by Yuding
    private String whetherOnSail;
    private int OnOrNot;
    private int price = 0;

    public Bean(int itemId, String picId, String type, String doc) {
        this.picId = picId;
        this.itemId = itemId;
        this.type = type;
        this.doc = doc;
    }

    public Bean(int itemId, String picId, String type, String doc,String whetherOnSail, int onOrNot) {
        this.picId = picId;
        this.itemId = itemId;
        this.type = type;
        this.doc = doc;
        this.whetherOnSail = whetherOnSail;
        this.OnOrNot = onOrNot;
    }

    public Bean(int itemId, String picId, String type, String doc,int price) {
        this.picId = picId;
        this.itemId = itemId;
        this.type = type;
        this.doc = doc;
        this.price = price;
    }

    public int getPrice(){return this.price;}
    public void getPrice(int price) {this.price = price;}
    public void setItemId(int itemID){
        this.itemId = itemID;
    }
    public int getItemId(){
        return itemId;
    }
    public String getPicId() {
        return picId;
    }

    public void setIconId(String picId) {
        this.picId = picId;
    }

    public String getType() {
        return type;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getDoc() {
        return doc;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWhetherOnSail(String whetherOnSail){ this.whetherOnSail = whetherOnSail;}

    public String getWhetherOnSail() { return this.whetherOnSail;}

    public int getOnOrNot(){ return this.OnOrNot; }
}
