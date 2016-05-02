package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.HomepageGuide;

import java.util.List;


/**
 * Created by liangyuding on 2016/5/1.
 */
public class HomepageGuiedAdapter extends ArrayAdapter<HomepageGuide> {

    private int resourceId;

    public HomepageGuiedAdapter (Context context,
                           int textViewResourseId,
                           List<HomepageGuide> objects) {
        super(context, textViewResourseId, objects);
        resourceId = textViewResourseId;

    }

    @Override
    public View getView(int position, View concertView, ViewGroup parent) {
        HomepageGuide guide = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView guideImage = (ImageView) view.findViewById(R.id.homepage_guide_image);
        TextView guideName = (TextView) view.findViewById(R.id.homepage_guide_name);

        guideImage.setImageResource(guide.getImgId());
        guideName.setText(guide.getName());
        return view;

    }
}
