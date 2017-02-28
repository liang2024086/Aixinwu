package com.aixinwu.axw.model;

import java.io.Serializable;

/**
 * Created by hello on 2016/10/29.
 */
public class VolunteerActivity implements Serializable {

    private int id;
    private String img_url;
    private String name;
    private double payback;
    private String time;
    private int neededPeople;
    private int signedPeople;
    private double duration;
    private String site;
    private int joined;
    private String about;
    private String content;

    public VolunteerActivity(int id,String img_url, String name, double payback, String time, int neededPeople, int signedPeople,double duration,String site,int joined, String about, String content){
        this.id = id;
        this.img_url = img_url;
        this.name = name;
        this.payback = payback;
        this.time = time;
        this.signedPeople = signedPeople;
        this.neededPeople = neededPeople;
        this.duration = duration;
        this.site = site;
        this.joined = joined;
        this.about = about;
        this.content = content;
    }

    public int getId(){
        return this.id;
    }

    public String getImg_url(){
        return this.img_url;
    }

    public String getName(){
        return this.name;
    }

    public double getPayback(){
        return this.payback;
    }

    public String getTime(){
        return this.time;
    }

    public int getNeededPeople(){
        return this.neededPeople;
    }

    public int getSignedPeople(){
        return this.signedPeople;
    }

    public double getDuration(){
        return this.duration;
    }

    public String getSite(){
        return this.site;
    }

    public int getJoined(){
        return this.joined;
    }

    public String getAbout() { return this.about; }

    public String getContent() { return this.content; }
}
