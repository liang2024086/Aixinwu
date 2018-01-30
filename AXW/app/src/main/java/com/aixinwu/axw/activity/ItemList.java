package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.internal.widget.ThemeUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.OnSailAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.SearchAdapter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ItemList extends Activity {
    private String MyToken;
    private static List<Bean> upData=new ArrayList<Bean>();
    private OnSailAdapter upadapter;
    private ListView uplist;
    //private static List<Bean> downData =new ArrayList<Bean>();
    //private OnSailAdapter downadapter;
    //private ListView downlist;
    private int now;
    private int noww;
    private String surl = GlobalParameterApplication.getSurl();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        uplist = (ListView) findViewById(R.id.itemlistviewup);
        //downlist = (ListView) findViewById(R.id.itemlistviewdown);
        upadapter = new OnSailAdapter(this, upData, R.layout.item_bean_list,nHandler);
        //downadapter = new OnSailAdapter(this, downData, R.layout.item_bean_list);
        uplist.setAdapter(upadapter);
        //downlist.setAdapter(downadapter);
        uplist.setVisibility(View.VISIBLE);
        //downlist.setVisibility(View.VISIBLE);
        mThread.start();
        /*uplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("itemId", upData.get(i).getItemId());
                intent.putExtra("caption",upData.get(i).getType());
                intent.setClass(ItemList.this, Buy.class);
                startActivity(intent);
            }
        });
        uplist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                now = i;
                new AlertDialog.Builder(ItemList.this)
                        .setTitle("提示")
                        .setMessage("是否下架该商品？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ii) {
                                new Thread(){
                                    @Override
                                    public void run(){
                                        super.run();
                                        changestatus(upData.get(now).getItemId(),2);
                                        getDbData();
                                        Message msg = new Message();
                                        msg.what = 1321;
                                        nHandler.sendMessage(msg);

                                    }
                                }.start();

                            }
                        })
                        .setNegativeButton("否", null)
                        .show();

                return false;
            }
        });*/

        /*
        downlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                noww = i;
                new AlertDialog.Builder(ItemList.this)
                        .setTitle("提示")
                        .setMessage("是否上架该商品？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ii) {
                                new Thread(){
                                    @Override
                                    public void run(){
                                        super.run();
                                        changestatus(downData.get(noww).getItemId(),0);
                                        Message msg = new Message();
                                        getDbData();
                                        msg.what = 1321;
                                        nHandler.sendMessage(msg);

                                    }
                                }.start();

                            }
                        })
                        .setNegativeButton("否", null)
                        .show();

                return false;
            }
        });
        */

    }
    public Thread mThread = new Thread(){
        @Override
        public void run(){
            super.run();
            getDbData();
            Message msg = new Message();
            msg.what=1321;
            nHandler.sendMessage(msg);
        }

    };
    public Handler nHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){

                case 1322:

                    final int nowPosition = Integer.valueOf(msg.getData().getString("position"));
                    final int whtherOn = Integer.valueOf(msg.getData().getString("whetherOn"));
                    Toast.makeText(getApplicationContext(), ""+nowPosition + " " + whtherOn,
                            Toast.LENGTH_SHORT).show();
                    new Thread(){
                        @Override
                        public void run(){
                            super.run();

                            changestatus(upData.get(nowPosition).getItemId(),whtherOn);
                            getDbData();
                            Message msg1 = new Message();
                            msg1.what = 1321;
                            nHandler.sendMessage(msg1);

                        }
                    }.start();
                    break;
                case 1321:

                    int totalHeight = 0;
                    for (int i = 0; i < upadapter.getCount(); i++) {
                        View listItem = upadapter.getView(i, null, uplist);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = uplist.getLayoutParams();
                    params.height = totalHeight + (uplist.getDividerHeight() * (uplist.getCount()));
                    // params.height = params.height;
                    uplist.setLayoutParams(params);

                    upadapter.notifyDataSetChanged();
                    /*totalHeight = 0;
                    for (int i = 0; i < downadapter.getCount(); i++) {
                        View listItem = downadapter.getView(i, null, downlist);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params1 = downlist.getLayoutParams();
                    params1.height = totalHeight + (downlist.getDividerHeight() * (downlist.getCount()));
                    // params.height = params.height;
                    downlist.setLayoutParams(params1);

                    downadapter.notifyDataSetChanged();
                    */
                    break;

            }
        }
    };

    private void getDbData(){
        MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();
        data.put("token",MyToken);

        Log.i("UsedDeal", "get");
            try {
                URL url = new URL(surl + "/item_get_list");
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
                        result = outjson.getJSONArray("items");
                        upData.clear();
                        //downData.clear();
                        //for (int i = 0; i < result.length(); i++) {
                        for (int i = result.length()-1; i >= 0 ; i--) {
                            String[] rr = result.getJSONObject(i).getString("images").split(",");
                            if (result.getJSONObject(i).getInt("status")==0) {
                                if (rr[0] == "") {
                                    BitmapFactory.Options cc = new BitmapFactory.Options();
                                    cc.inSampleSize = 20;
                                    upData.add(new Bean(result.getJSONObject(i).getInt("ID"), "http://202.120.47.213:12345/img/1B4B907678CCD423", result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description"),"上架中",1));
                                } else
                                    upData.add(new Bean(result.getJSONObject(i).getInt("ID"),GlobalParameterApplication.imgSurl + rr[0], result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description"),"上架中",1));
                            } else{
                                if (rr[0] == "") {
                                    BitmapFactory.Options cc = new BitmapFactory.Options();
                                    cc.inSampleSize = 20;
                                    //downData.add(new Bean(result.getJSONObject(i).getInt("ID"), "http://202.120.47.213:12345/img/1B4B907678CCD423", result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description"),"已下架",0));
                                    upData.add(new Bean(result.getJSONObject(i).getInt("ID"), "http://202.120.47.213:12345/img/1B4B907678CCD423", result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description"),"已下架",0));
                                } else
                                    //downData.add(new Bean(result.getJSONObject(i).getInt("ID"), "http://202.120.47.213:12345/img/" + rr[0], result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description"),"已下架",0));
                                    upData.add(new Bean(result.getJSONObject(i).getInt("ID"), GlobalParameterApplication.imgSurl + rr[0], result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description"),"已下架",0));
                            }
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

    public void changestatus(int id, int ch){
        MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();
        JSONObject itemInfo = new JSONObject();
        itemInfo.put("ID",id);
        itemInfo.put("status",ch);
        data.put("itemInfo",itemInfo);
        data.put("token",MyToken);

        Log.i("UsedDeal", "get");

        URL url = null;
        try {
            url = new URL(surl + "/item_set");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i("UsedDeal","getconnection");
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try {
            conn.getOutputStream().write(data.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ostr = null;
        try {
            ostr = IOUtils.toString(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        org.json.JSONObject outjson = null;
        JSONArray result = null;
        System.out.print(ostr);


    }

}
