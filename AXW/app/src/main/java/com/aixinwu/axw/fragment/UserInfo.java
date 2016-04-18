package com.aixinwu.axw.fragment;

//import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.aixinwu.axw.R;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UserInfo extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //View view = getActivity().findViewById(R.id.after_login);
        View view = inflater.inflate(R.layout.userinfo, null);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Button my_about_btn = (Button) getActivity().findViewById(R.id.my_about_btn);

        my_about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "hello", Toast.LENGTH_LONG).show();
            }
        });
    }
}
