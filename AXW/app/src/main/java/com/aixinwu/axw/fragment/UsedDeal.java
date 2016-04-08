package com.aixinwu.axw.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aixinwu.axw.R;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UsedDeal extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.used_deal,null);
        return view;
    }
}
