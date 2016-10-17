package com.aixinwu.axw.Adapter;

/**
 * Created by Cross_Life on 2016/10/16.
 */

import java.io.Serializable;

import android.os.Parcelable;


/**
 * Created by Cross_Life on 2016/10/16.
 */
public class NotifyMessage implements Serializable {
    private  String Who;
    private  String Message;
    private  String Time;
    public  void setWho(String _Who){
        Who = _Who;
    }
    public void setMessage(String _Message){
        Message = _Message;
    }
    public void setTime(String _Time){
        Time = _Time;
    }
    public String getMessage(){
        return Message;
    }
    public String getWho(){
        return Who;
    }
    public String getTime(){
        return Time;
    }
}

