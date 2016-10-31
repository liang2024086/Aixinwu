package com.aixinwu.axw.model;

/**
 * Created by hello on 2016/10/29.
 */
public class Consignee {
    private int id;
    private int custom_id;
    private String email;
    private String name;
    private String stuId;
    private String phoneNumber;
    private int whtherDefault;

    public Consignee(String name, String stuId, String phoneNumber, int id, int custom_id, String email, int whtherDefault){
        this.name = name;
        this.stuId = stuId;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.custom_id = custom_id;
        this.email = email;
        this.whtherDefault = whtherDefault;
    }

    public String getName(){
        return this.name;
    }

    public String getStuId(){
        return this.stuId;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public int getId(){
        return this.id;
    }

    public int getCustom_id(){
        return this.custom_id;
    }

    public String getEmail(){
        return this.email;
    }
}
