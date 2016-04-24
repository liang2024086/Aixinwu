package com.aixinwu.axw.tools;

/**
 * Created by dell1 on 2016/4/23.
 */

import android.app.Application;

public class GlobalParameterApplication extends Application{
    private int login_status = 0;
    private String user_name;
    private String token;

    public void setToken (String token) {
        this.token = token;
    }

    public String getToken () {
        return token;
    }
    public int getLogin_status () {
        return login_status;
    }

    public void setLogin_status (int s) {
        this.login_status = s;
    }


    public void setUser_name (String s) {
        this.user_name = s;
    }

    public String getUser_name () {
        return user_name;

    }


}
