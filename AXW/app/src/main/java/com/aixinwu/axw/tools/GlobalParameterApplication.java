package com.aixinwu.axw.tools;

/**
 * Created by dell1 on 2016/4/23.
 */

import android.app.Application;

import java.util.HashMap;

public class GlobalParameterApplication extends Application{
    private static int login_status = 0;
    private static String user_name;
    private static String token;
    private static String surl = "http://202.120.47.213:12345/api";
    private static HashMap<String,Integer> newOldStringToInt = new HashMap<String, Integer>();
    private static HashMap<Integer,String> newOldIntToString = new HashMap<Integer, String>();


    public GlobalParameterApplication(){
        newOldIntToString.put(Integer.valueOf(1),"全新");
        newOldIntToString.put(Integer.valueOf(2),"九成新");
        newOldIntToString.put(Integer.valueOf(3),"七成新");
        newOldIntToString.put(Integer.valueOf(4),"六成新及以下");

        newOldStringToInt.put("全新",Integer.valueOf(1));
        newOldStringToInt.put("九成新",Integer.valueOf(2));
        newOldStringToInt.put("七成新",Integer.valueOf(3));
        newOldStringToInt.put("六成新及以下",Integer.valueOf(4));
    }

    public static String getSurl () {
        return surl;
    }



    public static void setToken (String _token) {
        token = _token;
    }

    public static String getToken () {
        return token;
    }
    public static int getLogin_status () {
        return login_status;
    }

    public static void setLogin_status (int s) {
        login_status = s;
    }


    public static void setUser_name (String s) {
        user_name = s;
    }

    public static String getUser_name () {
        return user_name;
    }

    public static String getNewOldString(int i){
        return newOldIntToString.get(Integer.valueOf(i));
    }

    public static int getNewOldInt(String name){
        return newOldStringToInt.get(name).intValue();
    }

}
