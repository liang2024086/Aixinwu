package com.aixinwu.axw.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.Tool;
import com.aixinwu.axw.tools.DownloadTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

public class SettingActivity extends Activity {

    private View redDot;

    private TextView checkVersion;
    private RelativeLayout checkUpdate;

    private RelativeLayout personalInfo;
    private RelativeLayout headPortrait;
    private TextView resetJaccount;
    private EditText nickName;

    private RelativeLayout submit;

    private TextView newVersion;

    private ImageView myPic;

    private String pathImage="";

    private Tool am = new Tool();

    private String updateDesp = "";

    private Handler dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 843023:
                    Toast.makeText(SettingActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);*/
                    finish();
                    break;
                case 932466:
                    Toast.makeText(SettingActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    break;
                case 490345:
                    Toast.makeText(SettingActivity.this,"绑定成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 285392:
                    Toast.makeText(SettingActivity.this,"绑定失败",Toast.LENGTH_SHORT).show();
                    break;
                case 395923:
                    if (GlobalParameterApplication.wetherHaveNewVersion){
                        new  AlertDialog.Builder(SettingActivity.this)
                                .setTitle("爱心屋APP下载" )
                                .setMessage("搜索到有新版本，是否下载？\n"+updateDesp)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int ii) {
                                        DownloadTask downloadTask = new DownloadTask(SettingActivity.this);
                                        downloadTask.execute("http://salary.aixinwu.info/apk/axw.apk");
                                    }
                                })
                                .setNegativeButton("取消",null).show();
                    }
                    else{
                        new  AlertDialog.Builder(SettingActivity.this)
                                .setTitle("爱心屋APP下载" )
                                .setMessage("当前已经是最新版本" )
                                .setPositiveButton("确定", null).show();
                    }

                    if (GlobalParameterApplication.wetherHaveNewVersion){
                        redDot.setVisibility(View.VISIBLE);
                        newVersion.setText("(发现新版本)");
                    }
                    else{
                        redDot.setVisibility(View.GONE);
                        newVersion.setText("(已是最新版本)");
                    }

                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        personalInfo = (RelativeLayout) findViewById(R.id.myInfo);
        headPortrait = (RelativeLayout) findViewById(R.id.headPortrait);
        resetJaccount = (TextView) findViewById(R.id.reSet);
        nickName = (EditText)findViewById(R.id.editNickName);
        myPic = (ImageView) findViewById(R.id.myPic);
        submit = (RelativeLayout) findViewById(R.id.submit);
        redDot = findViewById(R.id.redDot);
        checkVersion = (TextView) findViewById(R.id.checkVersion);
        checkUpdate = (RelativeLayout) findViewById(R.id.checkUpdate);
        newVersion = (TextView) findViewById(R.id.newVersion);


        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //has permission, do operation directly

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String , String> versionInfo = WelcomeActivity.getVersionNumber();
                            String versionNumber = versionInfo.get("versionCode");
                            updateDesp = versionInfo.get("desp");
                            int a = versionNumber.compareTo(GlobalParameterApplication.versionName);
                            if (versionNumber.compareTo(GlobalParameterApplication.versionName) > 0){
                                GlobalParameterApplication.wetherHaveNewVersion = true;
                            }
                            else GlobalParameterApplication.wetherHaveNewVersion = false;

                            Message msg = new Message();
                            msg.what = 395923;
                            dHandler.sendMessage(msg);
                        }
                    }).start();

                } else {
                    //do not have permission
                    Log.i("DEBUG_TAG", "user do not have this permission!");

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Log.i("DEBUG_TAG", "we should explain why we need this permission!");
                    } else {

                        // No explanation needed, we can request the permission.
                        Log.i("DEBUG_TAG", "==request the permission==");

                        try {
                            ActivityCompat.requestPermissions(SettingActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    5698);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nickname = nickName.getText().toString();

                 /*if (pathImage.length() <= 1){
                    Message msg = new Message();
                   if (changeUsrInfo(nickname,"") == 0){
                        msg.what = 843023;
                    }
                    else
                        msg.what = 932466;
                    dHandler.sendMessage(msg);
                }else{*/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String imgUrl = uploadPic();
                            Message msg = new Message();
                            System.out.println("JSIDF  "+imgUrl);
                            if (changeUsrInfo(nickname,imgUrl) == 0){
                                msg.what = 843023;
                            }
                            else
                                msg.what = 932466;
                            dHandler.sendMessage(msg);
                        }
                    }).start();

                }
            //}
        });

        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        headPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //has permission, do operation directly

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                } else {
                    //do not have permission
                    Log.i("DEBUG_TAG", "user do not have this permission!");

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Log.i("DEBUG_TAG", "we should explain why we need this permission!");
                    } else {

                        // No explanation needed, we can request the permission.
                        Log.i("DEBUG_TAG", "==request the permission==");

                        try {
                            ActivityCompat.requestPermissions(SettingActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1122);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }
        });

        resetJaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        if (bindJaccount() == 0){
                            msg.what = 490345;
                        }
                        else
                            msg.what = 285392;
                        dHandler.sendMessage(msg);
                    }
                }).start();

            }
        });
    }

    private int bindJaccount(){
        int status = -1;
        String MyToken= GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        JSONObject orderrequest = new JSONObject();
        orderrequest.put("token", MyToken);

        try {
            URL url = new URL(surl + "/aixinwu_associate_jaccount");
            try {
                Log.i("Order","getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                Log.i("usr_update", orderrequest.toJSONString());
                conn.getOutputStream().write(orderrequest.toJSONString().getBytes());

                java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                org.json.JSONObject outjson = null;
                try{
                    outjson = new org.json.JSONObject(ostr);
                    status = outjson.getJSONObject("status").getInt("code");
                    System.out.println(outjson);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1122: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    Log.i("DEBUG_TAG", "user granted the permission!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("DEBUG_TAG", "user denied the permission!");
                }
                return;
            }
            case 5698: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String , String> versionInfo = WelcomeActivity.getVersionNumber();
                            String versionNumber = versionInfo.get("versionCode");
                            updateDesp = versionInfo.get("desp");
                            int a = versionNumber.compareTo(GlobalParameterApplication.versionName);
                            if (versionNumber.compareTo(GlobalParameterApplication.versionName) > 0){
                                GlobalParameterApplication.wetherHaveNewVersion = true;
                            }
                            else GlobalParameterApplication.wetherHaveNewVersion = false;

                            Message msg = new Message();
                            msg.what = 395923;
                            dHandler.sendMessage(msg);
                        }
                    }).start();
                    Log.i("DEBUG_TAG", "user granted the permission!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("DEBUG_TAG", "user denied the permission!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (GlobalParameterApplication.wetherHaveNewVersion){
            redDot.setVisibility(View.VISIBLE);
            newVersion.setText("(发现新版本)");
        }
        else{
            redDot.setVisibility(View.GONE);
            newVersion.setText("(已是最新版本)");
        }

        BitmapFactory.Options cc = new BitmapFactory.Options();
        cc.inSampleSize=8;

        Bitmap addbmp=BitmapFactory.decodeFile(pathImage,cc);
        myPic.setImageBitmap(addbmp);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK && requestCode==1) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                if (null == cursor) {
                    Log.i("FIND PIC","can't find pic");
                    return;
                }
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

            } else {
                pathImage = uri.getPath();

            }
        }
    }

    protected  String uploadPic(){
        String Picset = "";
            //   Toast.makeText(getActivity(),"Begin transication",Toast.LENGTH_SHORT).show();

            try {
                String imageID = am.sendFile(GlobalParameterApplication.getSurl(),pathImage);
                Picset = imageID;
                //      Toast.makeText(getActivity(),Picset.size(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return Picset;

    }

    private int changeUsrInfo(String nickName, String imgId){
        int status = -1;
        String MyToken = GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        JSONObject orderrequest = new JSONObject();

        JSONObject userInfo = new JSONObject();

        orderrequest.put("token", MyToken);
        if (nickName.length() > 0)
            userInfo.put("nickname",nickName);

        if (imgId.length() > 0)
            userInfo.put("image", imgId);

        orderrequest.put("userinfo",userInfo);
        //data.put("token", MyToken);


        try {
            URL url = new URL(surl + "/usr_update");
            try {
                Log.i("Order","getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                Log.i("usr_update", orderrequest.toJSONString());
                conn.getOutputStream().write(orderrequest.toJSONString().getBytes());

                java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                org.json.JSONObject outjson = null;
                try{
                    outjson = new org.json.JSONObject(ostr);
                    status = outjson.getJSONObject("status").getInt("code");
                    System.out.println(outjson);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return status;
    }

    protected File downLoadFile(String httpUrl) {
        // TODO Auto-generated method stub
        final String fileName = "axw.apk";
        File tmpFile = new File(Environment.getExternalStorageDirectory()+"/AXWupdate/");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File(Environment.getExternalStorageDirectory()+"/AXWupdate/" + fileName);

        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[256];
                conn.connect();
                double count = 0;
                if (conn.getResponseCode() >= 400) {
                    Toast.makeText(SettingActivity.this, "连接超时", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    while (count <= 100) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }

                        } else {
                            break;
                        }

                    }
                }
                openFile(file);
                conn.disconnect();
                fos.close();
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }

        return file;
    }
//打开APK程序代码

    private void openFile(File file) {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
