package com.aixinwu.axw.tools;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;



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
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
    public static Bitmap DownloadFile(String surl, String filename,int samplerate) throws IOException {
        URL url = new URL(surl + "/img_get");
        HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
        conn1.setRequestMethod("POST");
        conn1.setDoOutput(true);
        conn1.setRequestProperty("Content-Type", "application/json");
        JSONObject data = new JSONObject();
        data.put("imageID", filename);
        conn1.getOutputStream().write(data.toJSONString().getBytes());
        InputStream inStream = conn1.getInputStream();
        byte[] data1 = new byte[0];
        try {
            data1 = readInputStream(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = samplerate;
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        Bitmap bmp = BitmapFactory.decodeByteArray(data1,0,data1.length,options);
        return bmp;
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
        int i;
        URL url = new URL(surl + "/img_upload");
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




