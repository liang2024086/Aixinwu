package com.aixinwu.axw.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.tools.ReceiverInof;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by liangyuding on 2016/10/16.
 */
public class ReceiverAdapter extends ArrayAdapter<ReceiverInof> {

    private int resourceId;



    public ReceiverAdapter (Context context,
                           int textViewResourseId,
                           List<ReceiverInof> objects) {
        super(context, textViewResourseId, objects);
        resourceId = textViewResourseId;

    }




    @Override
    public View getView(int position, View concertView, ViewGroup parent) {

        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        ((TextView)view.findViewById(R.id.name)).setText(getItem(position).getName());
        ((TextView)view.findViewById(R.id.stuId)).setText(getItem(position).getStuId());
        ((TextView)view.findViewById(R.id.phone)).setText(getItem(position).getPhone());

        view.findViewById(R.id.relativeDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.relativeEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "edit", Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }
}
