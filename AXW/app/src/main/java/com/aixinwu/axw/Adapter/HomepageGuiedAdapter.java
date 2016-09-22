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

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.HomepageGuide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Created by liangyuding on 2016/5/1.
 */
public class HomepageGuiedAdapter extends ArrayAdapter<HomepageGuide> {

    private int resourceId;
    public HomepageGuide guide;
    public Bitmap bitmap;
    private ImageView guideImage;

    public HomepageGuiedAdapter (Context context,
                                 int textViewResourseId,
                                 List<HomepageGuide> objects) {
        super(context, textViewResourseId, objects);
        resourceId = textViewResourseId;

    }

    /*private Handler nhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x8888) {
                //显示从网上下载的图片
                guideImage.setImageBitmap(bitmap);
            }
        }
    };*/

    @Override
    public View getView(int position, View concertView, ViewGroup parent) {
        guide = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        guideImage = (ImageView) view.findViewById(R.id.homepage_guide_image);
        TextView guideName = (TextView) view.findViewById(R.id.homepage_guide_name);


        ImageLoader.getInstance().displayImage(guide.getImgURL(), guideImage);

        /*new Thread(){
            @Override
            public void run() {
                try {
                    //创建一个url对象
                    URL url=new URL(guide.getImgURL());
                    //打开URL对应的资源输入流
                    InputStream is= url.openStream();
                    //从InputStream流中解析出图片
                    bitmap = BitmapFactory.decodeStream(is);
                    //  imageview.setImageBitmap(bitmap);

                    //发送消息，通知UI组件显示图片
                    nhandler.sendEmptyMessage(0x8888);
                    //关闭输入流
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();*/

        //guideImage.setImageResource(guide.getImgId());
        guideName.setText(guide.getName());
        return view;

    }
}
