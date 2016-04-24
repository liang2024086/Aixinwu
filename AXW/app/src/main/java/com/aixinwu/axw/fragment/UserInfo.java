package com.aixinwu.axw.fragment;

//import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.LoginActivity;
import com.aixinwu.axw.activity.SignupActivity;
import com.aixinwu.axw.tools.GlobalParameterApplication;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UserInfo extends Fragment {


    private LinearLayout ly_personalinfo;
    private LinearLayout ly_logininfo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.userinfo, null);
        return view;
    }



    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ly_logininfo = (LinearLayout) getActivity().findViewById(R.id.before_login);
        ly_personalinfo = (LinearLayout) getActivity().findViewById(R.id.after_login);
    }


    @Override
    public void onStart () {
        GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();
        if (gpa.getLogin_status() == 1) {
            ly_logininfo.setVisibility(View.GONE);
            ly_personalinfo.setVisibility(View.VISIBLE);
        }
        else if (gpa.getLogin_status() == 0) {
            ly_logininfo.setVisibility(View.VISIBLE);
            ly_personalinfo.setVisibility(View.GONE);
        }

        TextView login_btn = (TextView) getActivity().findViewById(R.id.login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });

        TextView signup_btn = (TextView) getActivity().findViewById(R.id.signup);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                getActivity().startActivity(intent);
            }
        });

        TextView logoff_btn = (TextView) getActivity().findViewById(R.id.logoff);
        logoff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();
                gpa.setToken("");
                gpa.setLogin_status(0);


                ly_logininfo.setVisibility(View.VISIBLE);
                ly_personalinfo.setVisibility(View.GONE);


            }
        });

        super.onStart();
    }
}
