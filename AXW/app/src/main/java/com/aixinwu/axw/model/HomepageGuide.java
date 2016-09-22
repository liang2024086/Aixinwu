package com.aixinwu.axw.model;

/**
 * Created by liangyuding on 2016/5/1.
 */
public class HomepageGuide {
    private String imgURL;
    private String name;
    private int id;

    public HomepageGuide(int id ,String imgURL, String name){
        this.id = id;
        this.imgURL = imgURL;
        this.name = name;
    }

    public int getId() { return id; }

    public String getImgURL(){
        return imgURL;
    }

    public String getName(){
        return name;
    }
}
