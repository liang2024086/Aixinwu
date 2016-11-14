package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.database.Sqlite;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by liangyuding on 2016/4/25.
 */
public class WelcomeActivity extends Activity {

    private final long SPLASH_LENGTH = 2000;
    Handler handler = new Handler();
    private TextView time;
    private MyCountDownTimer mc;

    private boolean judge = true;

    private Sqlite userDbHelper = new Sqlite(this);

    private myRunnable my = new myRunnable();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);


        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            GlobalParameterApplication.versionName = info.versionName;
            //Toast.makeText(WelcomeActivity.this,version,Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String , String> versionInfo = getVersionNumber();
                String versionNumber = "";
                if (versionInfo.get("versionCode") != null)
                    versionNumber = versionInfo.get("versionCode");
                int a = versionNumber.compareTo(GlobalParameterApplication.versionName);
                if (versionNumber.compareTo(GlobalParameterApplication.versionName) > 0){
                    GlobalParameterApplication.wetherHaveNewVersion = true;
                }
                else GlobalParameterApplication.wetherHaveNewVersion = false;
            }
        }).start();

       /* time = (TextView)findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                judge = false;
                //Toast.makeText(getApplicationContext(), "跳过", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

       mc = new MyCountDownTimer(5000, 1000);
       mc.start();*/

        final SQLiteDatabase db = userDbHelper.getReadableDatabase();
        final Cursor cursor = db.rawQuery("select phoneNumber,pwd from AXWuser where userId = 1",null);
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(0); //获取第一列的值,第一列的索引从0开始
            String pwd = cursor.getString(1);//获取第二列的值
            LoginThread loginThread = new LoginThread(phoneNumber,pwd);
            loginThread.start();
            try{
                loginThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();

        handler.postDelayed(my, SPLASH_LENGTH);
    /*    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_LENGTH);//2秒后跳转至应用主界面MainActivity

*/

    }

    class myRunnable implements Runnable{

        public void run() {
            if (judge) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture
         *            表示以毫秒为单位 倒计时的总数
         *
         *            例如 millisInFuture=1000 表示1秒
         *
         * @param countDownInterval
         *            表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *            例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            time.setText("跳过\n0秒");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.i("MainActivity", millisUntilFinished + "");
            time.setText("跳过\n"+millisUntilFinished / 1000 + "秒");
        }
    }


    class LoginThread extends Thread{

        private String email,password;
        public LoginThread(String email,String password){
            this.email = email;
            this.password = password;
        }

        @Override
        public void run(){
            //GlobalParameterApplication gpa = (GlobalParameterApplication) getApplicationContext();

            try {
                String token = getToken(GlobalParameterApplication.getSurl(), email, password);
                if (token.length() == 0) {

                }
                else {
                    GlobalParameterApplication.setLogin_status(1);
                    GlobalParameterApplication.setToken(token);

                    JSONObject data = new JSONObject();
                    data.put("token",token);
                    URL url=new URL(GlobalParameterApplication.getSurl()+"/usr_get");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    InputStream input = conn.getInputStream();
                    String ostr = IOUtils.toString(input);
                    org.json.JSONObject outjson = null;
                    outjson = new org.json.JSONObject(ostr);
                    int result = outjson.getJSONObject("userinfo").getInt("ID");
                    String jc = outjson.getJSONObject("userinfo").getString("jaccount");
                    Log.i("LIANGYUDING",jc);
                    if (jc.length() > 0)
                        GlobalParameterApplication.whtherBindJC = 1;
                    else
                        GlobalParameterApplication.whtherBindJC = 0;
                    GlobalParameterApplication.setJaccount(jc);
                    GlobalParameterApplication.setUserID(result);
                    GlobalParameterApplication.start(token);
                    conn.disconnect();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


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


    public String getToken(String surl, String name, String psw) throws IOException {
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
        conn.disconnect();
        return result;
    }

    public static HashMap<String , String> getVersionNumber(){
        String versionNumber = "";
        HashMap<String ,String> version = new HashMap<>();

        try {
            URL url = new URL(GlobalParameterApplication.getSurl() + "/version_code");
            Log.i("Find product", "1");
            try {
                Log.i("LoveCoin", "getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                java.lang.String ostr ;
                org.json.JSONObject outjson = null;

                if (conn.getResponseCode() == 200){

                    ostr = IOUtils.toString(conn.getInputStream());
                    /*
                    InputStream is = conn.getInputStream();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while ((i = is.read()) != -1) {
                        baos.write(i);
                    }
                    ostr = baos.toString();*/
                    try {
                        org.json.JSONObject result = new org.json.JSONObject(ostr);
                        version.put("versionCode",result.getString("version_code"));
                        version.put("desp",result.getString("desp"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return version;
    }
}
