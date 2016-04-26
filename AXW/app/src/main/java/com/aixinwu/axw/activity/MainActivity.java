package com.aixinwu.axw.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.fragment.HomePage;
import com.aixinwu.axw.fragment.LoveCoin;
import com.aixinwu.axw.fragment.SubmitThings;
import com.aixinwu.axw.fragment.UsedDeal;
import com.aixinwu.axw.fragment.UserInfo;

import com.aixinwu.axw.R;
/**
 * Created by liangyuding on 2016/4/6.
 */
public class MainActivity extends FragmentActivity{

    private View tab_home,tab_love_coin,tab_submit,tab_deal,tab_userinfo;
    private Fragment[] mFragments;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    //���ڵ������˳�
    private int mBackKeyPressedTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){

        tab_home = findViewById(R.id.tab_home);
        tab_love_coin = findViewById(R.id.tab_love_coin);
        tab_submit = findViewById(R.id.tab_submit);
        tab_deal = findViewById(R.id.tab_deal);
        tab_userinfo = findViewById(R.id.tab_userinfo);

        tab_home.setOnClickListener(new tabOnClickListener(0));
        tab_love_coin.setOnClickListener(new tabOnClickListener(1));
        tab_submit.setOnClickListener(new tabOnClickListener(2));
        tab_deal.setOnClickListener(new tabOnClickListener(3));
        tab_userinfo.setOnClickListener(new tabOnClickListener(4));

        mFragments = new Fragment[5];
        fragmentManager = this.getSupportFragmentManager();

        mFragments[0] = fragmentManager.findFragmentById(R.id.homePage);
        mFragments[1] = fragmentManager.findFragmentById(R.id.LoveCoin);
        mFragments[2] = fragmentManager.findFragmentById(R.id.SubmitThings);
        mFragments[3] = fragmentManager.findFragmentById(R.id.UsedDeal);
        mFragments[4] = fragmentManager.findFragmentById(R.id.UserInfo);

        transaction = fragmentManager.beginTransaction()
                                            .hide(mFragments[0])
                                            .hide(mFragments[1])
                                            .hide(mFragments[2])
                                            .hide(mFragments[3])
                                            .hide(mFragments[4]);

        transaction.show(mFragments[0]).commit();
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);
        if (mBackKeyPressedTimes == 0) {
            Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT).show();
            mBackKeyPressedTimes = 1;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mBackKeyPressedTimes = 0;
                    }
                }
            }.start();
            return;
        }
        else{
            finish();
            System.exit(0);
        }
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class tabOnClickListener implements View.OnClickListener{
        private int index = 0;

        public tabOnClickListener(int i){ index = i; }

        @Override
        public void onClick(View v){
            ((TextView) tab_home.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
            ((TextView) tab_deal.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
            ((TextView) tab_submit.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
            ((TextView) tab_love_coin.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
            ((TextView) tab_userinfo.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);

            ((ImageView) tab_home.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.home_0);
            ((ImageView) tab_love_coin.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.coin_1);
            ((ImageView) tab_submit.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.add_0);
            ((ImageView) tab_deal.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.handshake_1);
            ((ImageView) tab_userinfo.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.user_1);

            transaction = fragmentManager.beginTransaction()
                                            .hide(mFragments[0])
                                            .hide(mFragments[1])
                                            .hide(mFragments[2])
                                            .hide(mFragments[3])
                                            .hide(mFragments[4]);

            switch (index) {
                case 0:
                    ((TextView) tab_home.findViewById(R.id.tab_text)).setTextColor(getResources().getColor(R.color.black));
                    ((ImageView) tab_home.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.home_1);
                    break;
                case 1:
                    ((TextView) tab_love_coin.findViewById(R.id.tab_text)).setTextColor(getResources().getColor(R.color.black));
                    ((ImageView) tab_love_coin.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.coin_2);
                    break;
                case 2:
                    ((TextView) tab_submit.findViewById(R.id.tab_text)).setTextColor(getResources().getColor(R.color.black));
                    ((ImageView) tab_submit.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.add_1);
                    break;
                case 3:
                    ((TextView) tab_deal.findViewById(R.id.tab_text)).setTextColor(getResources().getColor(R.color.black));
                    ((ImageView) tab_deal.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.handshake_0);
                    break;
                case 4:
                    ((TextView) tab_userinfo.findViewById(R.id.tab_text)).setTextColor(getResources().getColor(R.color.black));
                    ((ImageView) tab_userinfo.findViewById(R.id.tab_icon1)).setImageResource(R.drawable.user_0);
                    break;
            }

            transaction.show(mFragments[index]).commit();

        }
    }
}
