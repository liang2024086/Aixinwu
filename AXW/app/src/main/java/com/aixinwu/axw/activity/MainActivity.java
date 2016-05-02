package com.aixinwu.axw.activity;

import android.annotation.SuppressLint;
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
import com.aixinwu.axw.tools.ADInfo;
import com.aixinwu.axw.tools.ViewFactory;
import com.aixinwu.axw.view.CycleViewPager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class MainActivity extends FragmentActivity{

    private View tab_home,tab_love_coin,tab_submit,tab_deal,tab_userinfo;
    private Fragment[] mFragments;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    //锟斤拷锟节碉拷锟斤拷锟斤拷锟剿筹拷
    private int mBackKeyPressedTimes = 0;

    private List<ImageView> views = new ArrayList<ImageView>();
    private List<ADInfo> infos = new ArrayList<ADInfo>();
    //private CycleViewPager cycleViewPager;

    private String[] imageUrls = {"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
            "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
            "http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
            "http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
            "http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        configImageLoader();
        initialize();
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
            Toast.makeText(this, getResources().getString(R.string.pressAgain), Toast.LENGTH_SHORT).show();
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

        if (requestCode == 1){
            mFragments[0].onActivityResult(requestCode,resultCode,data);
        }
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

    @SuppressLint("NewApi")
    private void initialize() {

        //cycleViewPager = (CycleViewPager) getSupportFragmentManager()
        //        .findFragmentById(R.id.fragment_cycle_viewpager_content);

        for(int i = 0; i < imageUrls.length; i ++){
            ADInfo info = new ADInfo();
            info.setUrl(imageUrls[i]);
            info.setContent("图片-->" + i );
            infos.add(info);
        }

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, infos.get(infos.size() - 1).getUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(this, infos.get(i).getUrl()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, infos.get(0).getUrl()));

        // 设置循环，在调用setData方法前调用
        ((CycleViewPager)mFragments[0]).setCycle(true);

        // 在加载数据前设置是否循环
        ((CycleViewPager)mFragments[0]).setData(views, infos, mAdCycleViewListener);
        //设置轮播
        ((CycleViewPager)mFragments[0]).setWheel(true);

        // 设置轮播时间，默认5000ms
        ((CycleViewPager)mFragments[0]).setTime(2000);
        //设置圆点指示图标组居中显示，默认靠右
        ((CycleViewPager)mFragments[0]).setIndicatorCenter();
    }

    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            if (((CycleViewPager)mFragments[0]).isCycle()) {
                position = position - 1;
                Toast.makeText(MainActivity.this,
                        "position-->" + info.getContent(), Toast.LENGTH_SHORT)
                        .show();
            }

        }

    };

    /**
     * 配置ImageLoder
     */
    private void configImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }
}
