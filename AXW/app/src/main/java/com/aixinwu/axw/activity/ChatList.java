package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatList extends Activity {
    private ListView chatlist;
    private SimpleAdapter sim_adapter;
    private ArrayList<String> Name;
    private ArrayList<String> Item;
    private ArrayList<HashMap<String,Object>> chatitem;
   // private ArrayList<String> Content;
    private Button refreshbutton;
    private String[] Files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Files = fileList();
        String[] tmp;
        chatitem = new ArrayList<HashMap<String, Object>>();
        chatlist = (ListView)findViewById(R.id.chatlist);
        refreshbutton = (Button)findViewById(R.id.refresh);
        chatitem.clear();
        for (int i = 0; i < Files.length;i++){
            tmp = Files[i].split("\\$");
            if(tmp.length==3 && tmp[2].substring(tmp[2].length()-4,tmp[2].length()).equals(".txt")){
            if (GlobalParameterApplication.getUserID()==Integer.parseInt(tmp[0])){
                HashMap<String,Object> tt = new HashMap<String,Object>();
                tt.put("Name",tmp[1]);
                tt.put("Item",tmp[2].substring(0,tmp[2].length()-4));
                chatitem.add(tt);}
            }
          //  Content.add("SSSS");
        }
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
                chatitem.clear();
                Files = fileList();
               // String[] tmpp;
                for (int i = 0; i < Files.length;i++){
                    String[] tmpp = Files[i].split("\\$");
                    if(tmpp.length==3 && tmpp[2].substring(tmpp[2].length()-4,tmpp[2].length()).equals(".txt")){
                        if (GlobalParameterApplication.getUserID()==Integer.parseInt(tmpp[0])){
                            HashMap<String,Object> tt = new HashMap<String,Object>();
                            tt.put("Name",tmpp[1]);
                            tt.put("Item",tmpp[2].substring(0,tmpp[2].length()-4));
                            chatitem.add(tt);}
                    }
                sim_adapter.notifyDataSetChanged();

                }
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
}
