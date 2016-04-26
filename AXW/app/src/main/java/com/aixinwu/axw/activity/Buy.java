package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.Tool;
/**
 * Created by liangyuding on 2016/4/15.
 */
public class Buy extends Activity{
    private final String surl = GlobalParameterApplication.getSurl();
    public  java.lang.String MyToken;
    private Tool am = new Tool();
    private boolean flag;
    private boolean flag1 = false;
    private int itemID;
    private String OwnerID;
    private String Desc;
    private String Price;
    private String Picset;
    private ListView pics;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private EditText commentword;
    private String CommentWord;
    private Button commentsubmit;
    private ListView comments;
    private String[] picts;
    private ArrayList<HashMap<String, Object>> pic_list;
    private SimpleAdapter sim_adapter;
    private SimpleAdapter com_adapter;
    private Context mContext;
    private String commentwords;
    private ArrayList<String> User;
    private ArrayList<String> TimeStamp;
    private ArrayList<String> Comments = new ArrayList<String>();
    private ArrayList<HashMap<String,Object>> comment_list;
    //private Handler nhandler;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.i("AAAAA", "WHY");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        Intent intent=getIntent();
        Bundle out = intent.getExtras();
        itemID=(int)out.get("itemId");
        textView1 = (TextView)findViewById(R.id.ownerid);
        textView2 = (TextView)findViewById(R.id.desc);
        textView3 = (TextView)findViewById(R.id.price3);
        textView4 = (TextView)findViewById(R.id.shuoming);
        commentword = (EditText)findViewById(R.id.commentword);
        commentsubmit = (Button)findViewById(R.id.commentsubmit);
        comments = (ListView)findViewById(R.id.comments);
        pics = (ListView)findViewById(R.id.picdetail);
        pic_list = new ArrayList<HashMap<String, Object>>();
        comment_list=new ArrayList<HashMap<String, Object>>();
        mContext = this;
        flag=false;

        commentsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                commentwords = commentword.getText().toString();
                if(commentwords!=null)
                    if(GlobalParameterApplication.getLogin_status()==1)
                new Thread(new Runnable() {
                    @Override
                    public void run(){
                        URL url = null;
                        try {
                            url = new URL(surl + "/item_add_comment");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        try {
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        try {

                            conn.setRequestMethod("POST");
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }
                        conn.setDoOutput(true);
                            conn.setRequestProperty("Content-Type", "application/json");
                            JSONObject data = new JSONObject();
                            data.put("token", MyToken);
                            JSONObject comment = new JSONObject();
                            comment.put("itemID", itemID);
                            comment.put("content", commentwords);
                            data.put("comment", comment);

                            conn.getOutputStream().write(data.toJSONString().getBytes());
                            String ostr = IOUtils.toString(conn.getInputStream());
                            System.out.println(ostr);
                            GetComments(itemID);
                            comment_list.clear();
                            for (int i = 0; i < Comments.size();i++){
                                HashMap<String,Object> map = new HashMap<String, Object>();
                                map.put("comment",Comments.get(i));
                                comment_list.add(map);
                            }
                            Message Msg = new Message();
                            Msg.what = 231123;
                            nhandler.sendMessage(Msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyToken=GlobalParameterApplication.getToken();
                GetComments(itemID);
                GetInfo(itemID);
                /*
                textView1.setText("用户："+OwnerID);
                textView2.setText("描述："+Desc);
                textView3.setText("价格："+Price);
                textView4.setText("留言：");*/


                picts = Picset.split(",");
                pic_list.clear();
                for (int i = 0; i < picts.length; i++){
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    try {
                        map.put("image", am.DownloadFile(surl,picts[i],10));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   // map.put("image",R.drawable.icon);
                    pic_list.add(map);
                }
                comment_list.clear();
                for (int i = 0; i < Comments.size();i++){
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    map.put("comment",Comments.get(i));
                    comment_list.add(map);
                }
                Message msg=new Message();
                msg.what=2310231;
                nhandler.sendMessage(msg);
                flag=true;

            }
        }).start();
        //while(!flag);
//        textView1.setText("用户："+OwnerID);
  //      textView2.setText("描述："+Desc);
      //  textView3.setText("价格："+Price);
    //    textView4.setText("留言：");




    }
Handler nhandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(textView1!=null){
            switch (msg.what) {
                case 2310231:

                        flag=false;
                        textView1.setText("用户："+OwnerID);
                        textView2.setText("描述："+Desc);
                        textView3.setText("价格："+Price);
                        textView4.setText("留言：");

                        com_adapter=new SimpleAdapter(mContext,comment_list,R.layout.commentitem,new String[]{"comment"},new int[]{R.id.comment_text});
                        com_adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
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
                        comments.setAdapter(com_adapter);
                    comments.setVisibility(View.VISIBLE);
                    int totalHeight = 0;
                    for (int i = 0; i < com_adapter.getCount(); i++) {
                        View listItem = com_adapter.getView(i, null, pics);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = comments.getLayoutParams();
                    params.height = totalHeight + (comments.getDividerHeight() * (com_adapter.getCount() - 1));
                   // params.height = params.height;
                    comments.setLayoutParams(params);
                        String [] from = {"image"};
                        int [] to = {R.id.buyimage};System.out.println("---------------"+pic_list.size()+"----------------");

                        sim_adapter = new SimpleAdapter(mContext,pic_list,R.layout.picitem,from,to);

                    sim_adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
                            @Override
                            public boolean setViewValue(View view, Object o, String s) {
                                if (view instanceof ImageView && o instanceof Bitmap){
                                    ImageView i = (ImageView)view;
                                    i.setImageBitmap((Bitmap) o);
                                    return true;
                                }
                                return false;
                            }


                        });
                        pics.setAdapter(sim_adapter);
                        pics.setVisibility(View.VISIBLE);
                        totalHeight = 0;
                        for (int i = 0; i < sim_adapter.getCount(); i++) {
                            View listItem = sim_adapter.getView(i, null, pics);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params1;

                    params1 = pics.getLayoutParams();
                    params1.height = totalHeight + (pics.getDividerHeight() * (sim_adapter.getCount()));
                    pics.setLayoutParams(params1);
                    break;
                case 231123:
                    //comments.setAdapter(com_adapter);
                    int totalHeight1 = 0;
                    for (int i = 0; i < com_adapter.getCount(); i++) {
                        View listItem = com_adapter.getView(i, null, pics);
                        listItem.measure(0, 0);
                        totalHeight1 += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params11 = comments.getLayoutParams();
                    params11.height = totalHeight1 + (comments.getDividerHeight() * (com_adapter.getCount() - 1));
                    //params11.height = 5* params11.height;
                    comments.setLayoutParams(params11);
                    com_adapter.notifyDataSetChanged();
                    //comments.setVisibility(View.VISIBLE);
                    //flag1 = true;
                   // Toast.makeText(mContext,"We have1 "+comment_list.size()+"comments",Toast.LENGTH_LONG).show();
                    break;
            }
            }

        }

    };
    public void onStart(){
        super.onStart();
       // han'd.makeText(this,"UUonStart",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
      //  if (flag1){
    //        flag1 = false;

      //      Toast.makeText(mContext,"We have "+com_adapter.getCount()+"comments",Toast.LENGTH_LONG).show();

     //   }
        //Toast.makeText(this,"UUonResumekengbi",Toast.LENGTH_LONG).show();;

    }


    @Override
    public void onPause(){
        super.onPause();
        //Toast.makeText(this,"UUonPause",Toast.LENGTH_LONG).show();;
    }
    public void GetComments(int itemID){
        URL url = null;
        try {
            url = new URL(surl + "/item_get_comment");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            JSONObject data = new JSONObject();
            data.put("token",MyToken);
            JSONObject comment = new JSONObject();
            comment.put("itemID",itemID);
            data.put("comment",comment);
            conn.getOutputStream().write(data.toJSONString().getBytes());
            String ostr = IOUtils.toString(conn.getInputStream());
            System.out.println(ostr);
            JSONArray result=null;
            try {
                org.json.JSONObject outjson = new org.json.JSONObject(ostr);
                result=outjson.getJSONArray("comment");
                org.json.JSONObject outt=null;
                Comments.clear();
                for (int i = 0; i < result.length();i++){
                    {
                        outt=result.getJSONObject(i);
                        Comments.add(outt.getString("publisherID")+" "+outt.getString("created")+" "+outt.getString("content"));

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void GetInfo(int itemID) {

        URL url = null;
        try {
            url = new URL(surl + "/item_get");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            JSONObject data = new JSONObject();
            //data.put("token",MyToken);
            JSONObject iteminfo = new JSONObject();
            iteminfo.put("ID",itemID);
            data.put("itemInfo",iteminfo);
            conn.getOutputStream().write(data.toJSONString().getBytes());
            String ostr = IOUtils.toString(conn.getInputStream());
            System.out.println(ostr);
            try {
                org.json.JSONObject outjson = new org.json.JSONObject(ostr);

                OwnerID = outjson.getJSONObject("itemInfo").getString("ownerID");

                Desc = outjson.getJSONObject("itemInfo").getString("description");
                Price = outjson.getJSONObject("itemInfo").getString("price");
                Picset = outjson.getJSONObject("itemInfo").getString("images");
             System.out.println("---------------"+Picset+"----------------");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
