package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.CommonAdapter;
import com.aixinwu.axw.tools.ViewHolder;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Cross_Life on 2016/5/13.
 */
public class PicAdapter extends CommonAdapter<String> {
    static class ViewHolder {
        ImageView imgShow;

    }

    public PicAdapter(Context context, List<String> data, int resID){
        super(context,data,resID);

    }

    @Override
    public void convert(com.aixinwu.axw.tools.ViewHolder holder, int position) {
        holder.setImageBitmap(R.id.buyimage,mData.get(position));

    }

}

