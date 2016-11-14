package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Record;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangyuding on 2016/10/29.
 */
public class RecordAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Record> mDatas = new ArrayList<>();
    private ViewHolder holder;
    private Context recordContext;


    public RecordAdapter(Context context, ArrayList<Record> mDatas) {
        mInflater = LayoutInflater.from(context);
        this.recordContext = context;
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
            convertView = mInflater.inflate(R.layout.item_record, parent, false);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_item_shopping_cart);
            holder.name = (TextView) convertView.findViewById(R.id.tv_item_shopping_cart_name);
            holder.category = (TextView) convertView.findViewById(R.id
                    .tv_item_shopping_cart_category);
            holder.price = (TextView) convertView.findViewById(R.id
                    .tv_item_shopping_cart_price);
            holder.number = (TextView) convertView.findViewById(R.id
                    .tv_item_shopping_cart_number);
            holder.img1 = (ImageView) convertView.findViewById(R.id
                    .img1);
            holder.img2 = (ImageView) convertView.findViewById(R.id
                    .img2);
            holder.img3 = (ImageView) convertView.findViewById(R.id
                    .img3);
           // holder.imgList = (ListView) convertView.findViewById(R.id.imgList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Record entity = (Record) getItem(position);
       // holder.category.setText(entity.getCategory());
        holder.name.setText("订单编号："+entity.getOrder_sn());
        holder.price.setText(entity.getTotal_product_price());
        holder.number.setText(entity.getUpdateTime());

        String [] imgUrls = entity.getImgUrls().split(",");
       /* List<String> imgLists = new ArrayList<>();
        for (int i = 0; i < imgUrls.length; ++i){
            imgLists.add(imgUrls[i]);
        }
        ImgAdapter imgAdapter = new ImgAdapter(recordContext,imgLists);
        holder.imgList.setAdapter(imgAdapter);*/


       Log.i("GOOD  ",entity.getImgUrls());

        holder.img1.setVisibility(View.GONE);
        holder.img2.setVisibility(View.GONE);
        holder.img3.setVisibility(View.GONE);

        for (int i = 0; i < imgUrls.length; ++i){
            switch (i){
                case 0:
                    ImageLoader.getInstance().displayImage(GlobalParameterApplication.axwUrl+imgUrls[i], holder.img1);
                    holder.img1.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    ImageLoader.getInstance().displayImage(GlobalParameterApplication.axwUrl+imgUrls[i], holder.img2);
                    holder.img2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ImageLoader.getInstance().displayImage(GlobalParameterApplication.axwUrl+imgUrls[i], holder.img3);
                    holder.img3.setVisibility(View.VISIBLE);
                    break;
            }
        }

       // ImageLoader.getInstance().displayImage(entity.getImgUrl(), holder.img);


        return convertView;
    }

    class ViewHolder {
        CheckBox cb;
        ImageView img1,img2,img3;
        ListView imgList;
        TextView name, category, price, number;
    }

    private class ImgAdapter extends BaseAdapter{

        private int position;
        private List<String> imgLists = new ArrayList<>();
        private LayoutInflater imgInflater;
        private ImageView img;

        public ImgAdapter(Context context,List<String> imgLists){
            imgInflater = LayoutInflater.from(context);
            this.imgLists = imgLists;
        }

        @Override
        public int getCount() {
            return imgLists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            this.position = position;
            if (convertView == null) {
                convertView = imgInflater.inflate(R.layout.item_record_imgs, parent, false);
                String imgSurl = imgLists.get(position);

                img = (ImageView) convertView.findViewById(R.id.image);
                ImageLoader.getInstance().displayImage(GlobalParameterApplication.axwUrl+imgSurl,img);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }



            return convertView;
        }
    }
}
