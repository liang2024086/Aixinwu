package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.talkmessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
        sim_adapter = new SimpleAdapter(this,chatitem,R.layout.chatlist_item,new String[]{"Name","Item","Doc","Time","Img"},new int[]{R.id.name,R.id.itemid,R.id.product,R.id.messageTime,R.id.img_activity_product});
        sim_adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if (view instanceof TextView && o instanceof String){
                    TextView i = (TextView) view;
                    i.setText((String) o);
                    return true;
                }
                if (view instanceof ImageView && o instanceof String){
                    ImageView img = (ImageView) view;
                    String imgUrl = (String) o;
                    if (!o.equals(""))
                        ImageLoader.getInstance().displayImage(GlobalParameterApplication.imgSurl+imgUrl,img);
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
                intent.putExtra("To",Integer.parseInt(chatitem.get(i).get("usrId")));
                intent.putExtra("itemID",Integer.parseInt(chatitem.get(i).get("Item")));
                intent.putExtra("ToName",chatitem.get(i).get("Name"));
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

    static public HashMap<String,String> getUserName(String userId){
        //Product dbData = null;
        String usrName = "";
        HashMap<String, String> output = new HashMap<>();

        try {
            URL url = new URL(GlobalParameterApplication.getSurl() + "/usr_get_by_id/"+userId);
            Log.i("Find product", "1");
            try {
                Log.i("LoveCoin", "getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                java.lang.String ostr ;
                org.json.JSONObject outjson = null;

                if (conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while ((i = is.read()) != -1) {
                        baos.write(i);
                    }
                    ostr = baos.toString();
                    try {
                        outjson = new org.json.JSONObject(ostr);
                        // Log.i("Inall", result.length() + "");

                        String myUserName = outjson.getString("username");
                        String myNickName = outjson.getString("nickname");
                        //String imgSrul = outjson.getString("img");
                        if (myNickName.length() == 0)
                            usrName = myUserName;
                        else usrName = myNickName;
                        output.put("usrName",usrName);
                        output.put("img",outjson.getString("image"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return output;
    }



    public void getChatlist(){
        List<talkmessage> result = new ArrayList<>();
        ArrayList<Integer> chec = new ArrayList<Integer>();
        result = GlobalParameterApplication.gettalklist(GlobalParameterApplication.getUserID());
        chatitem.clear();
        for (int i = result.size()-1; i>= 0 ; --i){
            talkmessage re0 = result.get(i);
            HashMap<String,String> tt = new HashMap<String,String>();
            Integer ss = re0.getSender()+re0.getReceiver()-GlobalParameterApplication.getUserID();
            if (!chec.contains(ss)) {
                chec.add(ss);

                String usrName = null;
                GetNameThread getNameThread = new GetNameThread(ss.toString());
                getNameThread.start();
                try {
                    getNameThread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }

                tt.put("usrId",ss.toString());
                tt.put("Name", getNameThread.getUsrName());
                tt.put("Item", String.valueOf(GlobalParameterApplication.query(ss)));
                tt.put("Doc",re0.getDoc());
                tt.put("Time",re0.getTime());
                tt.put("Img",getNameThread.getImgUrl());
                chatitem.add(tt);
            }
        }
    }

    class GetNameThread extends Thread{

        private String usrId;
        private String usrName;
        private String imgUrl;

        public GetNameThread(String usrId){
            this.usrId = usrId;
        }

        public String getUsrName(){
            return this.usrName;
        }

        public String getImgUrl() {
            return this.imgUrl;
        }
        @Override
        public void interrupt() {
            super.interrupt();
        }

        @Override
        public void run(){
            try{
                HashMap<String,String> info = getUserName(usrId);
                usrName = info.get("usrName");
                imgUrl = info.get("img");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
