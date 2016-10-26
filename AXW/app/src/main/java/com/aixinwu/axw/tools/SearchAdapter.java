package com.aixinwu.axw.tools;

import android.content.Context;

import com.aixinwu.axw.R;

import java.util.List;

/**
 * Created by Cross_Life on 2016/4/18.
 */

public class SearchAdapter extends CommonAdapter<Bean> {

    public SearchAdapter(Context context, List<Bean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, int position) {
        holder.setImageBitmap(R.id.item_search_iv_icon,mData.get(position).getPicId())
                .setText(R.id.item_search_tv_title,mData.get(position).getType())
                .setText(R.id.item_search_tv_content,mData.get(position).getDoc())
                .setText(R.id.whether_on_sail,"￥"+mData.get(position).getPrice());
    }
}


