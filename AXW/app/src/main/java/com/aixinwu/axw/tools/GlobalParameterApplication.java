package com.aixinwu.axw.tools;

/**
 * Created by dell1 on 2016/4/23.
 */

import android.app.Application;
import android.content.SharedPreferences;

import java.util.HashMap;

public class GlobalParameterApplication extends Application{
    private static int login_status = 0;
    private static String user_name;
    private static String token;
    private static boolean pause=true;
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

    private static boolean AllowChatThread = true;
    private static int UserID;
    private static int Chat_Num;
    private static int prename=-1;
    private static boolean end = true;
    private SharedPreferences sharedPreferences;
    public static void setPrename(int _prename){
        prename=_prename;
    }
    private static boolean chat_othermsg=true;
    public static void setChat_othermsg(boolean _chat_othermsg){
        chat_othermsg=_chat_othermsg;
    }
    public static boolean getChat_othermsg(){
        return chat_othermsg;
    }
    public static int getPrename(){
        return prename;
    }

    public static String getSurl () {
        return surl;
    }
    public static void setEnd(boolean _end){
        end = _end;
    }
    public static boolean getEnd(){
        return end;
    }
    public static void setAllowChatThread(boolean aa){
        AllowChatThread = aa;
    }
    public static int getChat_Num(){
        return Chat_Num;
    }
    public static void setChat_Num(int _Chat_Num){
        Chat_Num = _Chat_Num;
    }
    public static void setPause(boolean _pause){
        pause = _pause;

    }
    public static boolean getPause(){
        return pause;
    }
    public static boolean getAllowChatThread(){
        return AllowChatThread;
    }
    public static void setUserID(int _UserID){
        UserID = _UserID;
    }
    public static int getUserID(){
        return UserID;
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
