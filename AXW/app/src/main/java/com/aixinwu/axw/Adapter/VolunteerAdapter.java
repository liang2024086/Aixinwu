package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.model.VolunteerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by hello on 2016/10/29.
 */
public class VolunteerAdapter extends ArrayAdapter<VolunteerActivity> {
    private int resourceId;
    public VolunteerActivity product;
    public Bitmap bitmap;






    public VolunteerAdapter (Context context,
                           int textViewResourseId,
                           List<VolunteerActivity> objects) {
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
        TextView workDate = (TextView) view.findViewById(R.id.workDate);
        TextView fullOrNot = (TextView) view.findViewById(R.id.fullOrNot);

        int need = product.getNeededPeople();
        int signed = product.getSignedPeople();

        if (need <= signed && need != 0)
            fullOrNot.setText("名额已满   " + signed+"/"+need);
        else{
            if (need == 0)
                fullOrNot.setText("欢迎报名   " + signed+"/∞");
            else if (need > signed)
                fullOrNot.setText("欢迎报名   " + signed+"/"+need);
        }

        if (!product.getImg_url().equals(""))
            ImageLoader.getInstance().displayImage(product.getImg_url(), productImage);

        productName.setText(product.getName());
        productPrice.setText("+ "+String.valueOf(product.getPayback()));
        workDate.setText("活动时间："+product.getTime().substring(5,10)+" "+product.getTime().substring(11,16));

        return view;

    }
}
