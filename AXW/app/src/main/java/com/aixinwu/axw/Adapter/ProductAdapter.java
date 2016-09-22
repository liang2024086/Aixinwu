package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.BaseAdapter;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.InputStream;
import java.util.List;
import java.net.URL;
/**
 * Created by dell1 on 2016/4/24.
 */
public class ProductAdapter extends ArrayAdapter<Product> {
    private int resourceId;
    public Product product;
    public Bitmap bitmap;






    public ProductAdapter (Context context,
                           int textViewResourseId,
                           List<Product> objects) {
        super(context, textViewResourseId, objects);
        resourceId = textViewResourseId;

    }




    @Override
    public View getView(int position, View concertView, ViewGroup parent) {
        product = getItem(position);


         View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

         ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
         TextView productName = (TextView) view.findViewById(R.id.product_name);
         TextView productPrice = (TextView) view.findViewById(R.id.product_price);

        ImageLoader.getInstance().displayImage(product.getImage_url(), productImage);
/*
        final Handler nhandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x4869) {
                    //显示从网上下载的图片
                    productImage.setImageBitmap(bitmap);
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                try {
                        //创建一个url对象
                        URL url=new URL(product.getImage_url());
                        //打开URL对应的资源输入流
                        InputStream is= url.openStream();
                        //从InputStream流中解析出图片
                        bitmap = BitmapFactory.decodeStream(is);
                        //  imageview.setImageBitmap(bitmap);

                        //发送消息，通知UI组件显示图片
                        nhandler.sendEmptyMessage(0x4869);
                        //关闭输入流
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
*/



        productName.setText(product.getProduct_name());
        productPrice.setText(String.valueOf(product.getPrice()));
        return view;

    }

}
