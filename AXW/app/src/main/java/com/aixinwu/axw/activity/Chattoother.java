package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.aixinwu.axw.R;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.talkmessage;

import io.nats.client.ConnectionFactory;
import schoolapp.chat.Chat;
import schoolapp.chat.Messages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by liangyuding on 2016/4/15.
 */
public class Chattoother extends Activity{
    public int start = -1;
    private TextView chatt;
    ArrayList<HashMap<String,Object>> chatList=null;
    public ArrayList<String> OtherMsg = new ArrayList<String>();
    public ArrayList<String> cont = new ArrayList<String>();
    public ArrayList<Integer> who = new ArrayList<Integer>();
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

    private String otherName = "数据的佛";

    protected ListView chatListView=null;
    protected TextView chatSendButton=null;
    protected EditText editText=null;
    public int num = 0;
    protected MyChatAdapter adapter=null;
    private int ItemID;
    private boolean pause = true;
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
        //  GlobalParameterApplication.setPause(true);
        GlobalParameterApplication.setAllowChatThread(false);
        ItemID=out.getInt("itemID");
        From=out.getInt("To");
        otherName = out.getString("ToName");

        To = GlobalParameterApplication.getUserID();
/*        FileName = To+"$"+From+"$"+ItemID+".txt";
        String[] sss = fileList();
        boolean exist=false;
        for (int i = 0; i < sss.length;i++){
            if (FileName.equals(sss[i])){
                exist=true;
                break;
            }
        }*/
        pause = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        chatList=new ArrayList<HashMap<String,Object>>();

       /* if(exist){
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
        }*/
        List<talkmessage> re00 = GlobalParameterApplication.gettalk(To, From);

        num = re00.size();
        for (talkmessage tm: re00){
            cont.add(tm.getDoc());
            if (tm.getSender()== GlobalParameterApplication.getUserID()){
                addTextToList(tm.getDoc(),ME);who.add(ME);
            } else {addTextToList(tm.getDoc(),OTHER);who.add(OTHER);}

        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (pause) {
                    if (GlobalParameterApplication.getLogin_status() == 1) {

                        Message msg = new Message();
                        msg.what = 22234;
                        msg.arg1 = 0;
                        int UserID = GlobalParameterApplication.getUserID();
                        List<talkmessage> res= GlobalParameterApplication.gettalk(To, From);
                        int ss = res.size();
                        if (ss > num){
                            for (int i=num; i<ss; i++)

                                if (res.get(i).getSender() != GlobalParameterApplication.getUserID()){
                                    cont.add(res.get(i).getDoc());
                                    who.add(OTHER);}

                            num = ss;}
                        nHandler.sendMessage(msg);
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                otherName = ChatList.getUserName(""+From);
                Message msg = new Message();
                msg.what = 537485;
                nHandler.sendMessage(msg);
            }
        }).start();*/

        chatt =(TextView)findViewById(R.id.chat_contact_name);
        chatt.setText(otherName);
        chatSendButton=(TextView)findViewById(R.id.chat_bottom_sendbutton);
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
                GlobalParameterApplication.publish(myWord, From);
                Date date= new Date();//创建一个时间对象，获取到当前的时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置时间显示格式
                String str = sdf.format(date);//将当前时间格式化为需要的类型

                GlobalParameterApplication.add(To, From, myWord, 1,str);
                cont.add(myWord);
                who.add(ME);
                addTextToList(myWord, ME);
                adapter.notifyDataSetChanged();
                chatListView.setSelection(chatList.size() - 1);
                /*new Thread(new Runnable(){
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
           //                         GlobalParameterApplication.setChat_othermsg(false);
                                    /*if (uploadSuccessful && !OtherMsg.isEmpty()){
                                        FileOutputStream fos = openFileOutput(FileName,MODE_APPEND);
                                        for (int i = 0; i < OtherMsg.size(); i++){
                                            fos.write(OtherMsg.get(i).getBytes());
                                        }
                                        fos.close();
                                        OtherMsg.clear();
                                        start = -1;
                                    }
       //                             GlobalParameterApplication.setChat_othermsg(true);
                                    Message msg = new Message();
                                    msg.what=233333;
                                  //  nHandler.sendMessage(msg);
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
                }).start();*/

            }
        });

        chatListView.setAdapter(adapter);
        chatListView.setSelection(chatList.size()-1);

    }
    @Override
    public void onDestroy(){
        // GlobalParameterApplication.setEnd(true);
        //while (!GlobalParameterApplication.getEnd());
        // GlobalParameterApplication.setChat_othermsg(true);

     /*   if (!OtherMsg.isEmpty()){
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
            }*/
        pause= false;
        //   GlobalParameterApplication.setPause(false);
        super.onDestroy();
    }





    public Handler nHandler = new Handler(){public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case 537485:
                chatt.setText(otherName);
                break;
            case 22234:
                /*if (msg.arg1 == 1){
                    if (OtherMsg.size()-1>=start+1){
                        for (int i = start+1; i < OtherMsg.size();i++){
                            addTextToList(OtherMsg.get(i).substring(4,OtherMsg.get(i).length()),OTHER);
                        }
                        start = OtherMsg.size()-1;
                    }

      //                  GlobalParameterApplication.setChat_othermsg(true);
                    adapter.notifyDataSetChanged();
                    chatListView.setSelection(chatList.size()-1);

                    Toast.makeText(Chat.this,"You have new message!!!",Toast.LENGTH_LONG);
                }*/
                chatList.clear();
                for (int i = 0; i < cont.size();i++){
                    addTextToList(cont.get(i),who.get(i));
                }
                adapter.notifyDataSetChanged();
                //chatListView.setSelection(chatList.size()-1);
                break;
            case 233333:
                if(uploadSuccessful) {
                   /* FileOutputStream fos = null;
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
                    }*/

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
        map.put("name", who==ME?"Me":"用户" + From);
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
