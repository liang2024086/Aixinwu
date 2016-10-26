package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.database.Sqlite;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MyCollection extends Activity {


    private ListView collectionList;
    private List<Bean> collectList = new ArrayList<>();

    private Sqlite userDbHelper = new Sqlite(this);

    private Handler dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 521521:

                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ASDF", "1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        Log.i("ASDF", "2");

        collectionList = (ListView)findViewById(R.id.collectionList);

        MyCollectionAdapter myCollectionAdapter = new MyCollectionAdapter(MyCollection.this,collectList);
        collectionList.setAdapter(myCollectionAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = userDbHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("select itemId,picUrl,type,price from AXWcollect where userName = '" + GlobalParameterApplication.getUser_name() + "'", null);
                while (cursor.moveToNext()) {

                    collectList.add(new Bean(cursor.getInt(0), cursor.getString(1), cursor.getString(2), "价格：" + (cursor.getInt(3))));

                }
                cursor.close();
                db.close();
                Message msg=new Message();
                msg.what=521521;
                dHandler.sendMessage(msg);
            }
        }).start();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_collection, menu);
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

    private class MyCollectionAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<Bean> mDatas = new ArrayList<>();
        private ViewHolder holder;


        public MyCollectionAdapter(Context context, List<Bean> mDatas) {
            mInflater = LayoutInflater.from(context);
            this.mDatas = mDatas;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_collection, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.commodity_name);
                holder.price = (TextView) convertView.findViewById(R.id
                        .commodity_price);
                holder.img = (ImageView) convertView.findViewById(R.id
                        .collectImg);
                holder.seeDetail = (RelativeLayout) convertView.findViewById(R.id.seeDetail);
                holder.delCollection = (RelativeLayout) convertView.findViewById(R.id.collectComodity);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Bean entity = (Bean) getItem(position);
            holder.name.setText(entity.getType());
            holder.price.setText(entity.getDoc() + "");
            ImageLoader.getInstance().displayImage(entity.getPicId().trim(), holder.img);

            holder.seeDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("itemId", collectList.get(position).getItemId());
                    intent.putExtra("caption", collectList.get(position).getType());
                    intent.setClass(MyCollection.this, Buy.class);
                    startActivity(intent);
                }
            });

            holder.delCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase db = userDbHelper.getWritableDatabase();
                    db.execSQL("delete from AXWcollect where itemId ="+collectList.get(position).getItemId()+" and userName = '" + GlobalParameterApplication.getUser_name() + "'");
                    collectList.remove(position);
                    MyCollectionAdapter myCollectionAdapter = (MyCollectionAdapter) collectionList.getAdapter();
                    myCollectionAdapter.notifyDataSetChanged();
                    db.close();
                    Toast.makeText(getApplicationContext(),"取消成功",Toast.LENGTH_SHORT).show();
                }
            });


            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView name, category, price, number;
            RelativeLayout seeDetail,delCollection;
        }
    }

}
