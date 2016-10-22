package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.Bean;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MyCollection extends Activity {

    private ListView collectionList;
    private List<Bean> collectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        collectionList = (ListView)findViewById(R.id.collectionList);
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        collectList.add(new Bean(12,"http://202.120.47.213:12345/"+"img/product/5570/201311.jpg","爱心屋","你好"));
        MyCollectionAdapter myCollectionAdapter = new MyCollectionAdapter(getApplicationContext(),collectList);
        collectionList.setAdapter(myCollectionAdapter);

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
        public View getView(int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_collection, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.commodity_name);
                holder.price = (TextView) convertView.findViewById(R.id
                        .commodity_price);
                holder.img = (ImageView) convertView.findViewById(R.id
                        .collectImg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Bean entity = (Bean) getItem(position);
            holder.name.setText(entity.getType());
            holder.price.setText(entity.getDoc() + "");
            ImageLoader.getInstance().displayImage(entity.getPicId(), holder.img);


            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView name, category, price, number;
        }
    }

}
