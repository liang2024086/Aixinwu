package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.PicAdapter;
import com.aixinwu.axw.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class Chat extends Activity{
    public int start = -1;
    ArrayList<HashMap<String,Object>> chatList=null;
    public ArrayList<String> OtherMsg = new ArrayList<String>();
    String[] from={"name","text"};
    int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_image_other,R.id.chatlist_text_other};
    int[] layout={R.layout.chat_listitem_me,R.layout.chat_listitem_other};
    String userQQ=null;
    public String myWord;
    /**
     * 这里两个布局文件使用了同一个id，测试一下是否管用
     * TT事实证明这回产生id的匹配异常！所以还是要分开。。
     *
     * userQQ用于接收Intent传递的qq号，进而用来调用数据库中的相关的联系人信息，这里先不讲
     * 先暂时使用一个头像
     */

    public final static int OTHER=1;
    public final static int ME=0;


    protected ListView chatListView=null;
    protected Button chatSendButton=null;
    protected EditText editText=null;

    protected MyChatAdapter adapter=null;
    private int ItemID;
    private int To;
    private int From;
    private String FileName;
    private String surl = GlobalParameterApplication.getSurl();
    private boolean uploadSuccessful = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        Bundle out = intent.getExtras();
        GlobalParameterApplication.setAllowChatThread(false);
        ItemID=(int)out.get("itemID");
        From=(int)out.get("To");

        To = GlobalParameterApplication.getUserID();
        FileName = To+"$"+From+"$"+ItemID+".txt";
        String[] sss = fileList();
        boolean exist=false;
        for (int i = 0; i < sss.length;i++){
            if (FileName.equals(sss[i])){
                exist=true;
                break;
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        chatList=new ArrayList<HashMap<String,Object>>();

        if(exist){
        FileInputStream inStream = null;
        try {
            inStream = openFileInput(FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();//
     
		int len=0;  
		byte[] buffer = new byte[102400];
        try {
            while((len=inStream.read(buffer))!=-1){
                outStream.write(buffer, 0, len);//
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] content_byte = outStream.toByteArray();
		String content = new String(content_byte);
        try {
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] ss = content.split("\\$\\$");
		for (int i = 1; i < ss.length;i++)
		{
			if (ss[i].charAt(0)=='0')addTextToList(ss[i].substring(2),0);
            else addTextToList(ss[i].substring(2),1);
		}
        }
        mThread.start();
        chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);
        editText=(EditText)findViewById(R.id.chat_bottom_edittext);
        chatListView=(ListView)findViewById(R.id.chat_list);

        adapter=new MyChatAdapter(this,chatList,layout,from,to);


        chatSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
       //         String myWord=null;

                /**
                 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
                 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
                 * ，并且不能发送空消息。
                 */

                myWord=(editText.getText()+"").toString();
                if(myWord.length()==0)
                    return;
                editText.setText("");
                uploadSuccessful = false;

                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        int UserID = GlobalParameterApplication.getUserID();
                        JSONObject data = new JSONObject();
                        JSONObject chatinfo = new JSONObject();
                        try {
                            URL url = new URL(surl+"/item_add_chart");
                            try {
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                chatinfo.put("publisher_id",To);
                                chatinfo.put("buyer_id",From);
                                chatinfo.put("content",myWord);
                                chatinfo.put("itemID",ItemID);
                                data.put("chat",chatinfo);
                                data.put("token",GlobalParameterApplication.getToken());
                                conn.setRequestMethod("POST");
                                conn.setDoOutput(true);

                                conn.setRequestProperty("Content-Type", "application/json");
                                //conn.setRequestProperty("Content-Length", String.valueOf(data.toJSONString().length()));
                                OutputStream output=conn.getOutputStream();
                                output.write(data.toJSONString().getBytes());

                                String ostr = IOUtils.toString(conn.getInputStream());
                                System.out.println("Chat2"+ostr);

                                org.json.JSONObject outjson = null;
                                int  result = 1;
                                try {
                                    outjson = new org.json.JSONObject(ostr);
                                    result = outjson.getJSONObject("status").getInt("code");
                                    uploadSuccessful = result==0?true:false;
                                   // while (!GlobalParameterApplication.getChat_othermsg());
                                    GlobalParameterApplication.setChat_othermsg(false);
                                    if (uploadSuccessful && !OtherMsg.isEmpty()){
                                        FileOutputStream fos = openFileOutput(FileName,MODE_APPEND);
                                        for (int i = 0; i < OtherMsg.size(); i++){
                                            fos.write(OtherMsg.get(i).getBytes());
                                        }
                                        fos.close();
                                        OtherMsg.clear();
                                        start = -1;
                                    }
                                    GlobalParameterApplication.setChat_othermsg(true);
                                    Message msg = new Message();
                                    msg.what=233333;
                                    nHandler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }



                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        chatListView.setAdapter(adapter);

    }
    @Override
    public void onDestroy(){
        GlobalParameterApplication.setEnd(true);
        //while (!GlobalParameterApplication.getEnd());
        GlobalParameterApplication.setChat_othermsg(true);
		GlobalParameterApplication.setAllowChatThread(true);
        if (!OtherMsg.isEmpty()){
                FileOutputStream out = null;
                try {
                    out = openFileOutput(FileName,MODE_APPEND);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < OtherMsg.size(); i++){
                    try {
                        out.write(OtherMsg.get(i).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        super.onDestroy();
        }



    public Thread mThread=new Thread(new Runnable() {
        @Override
        public void run() {

            while (GlobalParameterApplication.getPause()) {
                if (GlobalParameterApplication.getLogin_status() == 1 && !GlobalParameterApplication.getAllowChatThread()) {
                  //  while (!GlobalParameterApplication.getEnd());
                    GlobalParameterApplication.setEnd(false);
                    Message msg = new Message();
                    msg.what = 22234;
                    msg.arg1 = 0;
                    int UserID = GlobalParameterApplication.getUserID();
                    JSONObject data = new JSONObject();

                    try {
                        URL url = new URL(surl + "/item_get_chart");
                        try {
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();



                            data.put("token", GlobalParameterApplication.getToken());
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);
                            conn.setConnectTimeout(1000);
                            conn.setReadTimeout(1000);
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Content-Length", String.valueOf(data.toJSONString().length()));
                            conn.getOutputStream().write(data.toJSONString().getBytes());

                            String ostr = IOUtils.toString(conn.getInputStream());
                            System.out.println("chat" + ostr);

                            org.json.JSONObject outjson = null;
                            org.json.JSONArray result = null;
                            try {
                                outjson = new org.json.JSONObject(ostr);
                                result = outjson.getJSONArray("chat");

                                int chat_num = GlobalParameterApplication.getChat_Num();
                              //  while (!GlobalParameterApplication.getChat_othermsg());
                                GlobalParameterApplication.setChat_othermsg(false);
                                System.out.println(chat_num+"++++++++++"+result.length());
                                if (chat_num < result.length()) {
                                    System.out.println(chat_num+"++++++++++"+result.length());
                                    //GlobalParameterApplication.setAllowChatThread(false);

                                    for (int i = chat_num; i < result.length(); i++) {
                                        org.json.JSONObject now = (org.json.JSONObject) result.get(i);
                                        int To = now.getInt("buyer_id");
                                        int From = now.getInt("publisher_id");
                                        int itemID = now.getInt("itemID");
                                        String content = now.getString("content");
                                        String FileName1 = To + "$" + From + "$" + itemID + ".txt";
                                        if (FileName1.equals(FileName)) {
                                            OtherMsg.add("$$" + 1 + "$" + content);
                                        } else {
                                            FileOutputStream fos = openFileOutput(FileName1, MODE_APPEND);
                                            fos.write(("$$" + 1 + "$" + content).getBytes());
                                            fos.close();
                                        }
                                    }

                                    GlobalParameterApplication.setChat_Num(result.length());

                                    msg.arg1 = 1;
                                    nHandler.sendMessage(msg);
                                    GlobalParameterApplication.setEnd(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    });

    public Handler nHandler = new Handler(){public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case 22234:
                if (msg.arg1 == 1){
                    if (OtherMsg.size()-1>=start+1){
                        for (int i = start+1; i < OtherMsg.size();i++){
                            addTextToList(OtherMsg.get(i).substring(4,OtherMsg.get(i).length()),OTHER);
                        }
                        start = OtherMsg.size()-1;
                    }

                        GlobalParameterApplication.setChat_othermsg(true);
                    adapter.notifyDataSetChanged();
                    chatListView.setSelection(chatList.size()-1);

                    Toast.makeText(Chat.this,"You have new message!!!",Toast.LENGTH_LONG);
                }
                break;
            case 233333:
                if(uploadSuccessful) {
                    FileOutputStream fos = null;
                    try {
                        fos = openFileOutput(FileName,MODE_APPEND);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.write(("$$0$"+myWord).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addTextToList(myWord, ME);}


                /**
                 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
                 */
                adapter.notifyDataSetChanged();
                chatListView.setSelection(chatList.size()-1);

            break;
        }
    }

    };
    protected void addTextToList(String text, int who){
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("person",who );
        map.put("name", who==ME?"Me":From);
        map.put("text", text);
        chatList.add(map);
    }

    private class MyChatAdapter extends BaseAdapter {

        Context context=null;
        ArrayList<HashMap<String,Object>> chatList=null;
        int[] layout;
        String[] from;
        int[] to;



        public MyChatAdapter(Context context,
                             ArrayList<HashMap<String, Object>> chatList, int[] layout,
                             String[] from, int[] to) {
            super();
            this.context = context;
            this.chatList = chatList;
            this.layout = layout;
            this.from = from;
            this.to = to;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return chatList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder{
            public TextView nameView=null;
            public TextView textView=null;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder=null;
            int who=(Integer)chatList.get(position).get("person");

            convertView= LayoutInflater.from(context).inflate(
                    layout[who==ME?0:1], null);
            holder=new ViewHolder();
            holder.nameView=(TextView)convertView.findViewById(to[who*2+0]);
            holder.textView=(TextView)convertView.findViewById(to[who*2+1]);


            System.out.println(holder);
            System.out.println("WHYWHYWHYWHYW");
            System.out.println(holder.nameView);
            //holder.nameView.setBackgroundResource((Integer)chatList.get(position).get(from[0]));
            holder.nameView.setText(chatList.get(position).get(from[0]).toString());
            holder.textView.setText(chatList.get(position).get(from[1]).toString());
            return convertView;
        }

    }

}
