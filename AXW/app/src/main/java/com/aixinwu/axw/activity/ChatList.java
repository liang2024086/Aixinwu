package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatList extends Activity {
    private ListView chatlist;
    private SimpleAdapter sim_adapter;
    private ArrayList<String> Name;
    private String surl = GlobalParameterApplication.getSurl();
    private ArrayList<String> Item;
    private ArrayList<HashMap<String,String>> chatitem;
   // private ArrayList<String> Content;
    private Button refreshbutton;
    private String[] Files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Files = fileList();
        String[] tmp;
        chatitem = new ArrayList<HashMap<String, String>>();
        chatlist = (ListView)findViewById(R.id.chatlist);
        refreshbutton = (Button)findViewById(R.id.refresh);
        chatitem.clear();
        mThread.start();
        sim_adapter = new SimpleAdapter(this,chatitem,R.layout.chatlist_item,new String[]{"Name","Item"},new int[]{R.id.name,R.id.itemid});
        sim_adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if (view instanceof TextView && o instanceof String){
                    TextView i = (TextView) view;
                    i.setText((String) o);
                    return true;
                }
                return false;
            }
        });

        chatlist.setAdapter(sim_adapter);
        chatlist.setVisibility(View.VISIBLE);
        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getChatlist();
                        Message msg = new Message();
                        msg.what = 1324;
                        nHandler.sendMessage(msg);
                    }
                }).start();

            }
        });
        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChatList.this,Chat.class);
                intent.putExtra("To",Integer.parseInt(chatitem.get(i).get("Name").toString()));
                intent.putExtra("itemID",Integer.parseInt(chatitem.get(i).get("Item").toString()));
                startActivity(intent);
            }
        });
    }
    public Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            getChatlist();
            Message msg = new Message();
            msg.what = 1324;
            nHandler.sendMessage(msg);
        }
    });
    public Handler nHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1324:
                    sim_adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    public void getChatlist(){
        try {
            URL url = new URL(surl + "/item_get_chart");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            JSONObject data = new JSONObject();
            JSONObject chatinfo = new JSONObject();
            chatinfo.put("buyer_id", 1);
            chatinfo.put("itemID", 1);
            chatinfo.put("publisher_id", 2);
            chatinfo.put("content", "ssdsdd");
            // data.put("chat", chatinfo);
            JSONObject itemInfo = new JSONObject();
          //  itemInfo.put("ID", 10);
         //   itemInfo.put("status", 0);
           // data.put("itemInfo", itemInfo);
            data.put("token", GlobalParameterApplication.getToken());
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(data.toJSONString().length()));
            conn.getOutputStream().write(data.toJSONString().getBytes());

            String ostr = IOUtils.toString(conn.getInputStream());

            System.out.println(ostr);
            org.json.JSONObject outjson = new org.json.JSONObject(ostr);
            org.json.JSONArray result = null;
            result = outjson.getJSONArray("chat");
            chatitem.clear();
            for (int i = 0; i < result.length(); i++){
                int From = result.getJSONObject(i).getInt("publisher_id");
                int To = result.getJSONObject(i).getInt("buyer_id");
                int itemId = result.getJSONObject(i).getInt("itemID");
                boolean flag = true;
                for (int j = 0; j < chatitem.size(); j++){
                    if (((Integer.parseInt(chatitem.get(j).get("Name"))+GlobalParameterApplication.getUserID())==(To + From))&&(Integer.parseInt(chatitem.get(j).get("Item"))==itemId)){
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    HashMap<String,String> tt = new HashMap<String,String>();
                    Integer ss = (From+To-GlobalParameterApplication.getUserID());
                    tt.put("Name", ss.toString());
                    Integer aa = itemId;
                    tt.put("Item",aa.toString());
                    chatitem.add(tt);}
                }



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
