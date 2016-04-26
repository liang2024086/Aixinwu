package com.aixinwu.axw.tools;

/**
 * Created by liangyuding on 2016/4/26.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.aixinwu.axw.R;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @param context
     * @return
     */
    public static ImageView getImageView(Context context, String url) {
        ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
                R.layout.view_banner, null);
        ImageLoader.getInstance().displayImage(url, imageView);
        return imageView;
    }
}
