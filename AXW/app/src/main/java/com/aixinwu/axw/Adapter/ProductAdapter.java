package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.BaseAdapter;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;

import java.util.List;

/**
 * Created by dell1 on 2016/4/24.
 */
public class ProductAdapter extends ArrayAdapter<Product> {
    private int resourceId;

    public ProductAdapter (Context context,
                           int textViewResourseId,
                           List<Product> objects) {
        super(context, textViewResourseId, objects);
        resourceId = textViewResourseId;

    }

    @Override
    public View getView(int position, View concertView, ViewGroup parent) {
        Product product = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price);

        productImage.setImageResource(product.getImage_id());
        productName.setText(product.getProduct_name());
        productPrice.setText(String.valueOf(product.getPrice()));
        return view;

    }

}
