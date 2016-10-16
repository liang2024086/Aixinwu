package com.aixinwu.axw.tools;

/**
 * Created by liangyuding on 2016/10/16.
 */
public class ReceiverInof {

    private String name;
    private String stuId;
    private String phone;

    public ReceiverInof(String name, String stuId, String phone){
        this.name = name;
        this.stuId = stuId;
        this.phone = phone;
    }

    public String getName(){ return this.name; }

    public String getStuId(){ return this.stuId; }

    public String getPhone() { return this.phone; }
}
