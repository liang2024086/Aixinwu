package com.aixinwu.axw.tools;

import android.provider.MediaStore;


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
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liangyuding on 2016/3/31.
 */
public class Tool {
    public String genJson(String name, String psw) {
        JSONObject matadata = new JSONObject();
        matadata.put("TimeStamp", 123124233);
        matadata.put("Device", "android");
        JSONObject userinfo = new JSONObject();
        userinfo.put("username", name);
        userinfo.put("password", psw);

        JSONObject data = new JSONObject();
        data.put("mataData", matadata);
        data.put("userinfo", userinfo);
        return data.toJSONString();
    }

    public java.lang.String getToken(String surl, String name, String psw) throws IOException {
        String jsonstr = genJson(name, psw);
        URL url = new URL(surl + "/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        conn.getOutputStream().write(jsonstr.getBytes());

        String ostr = IOUtils.toString(conn.getInputStream());
        System.out.println(ostr);

        org.json.JSONObject outjson = null;
        String result = null;
        try {
            outjson = new org.json.JSONObject(ostr);
            result = outjson.getString("token");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String sendFile(String surl, String filename) throws IOException {
        URL url = new URL(surl + "/upload");
        String charset = "UTF-8";
        String CRLF = "\r\n";
        String boundary = Long.toHexString(System.currentTimeMillis());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestMethod("POST");

        OutputStream outputStream = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

        // sent a binary file
        File binaryFile = new File(filename);
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
        writer.append(CRLF).flush();
        InputStream ini = new FileInputStream(binaryFile);
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while ((n = ini.read(buf)) > 0) {
            outputStream.write(buf, 0, n);
            nread += n;
        }
        // Files.copy(binaryFile.toPath(), outputStream);
        outputStream.flush();
        writer.append(CRLF).flush();
        // End of multipart/form-data.
        writer.append("--" + boundary + "--").append(CRLF).flush();
        int respcode = conn.getResponseCode();
        System.out.println("RespCode:" + respcode);
        String result = null;
        if (respcode == 200) {
            String ostr = IOUtils.toString(conn.getInputStream());
            System.out.println(ostr);
            try {
                org.json.JSONObject report = new org.json.JSONObject(ostr);

                result = report.getString("imageID");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return result;
    }
}




