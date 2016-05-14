package com.aixinwu.axw.fragment;

//import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.LoginActivity;
import com.aixinwu.axw.activity.PersonalDetailActivity;
import com.aixinwu.axw.activity.SignupActivity;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UserInfo extends Fragment {

    private int ss=0;
    private RelativeLayout ly_personalinfo;
    private LinearLayout ly_logininfo;
    private Button btn_logoff;
    private String username;
    private String coins;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.personalinfo, null);
        return view;
    }



    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ly_logininfo = (LinearLayout) getActivity().findViewById(R.id.login);
        ly_personalinfo = (RelativeLayout) getActivity().findViewById(R.id.personal);
        btn_logoff = (Button) getActivity().findViewById(R.id.personal_exit);

    }




    @Override
    public void onStart () {
        //GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();

        if (GlobalParameterApplication.getLogin_status() == 1) {
            Toast.makeText(getActivity(), "ssssssss", Toast.LENGTH_LONG);
            ly_logininfo.setVisibility(View.GONE);
            ly_personalinfo.setVisibility(View.VISIBLE);
            btn_logoff.setVisibility(View.VISIBLE);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getPersonalInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what=123123;
                    nHandler.sendMessage(msg);
                }
            }).start();
        }
        else if (GlobalParameterApplication.getLogin_status() == 0) {
            ly_logininfo.setVisibility(View.VISIBLE);
            ly_personalinfo.setVisibility(View.GONE);
            btn_logoff.setVisibility(View.GONE);
        }

        Button login_btn = (Button) getActivity().findViewById(R.id.personal_login_button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });
/*
        TextView signup_btn = (TextView) getActivity().findViewById(R.id.signup);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                getActivity().startActivity(intent);
            }
        });
*/
        Button logoff_btn = (Button) getActivity().findViewById(R.id.personal_exit);
        logoff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();
                GlobalParameterApplication.setToken("");
                GlobalParameterApplication.setLogin_status(0);


                ly_logininfo.setVisibility(View.VISIBLE);
                ly_personalinfo.setVisibility(View.GONE);
                btn_logoff.setVisibility(View.GONE);

            }

        });

        ly_personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalDetailActivity.class);
                getActivity().startActivity(intent);
            }

        });




        super.onStart();
    }
    /*
public Thread mThread = new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            getPersonalInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what=123123;
        nHandler.sendMessage(msg);
    }
});
*/
    public Handler nHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123123) {
                TextView coinsText = (TextView) getActivity().findViewById(R.id.coins);
                TextView usernameText = (TextView) getActivity().findViewById(R.id.username);
                coinsText.setText("爱心币： " + coins);
                usernameText.setText(username);

            }
        }
    };
//=======================Connect to Server=================
    public String genJson(String token) {
        JSONObject matadata = new JSONObject();
        matadata.put("TimeStamp", 123124233);
        matadata.put("Device", "android");


        JSONObject data = new JSONObject();
        data.put("mataData", matadata);
        data.put("token", token);
        return data.toJSONString();
    }

    private void getPersonalInfo () throws IOException {
        String token = GlobalParameterApplication.getToken();
        String jsonstr = genJson(token);
        String surl = GlobalParameterApplication.getSurl();
        URL url = new URL(surl + "/usr_get");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        conn.getOutputStream().write(jsonstr.getBytes());

        String ostr = IOUtils.toString(conn.getInputStream());
        //System.out.println(ostr);

        org.json.JSONObject outjson;
        org.json.JSONObject userinfojson;
        String userinfo;
        try {
            outjson = new org.json.JSONObject(ostr);
            userinfo = outjson.getString("userinfo");


            userinfojson = new org.json.JSONObject(userinfo);
            coins = userinfojson.getString("coins");
            username = userinfojson.getString("username");



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//======================================================================
}
