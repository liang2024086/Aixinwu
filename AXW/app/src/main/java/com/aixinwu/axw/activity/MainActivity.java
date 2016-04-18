package com.aixinwu.axw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.aixinwu.axw.fragment.HomePage;
import com.aixinwu.axw.fragment.LoveCoin;
import com.aixinwu.axw.fragment.SubmitThings;
import com.aixinwu.axw.fragment.UsedDeal;
import com.aixinwu.axw.fragment.UserInfo;
import com.aixinwu.axw.fragment.ViewPageFragment;
import com.aixinwu.axw.view.SlidingMenu;

import com.aixinwu.axw.R;
/**
 * Created by liangyuding on 2016/4/6.
 */
public class MainActivity extends FragmentActivity{

    SlidingMenu slidingMenu;
    HomePage homePage;
    LoveCoin loveCoin;
    SubmitThings submitThings;
    UsedDeal usedDeal;
    UserInfo userInfo,userInfo1;
    ViewPageFragment viewPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initListener();
    }

    private void init(){
        slidingMenu = (SlidingMenu)findViewById(R.id.slidingMenu);
        slidingMenu.setLeftView(getLayoutInflater().inflate(R.layout.userinfo_frame, null));
        slidingMenu.setRightView(getLayoutInflater().inflate(R.layout.try_frame, null));
        slidingMenu.setCenterView(getLayoutInflater().inflate(R.layout.center_frame,null));
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        userInfo = new UserInfo();
        transaction.replace(R.id.userinfo_frame,userInfo);

        //userInfo1 = new UserInfo();
        //transaction.replace(R.id.try_frame,userInfo1);

        viewPageFragment = new ViewPageFragment();
        transaction.replace(R.id.center_frame,viewPageFragment);

        transaction.commit();
    }

    private void initListener(){
        viewPageFragment.setMyPageChangeListener(new ViewPageFragment.MyPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if(viewPageFragment.isFirst()){
                    slidingMenu.setCanSliding(true,false);
                }else if(viewPageFragment.isEnd()){
                    slidingMenu.setCanSliding(false,true);
                }
                else{
                    slidingMenu.setCanSliding(false,false);
                }
            }
        });
    }

    public void showLeft() {
        slidingMenu.showLeftView();
    }

    public void showRight() {
        slidingMenu.showRightView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }
}
