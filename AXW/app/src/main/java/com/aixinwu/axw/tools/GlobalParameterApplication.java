package com.aixinwu.axw.tools;

/**
 * Created by dell1 on 2016/4/23.
 */

import android.app.Application;

public class GlobalParameterApplication extends Application{
    private static int login_status = 0;
    private static String user_name;
    private static String token;
    private static String surl = "http://202.120.47.213:12345/api";


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


}
