package com.aixinwu.axw.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.aixinwu.axw.Adapter.ConfirmOrderAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import java.util.ArrayList;
import java.util.List;

public class ConfirmOrder extends Activity {

    private ArrayList<ShoppingCartEntity> mDatas = new ArrayList<>();
    private ConfirmOrderAdapter mAdapter;
    private ListView commodityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        mDatas.add(new ShoppingCartEntity("jsiod", "爱心屋", "你好", 1, 2, "http://202.120.47.213:12345/" + "img/product/5570/201311.jpg"));
        mDatas.add(new ShoppingCartEntity("jsiod","爱心屋","你好",1,2, "http://202.120.47.213:12345/"+"img/product/5570/201311.jpg"));
        mDatas.add(new ShoppingCartEntity("jsiod","爱心屋","你好",1,2, "http://202.120.47.213:12345/"+"img/product/5570/201311.jpg"));
        mDatas.add(new ShoppingCartEntity("jsiod","爱心屋","你好",1,2, "http://202.120.47.213:12345/"+"img/product/5570/201311.jpg"));
        mDatas.add(new ShoppingCartEntity("jsiod","爱心屋","你好",1,2, "http://202.120.47.213:12345/"+"img/product/5570/201311.jpg"));
        mDatas.add(new ShoppingCartEntity("jsiod","爱心屋","你好",1,2, "http://202.120.47.213:12345/"+"img/product/5570/201311.jpg"));


        commodityList = (ListView)findViewById(R.id.commodityList);
        mAdapter = new ConfirmOrderAdapter(this,mDatas);
        commodityList.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm_order, menu);
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
}
