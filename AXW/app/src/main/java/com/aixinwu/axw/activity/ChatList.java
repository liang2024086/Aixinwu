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
import com.aixinwu.axw.tools.talkmessage;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import io.nats.client.ConnectionFactory;
import schoolapp.chat.Chat;

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
    //   private Chat chat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Files = fileList();
        //     chat = new Chat(GlobalParameterApplication.getToken(), ConnectionFactory.DEFAULT_URL);
        //   try {
        //     chat.start();
        //} catch (IOException | TimeoutException e) {
        //   e.printStackTrace();
        // }
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
                Intent intent = new Intent(ChatList.this,Chattoother.class);
                intent.putExtra("To",Integer.parseInt(chatitem.get(i).get("Name")));
                intent.putExtra("itemID",Integer.parseInt(chatitem.get(i).get("Item")));
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
        List<talkmessage> result = new ArrayList<>();
        ArrayList<Integer> chec = new ArrayList<Integer>();
        result = GlobalParameterApplication.gettalklist(GlobalParameterApplication.getUserID());
        chatitem.clear();
        for (talkmessage re0: result){
            HashMap<String,String> tt = new HashMap<String,String>();
            Integer ss = re0.getSender()+re0.getReceiver()-GlobalParameterApplication.getUserID();
            if (!chec.contains(ss)) {
                chec.add(ss);
                tt.put("Name", ss.toString());
                tt.put("Item",String.valueOf(GlobalParameterApplication.query(ss)));
                chatitem.add(tt);
            }
        }




    }
}
