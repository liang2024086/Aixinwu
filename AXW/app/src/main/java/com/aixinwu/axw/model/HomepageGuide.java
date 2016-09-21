package com.aixinwu.axw.model;

/**
 * Created by liangyuding on 2016/5/1.
 */
public class HomepageGuide {
    private String imgURL;
    private String name;
    //private int imgId;

    public HomepageGuide(String imgURL, String name){
        //this.imgId = imgId;
        this.imgURL = imgURL;
        this.name = name;
    }

    //public int getImgId() { return imgId; }

    public String getImgURL(){
        return imgURL;
    }

    public String getName(){
        return name;
    }
}
