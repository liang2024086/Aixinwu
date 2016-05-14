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
import com.aixinwu.axw.activity.PersonalDetailActivity;
import com.aixinwu.axw.activity.SignupActivity;
import com.aixinwu.axw.tools.GlobalParameterApplication;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UserInfo extends Fragment {


    private RelativeLayout ly_personalinfo;
    private LinearLayout ly_logininfo;
    private Button btn_logoff;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.personalinfo, null);
        return view;
    }



    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ly_logininfo = (LinearLayout) getActivity().findViewById(R.id.login);
        ly_personalinfo = (RelativeLayout) getActivity().findViewById(R.id.personal);
        btn_logoff = (Button) getActivity().findViewById(R.id.personal_exit);

    }


    @Override
    public void onStart () {
        //GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();

        if (GlobalParameterApplication.getLogin_status() == 0) {
            ly_logininfo.setVisibility(View.GONE);
            ly_personalinfo.setVisibility(View.VISIBLE);
            btn_logoff.setVisibility(View.VISIBLE);
        }
        else if (GlobalParameterApplication.getLogin_status() == 1) {
            ly_logininfo.setVisibility(View.VISIBLE);
            ly_personalinfo.setVisibility(View.GONE);
            btn_logoff.setVisibility(View.GONE);
        }

        Button login_btn = (Button) getActivity().findViewById(R.id.personal_login_button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });
/*
        TextView signup_btn = (TextView) getActivity().findViewById(R.id.signup);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                getActivity().startActivity(intent);
            }
        });
*/
        Button logoff_btn = (Button) getActivity().findViewById(R.id.personal_exit);
        logoff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();
                GlobalParameterApplication.setToken("");
                GlobalParameterApplication.setLogin_status(0);


                ly_logininfo.setVisibility(View.VISIBLE);
                ly_personalinfo.setVisibility(View.GONE);
                btn_logoff.setVisibility(View.GONE);

            }

        });

        ly_personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalDetailActivity.class);
                getActivity().startActivity(intent);
            }

        });

        super.onStart();
    }
}
