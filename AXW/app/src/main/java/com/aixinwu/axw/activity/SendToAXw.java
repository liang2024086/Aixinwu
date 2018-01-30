package com.aixinwu.axw.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.Tool;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SendToAXw extends Activity {

    private RelativeLayout buttonPublish;
    private EditText itemname;
    private String ItemName;
    private EditText itemnumber;
    private int itemnum;
    private EditText jaccount;
    private String JaccountID;
    private String barcode;
    private int code = -1;
    private final String surl = GlobalParameterApplication.getSurl();
    public  java.lang.String MyToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_send_to_axw);
        itemname=(EditText)findViewById(R.id.itemname1);
        itemnumber=(EditText)findViewById(R.id.itemnumber);
        jaccount=(EditText)findViewById(R.id.jaccountID);
        buttonPublish = (RelativeLayout)findViewById(R.id.donate);
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalParameterApplication gpa = (GlobalParameterApplication) getApplicationContext();
                if(gpa.getLogin_status()==0) Toast.makeText(SendToAXw.this,"你跪了",Toast.LENGTH_LONG).show();
                else{

                    //Toast.makeText(SendToAXw.this,"nih",Toast.LENGTH_SHORT).show();
                    ItemName = itemname.getText().toString();
                    if (!itemnumber.getText().toString().isEmpty())
                        itemnum = Integer.parseInt(itemnumber.getText().toString());
                    //JaccountID = "liangyuding";//jaccount.getText().toString();
                    JaccountID = GlobalParameterApplication.getJaccount();


                    ItemName = ItemName+"*"+itemnum;
                    // String itemID = AddItem(TypeName,money,Descrip,YesorNo);
                    //if (!itemname.getText().toString().isEmpty() && !itemnumber.getText().toString().isEmpty() && !jaccount.getText().toString().isEmpty())
                    if (!ItemName.isEmpty() && itemnum != 0 && !JaccountID.isEmpty()){
                        final ProgressDialog progressDialog = new ProgressDialog(SendToAXw.this,
                                R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("发布中...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                        new Thread(runnable1).start();
                    }


                }

//                Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
                /*
                AddImage 部分 将itemID和imageID绑定上传
                 */
            }
        });

    }
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {

            MyToken=GlobalParameterApplication.getToken();
            JSONObject data = new JSONObject();
            JSONObject iteminfo = new JSONObject();
            iteminfo.put("jacount_id",JaccountID);
            //iteminfo.put("jacount_id","liangyuding");
            iteminfo.put("desc",ItemName);
            data.put("itemInfo",iteminfo);
            data.put("token",MyToken);
            try {
                URL url = new URL(surl + "/item_add_aixinwu");
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(1000);
                    conn.setReadTimeout(1000);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    java.lang.String oss = IOUtils.toString(conn.getInputStream());
                    try{
                        org.json.JSONObject res = new org.json.JSONObject(oss);
                        code = res.getJSONObject("status").getInt("code");
                        barcode = res.getJSONObject("item_info").getString("barcode");
                        Message msg = new Message();
                        msg.what = 134;
                        nHandler.sendMessage(msg);
                        System.out.println(oss);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }
    };
    public Handler nHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 134:
                    if (code == 0){
                        AlertDialog.Builder dialogA = new  AlertDialog.Builder(SendToAXw.this)
                                .setTitle("消息")
                                .setMessage("爱心屋捐赠成功\n" + "捐赠条码为：" + barcode + "\n感谢您的捐赠!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int ii) {
                                        finish();
                                    }
                                });
                        dialogA.setCancelable(false);
                        dialogA.show();
                    }
                    //SendToAXw.this.setResult(RESULT_OK);
                    break;
            }
        }
    };
}
