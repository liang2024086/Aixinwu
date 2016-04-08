package com.aixinwu.axw.tools;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liangyuding on 2016/3/31.
 */
public class Tool {
    static String genJson(String name, String psw){
        JSONObject matadata = new JSONObject();
        matadata.put("TimeStamp",123124233);
        matadata.put("Device", "android");
        JSONObject userinfo = new JSONObject();
        userinfo.put("username", name);
        userinfo.put("password", psw);

        JSONObject data = new JSONObject();
        data.put("mataData",matadata);
        data.put("userinfo", userinfo);
        return data.toJSONString();
    }

    static java.lang.String getToken(String surl, String name, String psw) throws IOException {
        String jsonstr = genJson(name, psw);
        URL url  = new URL(surl + "/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        conn.getOutputStream().write(jsonstr.getBytes());

        String ostr = IOUtils.toString(conn.getInputStream());
        System.out.println(ostr);

        org.json.JSONObject outjson = null;
        String result = null;
        try{
            outjson = new org.json.JSONObject(ostr);
            result = outjson.getString("token");
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}
