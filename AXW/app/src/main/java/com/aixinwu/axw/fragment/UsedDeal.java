package com.aixinwu.axw.fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.Tool;

import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.Buy;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.SearchAdapter;
import com.aixinwu.axw.view.SearchView;

import org.apache.commons.io.IOUtils;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UsedDeal extends Fragment implements SearchView.SearchViewListener{
    public ListView lvResults;
    public boolean needtochange = false;
    public SearchView searchView;
    public SearchAdapter resultAdapter;
    public String MyToken;
    public String needtochange_text="";
    public Tool am = new Tool();
    public static List<Bean> dbData;
    public static List<Bean> resultData;
    public String surl = GlobalParameterApplication.getSurl();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


      //  Toast.makeText(getActivity(),"onCreat",Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.used_deal,null);

        //Toast.makeText(getActivity(),"get"+dbData.size(),Toast.LENGTH_LONG).show();

        lvResults = (ListView) view.findViewById(R.id.main_lv_search_results);
        searchView = (SearchView)view.findViewById(R.id.main_search_layout);
        searchView.setSearchViewListener(this);

        lvResults.setVisibility(View.VISIBLE);
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              //  Toast.makeText(getActivity(), i + " is what you want!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("itemId", resultData.get(i).getItemId());

                intent.setClass(getActivity(), Buy.class);
                startActivity(intent);


            }
        });
        dbData=new ArrayList<Bean>();
      //  resultData = new ArrayList<Bean>();
        mThread.start();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getResultData(null);

    //    Toast.makeText(getActivity(),"onCreatview",Toast.LENGTH_LONG).show();
        return view;
    }
    public Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            getDbData();
            Message msg= new Message();
            msg.what = 23212;


        }
    });

    public Handler handler = new Handler(){
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case 23214:

                   if(needtochange){
                        getResultData(needtochange_text);
                       needtochange=false;
                       needtochange_text="";
                   }
                   break;
           }
       }

   };
    @Override
    public void onRefreshAutoComplete(String text) {

    }

    @Override
    public void onSearch(String text) {
        needtochange = true;
        needtochange_text = text;
        Thread mThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                getDbData();
                Message msg= new Message();
                msg.what = 23214;
                resultData.clear();
                resultData.addAll(dbData);
                //        handler.sendMessage(msg);
            }
        });
        mThread1.start();
        try {
            mThread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getResultData(text);

        //Toast.makeText(getActivity(),"get in search"+text+resultData.isEmpty()+dbData.isEmpty(), Toast.LENGTH_SHORT).show();


        //Toast.makeText(getActivity(),"Finish Search"+lvResults.getAdapter().getCount(), Toast.LENGTH_SHORT).show();
       // Toast.makeText(getActivity(),"Finish Search1"+resultData.size(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取所有商品信息，用dbData存储
     * itemId：商品编号
     * picId：一张示意图的编号
     * type：种类
     * doc：说明
     */
    private void getDbData(){
        MyToken=GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();

         {

            Log.i("UsedDeal", "get");
            try {
                URL url = new URL(surl + "/item_get_all");
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
                        dbData.clear();
                        for (int i = 0; i < result.length(); i++) {
                            String[] rr = result.getJSONObject(i).getString("images").split(",");
                            if (rr[0]=="") {
                                BitmapFactory.Options cc = new BitmapFactory.Options();
                                cc.inSampleSize = 20;
                                dbData.add(new Bean(result.getJSONObject(i).getInt("ID"), BitmapFactory.decodeResource(getResources(), R.drawable.icon, cc), result.getJSONObject(i).getString("ownerID"), result.getJSONObject(i).getString("description")));
                            } else
                                dbData.add(new Bean(result.getJSONObject(i).getInt("ID"), am.DownloadFile(surl, rr[0], 20), result.getJSONObject(i).getString("ownerID"), result.getJSONObject(i).getString("description")));
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
    private void getResultData(String text){
        if (resultData == null){
            resultData = new ArrayList<Bean>();
            resultData.clear();
            resultData.addAll(dbData);

        } else {
            resultData.clear();
            if (text ==null) resultData.addAll(dbData);else
            for (int i = 0; i < dbData.size(); i++){
                if ((dbData.get(i).getDoc()+dbData.get(i).getType()).contains(text.trim())){
                    resultData.add(dbData.get(i));
          //          Toast.makeText(getActivity(),"result"+text+resultData.size(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        //Toast.makeText(getActivity(),"Find answer"+resultData.size(), Toast.LENGTH_SHORT).show();
        if (resultAdapter == null) {
            resultAdapter = new SearchAdapter(getActivity(), resultData, R.layout.item_bean_list);
        } else {
            resultAdapter.notifyDataSetChanged();
        }
        if (lvResults.getAdapter() == null){
            lvResults.setAdapter(resultAdapter);
        }else{
            resultAdapter.notifyDataSetChanged();
        }
        lvResults.setVisibility(View.VISIBLE);
    }
}
