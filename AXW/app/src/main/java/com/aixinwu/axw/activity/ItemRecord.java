package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.icu.text.AlphabeticIndex;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.RecordAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Record;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ItemRecord extends Activity {

    private ListView recordItem;
    private ArrayList<Record> record = new ArrayList<>();


    private Handler dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 395932:
                    RecordAdapter recordAdapter = new RecordAdapter(ItemRecord.this,record);
                    recordItem.setAdapter(recordAdapter);
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_record);

        recordItem = (ListView) findViewById(R.id.recordItem);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getDbData();
                Message msg = new Message();
                msg.what = 395932;
                dHandler.sendMessage(msg);
                //getDbItemData();
            }
        }).start();
    }


    private void getDbData(){
        String MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();

        data.put("token",MyToken);
        data.put("offset",0);
        data.put("length",0);

        {

            try {
                URL url = new URL(GlobalParameterApplication.getSurl() + "/aixinwu_order_get");
                try {
                    Log.i("UsedDeal","getconnection");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                    org.json.JSONObject outjson = null;
                    try {
                        JSONArray result = null;
                        outjson = new org.json.JSONObject(ostr);
                        //System.out.println(ostr);
                        result = outjson.getJSONArray("orders");
                        for (int i = 0; i < result.length(); i++){
                            org.json.JSONObject a = result.getJSONObject(i);
                            //System.out.println(a.toString());

                            org.json.JSONArray abc = a.getJSONArray("items");
                            String imgUrl = "";
                            for (int j = 0; j < abc.length(); ++j){
                                String abcd = (abc.getJSONObject(j).getString("image")).split(",")[0];
                                if (j == 0)
                                    imgUrl = abcd;
                                else
                                    imgUrl = imgUrl + "," + abcd;
                                //System.out.println(abcd);
                            }

                            record.add(new Record(a.getString("id"),
                                    a.getString("customer_id"),
                                    a.getString("consignee_id"),
                                    a.getString("order_sn"),
                                    a.getString("total_product_price"),
                                    a.getString("update_at"),
                                    imgUrl
                            ));


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }

    private void getDbItemData(){
        String MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();
System.out.println("getDbItemData:");
        data.put("token",MyToken);
        data.put("order_id",98184);

        {

            Log.i("UsedDeal", "get");
            try {
                URL url = new URL(GlobalParameterApplication.getSurl() + "/aixinwu_order_item_get");
                try {
                    Log.i("UsedDeal","getconnection");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                    org.json.JSONObject outjson = null;
                    try {
                        JSONArray result = null;
                        outjson = new org.json.JSONObject(ostr);
                        System.out.println(ostr);
                        /*result = outjson.getJSONArray("orders");
                        for (int i = 0; i < result.length(); i++){
                            org.json.JSONObject a = result.getJSONObject(i);
                            System.out.println(a.toString());
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }

}
