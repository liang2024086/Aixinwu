package com.aixinwu.axw.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.aixinwu.axw.Adapter.RecordAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Record;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.view.MyScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MyDonation extends ActionBarActivity {

    private ListView donateListView;
    private SimpleAdapter simpleAdapter;
    private ArrayList<HashMap<String,String>> donateMaps = new ArrayList<HashMap<String, String>>();
    private ArrayList<Record> record = new ArrayList<>();


    private Handler dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 395932:
                    simpleAdapter = new SimpleAdapter(MyDonation.this,donateMaps,R.layout.donation_item,new String[]{"desc","barcode","produced_at"},new int[]{R.id.t1,R.id.t2,R.id.t3});
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object o, String s) {
                            if (view instanceof TextView && o instanceof String) {
                                TextView i = (TextView) view;
                                i.setText((String) o);
                                return true;
                            }
                            return false;
                        }
                    });
                    donateListView.setAdapter(simpleAdapter);
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donation);

        donateListView = (ListView) findViewById(R.id.myOwnDonation);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_donation, menu);
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

    private void getDbData(){
        String MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();

        data.put("token",MyToken);

        {

            try {
                URL url = new URL(GlobalParameterApplication.getSurl() + "/donate_get");
                try {
                    Log.i("UsedDeal", "getconnection");
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
                        result = outjson.getJSONArray("records");
                        for (int i = result.length()-1; i >=0 ; i--){
                            org.json.JSONObject tmpJSONObject = result.getJSONObject(i);
                            //System.out.println(a.toString());

                            String desc = tmpJSONObject.getString("desc");
                            String barcode = tmpJSONObject.getString("barcode");
                            String donateTime = tmpJSONObject.getString("produced_at");

                            HashMap<String,String> donateMap = new HashMap<>();
                            donateMap.put("desc",desc);
                            donateMap.put("barcode",barcode);
                            donateMap.put("produced_at",donateTime);
                            donateMaps.add(donateMap);
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
}
