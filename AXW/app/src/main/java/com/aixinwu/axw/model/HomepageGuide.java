package com.aixinwu.axw.model;

/**
 * Created by liangyuding on 2016/5/1.
 */
public class HomepageGuide {
    private int imgId;
    private String name;

    public HomepageGuide(int imgId, String name){
        this.imgId = imgId;
        this.name = name;
    }

    public int getImgId(){
        return imgId;
    }

    public String getName(){
        return name;
    }
}
