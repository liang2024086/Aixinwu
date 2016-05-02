package com.aixinwu.axw.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.HomepageGuiedAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.activity.ProductDetailActivity;
import com.aixinwu.axw.model.HomepageGuide;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.view.BaseViewPager;
import com.aixinwu.axw.view.CycleViewPager;
import com.aixinwu.axw.view.MyScrollView;


import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class HomePage extends CycleViewPager implements MyScrollView.ScrollViewListener{

    private TextView info1,info2,info3,moreInfo;
    private LinearLayout newCommodity;
    private RelativeLayout popCommodity;
    private RelativeLayout showCommodity;
    private DisplayMetrics metrics;
    private int screenHalfWidth;

    private MyScrollView scrollView;
    View searchHomePage;
    View search;

    private int searchTouchTime = 0;

    private ArrayList<HomepageGuide> homepageGuides = new ArrayList<HomepageGuide>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home_page,null);

        //获取屏幕宽度
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHalfWidth = metrics.widthPixels / 2;

        //继承自父类
        viewPager = (BaseViewPager) view.findViewById(R.id.viewPager);
        indicatorLayout = (LinearLayout) view
                .findViewById(R.id.layout_viewpager_indicator);

        viewPagerFragmentLayout = (FrameLayout) view
                .findViewById(R.id.layout_viewager_content);

        setViewPagerScrollSpeed(1000);//设置滑动速度
        init();

        search =  view.findViewById(R.id.homepage_search);
        searchTouchTime = 0;
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (searchTouchTime == 0) {
                    searchTouchTime++;
                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    intent.putExtra("param1", "搜索");
                    getActivity().startActivityForResult(intent, 1);
                }

                return false;
            }
        });


        //自己的

        scrollView = (MyScrollView) view.findViewById(R.id.homepageScroll);
        scrollView.setScrollViewListener(this);

        searchHomePage = view.findViewById(R.id.searchHomePage);
        searchHomePage.setVisibility(View.INVISIBLE);
        searchHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1","搜索");
                getActivity().startActivity(intent);
            }
        });

        initializeGuide(view);
        showCommodity = (RelativeLayout) view.findViewById(R.id.show_commodity);
        createShowCommodity();
        addFirst();
        addSecond();
        addThird();
        addForth();
        addFifth();
        addSixth();

        /*
        info1 = (TextView) view.findViewById(R.id.info1);
        info1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelloWorld.class);
                Log.i("AAA","JSDFO");
                getActivity().startActivity(intent);
            }
        });

        info2 = (TextView) view.findViewById(R.id.info2);
        info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BBB","JSDFO");
            }
        });

        info3 = (TextView) view.findViewById(R.id.info3);
        info3.setOnClickListener(null);

        moreInfo = (TextView) view.findViewById(R.id.moreInfo);
        moreInfo.setOnClickListener(null);

        info1.setText("-爱心币众筹004期开奖通知֪");
        info2.setText("-爱心币众筹004期未满延期通知֪");
        info3.setText("-女神节开奖公告");

        newCommodity = (LinearLayout) view.findViewById(R.id.home_commodity);
        popCommodity = (RelativeLayout) view.findViewById(R.id.home_deal);

        addNewCommodity(R.mipmap.img1,R.id.img1,"49.00","43");
        addLine();
        addNewCommodity(R.mipmap.img2, R.id.img2, "29.00", "3");
        addLine();
        addNewCommodity(R.mipmap.img3, R.id.img3, "3.00", "2973");

        addPopularCommodity(R.id.popular1, -1);
        addPopularCommodity(R.id.popular2, R.id.popular1);
        addPopularCommodity(R.id.popular3,R.id.popular2);
*/
        return view;
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy){

        if (y > search.getHeight()){
            searchHomePage.setVisibility(View.VISIBLE);
        }
        else {
            searchHomePage.setVisibility(View.INVISIBLE);
        }
        //Toast.makeText(getActivity(),x+" "+search.getHeight()+" "+y,Toast.LENGTH_SHORT).show();
    }

    private void initializeGuide(View view){
        homepageGuides.add(new HomepageGuide(R.mipmap.product1,"二手物品"));
        homepageGuides.add(new HomepageGuide(R.mipmap.img1,"爱心"));
        homepageGuides.add(new HomepageGuide(R.mipmap.product2,"租赁"));
        homepageGuides.add(new HomepageGuide(R.mipmap.product3,"志愿者"));
        homepageGuides.add(new HomepageGuide(R.mipmap.product4,"众筹"));
        homepageGuides.add(new HomepageGuide(R.mipmap.img2,"规则清单"));
        //homepageGuides.add(new HomepageGuide(R.mipmap.img3,"待定"));
        //homepageGuides.add(new HomepageGuide(R.mipmap.img1,"待定"));

        HomepageGuiedAdapter homepageGuiedAdapter = new HomepageGuiedAdapter(
                getActivity(),
                R.layout.homepage_guide_item,
                homepageGuides);

        GridView guides = (GridView) view.findViewById(R.id.homepage_guide);
        guides.setAdapter (homepageGuiedAdapter);
        guides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomepageGuide guide = homepageGuides.get(i);
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1",guide.getName());
                getActivity().startActivity(intent);

            }
        });
    }

    private void addData(){

    }

    private void createShowCommodity(){

        // first
        RelativeLayout firstCommodity = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams firstCommodityParams = new RelativeLayout.LayoutParams(screenHalfWidth, screenHalfWidth);
        firstCommodity.setId(R.id.firstCommodity);
        firstCommodity.setBackgroundColor(Color.WHITE);
        firstCommodityParams.setMargins(0, 0, 3, 3);
        firstCommodityParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        firstCommodityParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        firstCommodity.setLayoutParams(firstCommodityParams);
        showCommodity.addView(firstCommodity, firstCommodityParams);

        //second
        RelativeLayout secondCommodity = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams secondCommodityParams = new RelativeLayout.LayoutParams(screenHalfWidth, screenHalfWidth/2-3);
        secondCommodity.setId(R.id.secondCommodity);
        secondCommodity.setBackgroundColor(Color.WHITE);
        secondCommodityParams.setMargins(0, 0, 0, 3);
        secondCommodityParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        secondCommodityParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        secondCommodityParams.addRule(RelativeLayout.RIGHT_OF, firstCommodity.getId());
        secondCommodity.setLayoutParams(secondCommodityParams);
        showCommodity.addView(secondCommodity,secondCommodityParams);

        //third
        RelativeLayout thirdCommodity = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams thirdCommodityParams = new RelativeLayout.LayoutParams(screenHalfWidth, screenHalfWidth/2);
        thirdCommodity.setId(R.id.thirdCommodity);
        thirdCommodity.setBackgroundColor(Color.WHITE);
        thirdCommodityParams.addRule(RelativeLayout.RIGHT_OF, firstCommodity.getId());
        thirdCommodityParams.addRule(RelativeLayout.BELOW, secondCommodity.getId());
        thirdCommodity.setLayoutParams(thirdCommodityParams);
        showCommodity.addView(thirdCommodity,thirdCommodityParams);

        //forth
        RelativeLayout forthCommodity = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams forthCommodityParams = new RelativeLayout.LayoutParams(metrics.widthPixels/3-3, screenHalfWidth/2);
        forthCommodity.setId(R.id.forthCommodity);
        forthCommodity.setBackgroundColor(Color.WHITE);
        forthCommodityParams.setMargins(0, 0, 3, 0);
        forthCommodityParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        forthCommodityParams.addRule(RelativeLayout.BELOW, firstCommodity.getId());
        forthCommodity.setLayoutParams(forthCommodityParams);
        showCommodity.addView(forthCommodity,forthCommodityParams);

        //fifth
        RelativeLayout fifthCommodity = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams fifthCommodityParams = new RelativeLayout.LayoutParams(metrics.widthPixels/3-3, screenHalfWidth/2);
        fifthCommodity.setId(R.id.fifthCommodity);
        fifthCommodity.setBackgroundColor(Color.WHITE);
        fifthCommodityParams.setMargins(0, 0, 3, 0);
        fifthCommodityParams.addRule(RelativeLayout.RIGHT_OF,forthCommodity.getId());
        fifthCommodityParams.addRule(RelativeLayout.BELOW, firstCommodity.getId());
        fifthCommodity.setLayoutParams(fifthCommodityParams);
        showCommodity.addView(fifthCommodity,fifthCommodityParams);

        //sixth
        RelativeLayout sixthCommodity = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams sixthCommodityParams = new RelativeLayout.LayoutParams(metrics.widthPixels/3, screenHalfWidth/2);
        sixthCommodity.setId(R.id.sixthCommodity);
        sixthCommodity.setBackgroundColor(Color.WHITE);
        sixthCommodityParams.addRule(RelativeLayout.RIGHT_OF,fifthCommodity.getId());
        sixthCommodityParams.addRule(RelativeLayout.BELOW, firstCommodity.getId());
        sixthCommodity.setLayoutParams(forthCommodityParams);
        showCommodity.addView(sixthCommodity,sixthCommodityParams);

    }

    private void addFirst(){
        RelativeLayout firstCommodity = (RelativeLayout) showCommodity.findViewById(R.id.firstCommodity);

        TextView commodityInfo = new TextView(getActivity());
        commodityInfo.setText("匿名用户\n n小时前发布");
        commodityInfo.setId(R.id.userinfo);
        commodityInfo.setPadding(5, 5, 5, 5);
        RelativeLayout.LayoutParams commodityInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        commodityInfo.setLayoutParams(commodityInfoLayoutParams);
        firstCommodity.addView(commodityInfo, commodityInfoLayoutParams);

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(R.mipmap.img1);
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        img1.setPadding(5,5,5,5);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(screenHalfWidth*4/5, screenHalfWidth*4/5);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        img1.setLayoutParams(img1LayoutParams);
        firstCommodity.addView(img1, img1LayoutParams);

        firstCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "first commodity");
                getActivity().startActivity(intent);
            }
        });

    }

    private void addSecond(){

        RelativeLayout secondCommodity = (RelativeLayout) showCommodity.findViewById(R.id.secondCommodity);

        TextView commodityInfo = new TextView(getActivity());
        commodityInfo.setText("萌物志\n欢迎来抢购");
        commodityInfo.setId(R.id.userinfo);
        commodityInfo.setPadding(5, 5, 5, 5);
        commodityInfo.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams commodityInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        commodityInfoLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        commodityInfo.setLayoutParams(commodityInfoLayoutParams);
        secondCommodity.addView(commodityInfo, commodityInfoLayoutParams);

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(R.mipmap.product1);
        img1.setPadding(5, 5, 5, 5);
        //img1.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(screenHalfWidth/2, screenHalfWidth/2);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img1LayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        img1.setLayoutParams(img1LayoutParams);
        secondCommodity.addView(img1, img1LayoutParams);

        secondCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "second commodity");
                getActivity().startActivity(intent);
            }
        });
    }

    private void addThird(){

        RelativeLayout thirdCommodity = (RelativeLayout) showCommodity.findViewById(R.id.thirdCommodity);

        TextView commodityInfo = new TextView(getActivity());
        commodityInfo.setText("萌物志\n欢迎来抢购");
        commodityInfo.setId(R.id.userinfo);
        commodityInfo.setPadding(5, 5, 5, 5);
        commodityInfo.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams commodityInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        commodityInfoLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        commodityInfo.setLayoutParams(commodityInfoLayoutParams);
        thirdCommodity.addView(commodityInfo, commodityInfoLayoutParams);

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(R.mipmap.product2);
        img1.setPadding(5,5,5,5);
        //img1.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(screenHalfWidth/2, screenHalfWidth/2);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img1LayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        img1.setLayoutParams(img1LayoutParams);
        thirdCommodity.addView(img1, img1LayoutParams);

        thirdCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "third commodity");
                getActivity().startActivity(intent);
            }
        });
    }

    private void addForth(){

        RelativeLayout forthCommodity = (RelativeLayout) showCommodity.findViewById(R.id.forthCommodity);

        TextView commodityInfo = new TextView(getActivity());
        commodityInfo.setText("萌物志\n欢迎来抢购");
        commodityInfo.setId(R.id.userinfo);
        commodityInfo.setPadding(5, 5, 5, 5);
        commodityInfo.setGravity(Gravity.CENTER);
        commodityInfo.setTextSize(screenHalfWidth / 50);
        RelativeLayout.LayoutParams commodityInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        commodityInfoLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        commodityInfo.setLayoutParams(commodityInfoLayoutParams);
        forthCommodity.addView(commodityInfo, commodityInfoLayoutParams);

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(R.mipmap.img2);
        img1.setPadding(5, 5, 5, 5);
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels/6, metrics.widthPixels/6);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img1LayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        img1.setLayoutParams(img1LayoutParams);
        forthCommodity.addView(img1, img1LayoutParams);

        forthCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "forth commodity");
                getActivity().startActivity(intent);
            }
        });
    }

    private void addFifth(){

        RelativeLayout fifthCommodity = (RelativeLayout) showCommodity.findViewById(R.id.fifthCommodity);

        TextView commodityInfo = new TextView(getActivity());
        commodityInfo.setText("萌物志\n欢迎来抢购");
        commodityInfo.setId(R.id.userinfo);
        commodityInfo.setPadding(5, 5, 5, 5);
        commodityInfo.setGravity(Gravity.CENTER);
        commodityInfo.setTextSize(screenHalfWidth / 50);
        RelativeLayout.LayoutParams commodityInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        commodityInfoLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        commodityInfo.setLayoutParams(commodityInfoLayoutParams);
        fifthCommodity.addView(commodityInfo, commodityInfoLayoutParams);

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(R.mipmap.img3);
        img1.setPadding(5, 5, 5, 5);
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels/6, metrics.widthPixels/6);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img1LayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        img1.setLayoutParams(img1LayoutParams);
        fifthCommodity.addView(img1, img1LayoutParams);

        fifthCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "fifth commodity");
                getActivity().startActivity(intent);
            }
        });
    }

    private void addSixth(){

        RelativeLayout sixthCommodity = (RelativeLayout) showCommodity.findViewById(R.id.sixthCommodity);

        TextView commodityInfo = new TextView(getActivity());
        commodityInfo.setText("萌物志\n欢迎来抢购");
        commodityInfo.setId(R.id.userinfo);
        commodityInfo.setPadding(5, 5, 5, 5);
        commodityInfo.setGravity(Gravity.CENTER);
        commodityInfo.setTextSize(screenHalfWidth/50);
        RelativeLayout.LayoutParams commodityInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commodityInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        commodityInfoLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        commodityInfo.setLayoutParams(commodityInfoLayoutParams);
        sixthCommodity.addView(commodityInfo, commodityInfoLayoutParams);

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(R.mipmap.product3);
        img1.setPadding(5, 5, 5, 5);
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels/6, metrics.widthPixels/6);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img1LayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        img1.setLayoutParams(img1LayoutParams);
        sixthCommodity.addView(img1, img1LayoutParams);

        sixthCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "sixth commodity");
                getActivity().startActivity(intent);
            }
        });
    }

    private void addPopularCommodity(int id,int aboveId){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHalfWidth = metrics.widthPixels / 2;

        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.setId(id);
        relativeLayout.setBackgroundColor(0xFFFFFFFF);
        if (aboveId != -1){
            relativeLayoutParams.addRule(RelativeLayout.BELOW,aboveId);
        }
        relativeLayoutParams.setMargins(0, 20, 0, 20);
        relativeLayout.setLayoutParams(relativeLayoutParams);

        TextView userInfo = new TextView(getActivity());
        userInfo.setText("匿名用户\n n小时前发布");
        userInfo.setId(R.id.userinfo);
        RelativeLayout.LayoutParams userInfoLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        userInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        userInfoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        userInfoLayoutParams.setMargins(0, 5, 0, 20);
        userInfo.setLayoutParams(userInfoLayoutParams);

        relativeLayout.addView(userInfo, userInfoLayoutParams);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setId(R.id.linear1);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(15, 25, 15, 15);
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, screenHalfWidth/2);
        linearLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayoutParams.addRule(RelativeLayout.BELOW, userInfo.getId());
        linearLayout.setLayoutParams(linearLayoutParams);


        ImageView picture = new ImageView(getActivity());
        picture.setId(R.id.pop1);
        picture.setImageResource(R.mipmap.img1);
        RelativeLayout.LayoutParams pictureLayoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels/4, screenHalfWidth/2);
        picture.setLayoutParams(pictureLayoutParams);

        linearLayout.addView(picture, pictureLayoutParams);

        View blank1 = new View(getActivity());
        blank1.setLayoutParams(new RelativeLayout.LayoutParams(screenHalfWidth / 10, screenHalfWidth / 2));
        linearLayout.addView(blank1);

        ImageView picture1 = new ImageView(getActivity());
        picture1.setId(R.id.pop2);
        picture1.setImageResource(R.mipmap.img2);
        RelativeLayout.LayoutParams pictureLayoutParams1 = new RelativeLayout.LayoutParams(metrics.widthPixels/4, screenHalfWidth / 2);
        picture1.setLayoutParams(pictureLayoutParams1);

        linearLayout.addView(picture1, pictureLayoutParams1);

        View blank2 = new View(getActivity());
        blank2.setLayoutParams(new RelativeLayout.LayoutParams(screenHalfWidth / 10, screenHalfWidth / 2));
        linearLayout.addView(blank2);

        ImageView picture2 = new ImageView(getActivity());
        picture2.setImageResource(R.mipmap.img3);
        RelativeLayout.LayoutParams pictureLayoutParams2 = new RelativeLayout.LayoutParams(metrics.widthPixels / 4, screenHalfWidth / 2);
        picture2.setLayoutParams(pictureLayoutParams2);

        linearLayout.addView(picture2, pictureLayoutParams2);

        relativeLayout.addView(linearLayout, linearLayoutParams);

        TextView description = new TextView(getActivity());
        description.setText("商品介绍\n     这件商品质量很好，大家快来买啊！！！");
        RelativeLayout.LayoutParams desLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        desLayoutParams.addRule(RelativeLayout.BELOW,linearLayout.getId());
        desLayoutParams.setMargins(0,0,0,30);
        description.setLayoutParams(desLayoutParams);

        relativeLayout.addView(description);

        popCommodity.addView(relativeLayout,relativeLayoutParams);


    }

    private void addNewCommodity(int pic,int pic_id,String coin,String repertory){

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHalfWidth = metrics.widthPixels / 2;

        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setPadding(0, 10, 0, 10);
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));//���ǿ�ȣ����Ǹ߶�
        /*LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) newCommodity.getLayoutParams();
        layoutParams.height = screenHalfWidth;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;*/

        ImageView img1 = new ImageView(getActivity());
        img1.setImageResource(pic);
        //img1.setLayoutParams(new RelativeLayout.LayoutParams(screenHalfWidth, screenHalfWidth));
        img1.setId(pic_id);
        RelativeLayout.LayoutParams img1LayoutParams = new RelativeLayout.LayoutParams(screenHalfWidth*4/3, screenHalfWidth);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        img1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        img1.setLayoutParams(img1LayoutParams);
        relativeLayout.addView(img1, img1LayoutParams);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.addRule(RelativeLayout.RIGHT_OF, img1.getId());
        linearLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(linearLayoutParams);
        relativeLayout.addView(linearLayout, linearLayoutParams);

        TextView text1 = new TextView(getActivity());
        text1.setText("爱心币: "+coin);
        //text1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams text1LayoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        text1.setLayoutParams(text1LayoutParams);
        linearLayout.addView(text1, text1LayoutParams);

        TextView text2 = new TextView(getActivity());
        text2.setText("库存: "+ repertory);
        //text1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams text2LayoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        text2.setLayoutParams(text2LayoutParams);
        linearLayout.addView(text2, text2LayoutParams);



        newCommodity.addView(relativeLayout,relativeLayout.getLayoutParams());

    }

    private void addLine(){

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(15, 10, 15, 10);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));//���ǿ�ȣ����Ǹ߶�

        View line = new View(getActivity());
        line.setBackgroundColor(Color.GRAY);
        RelativeLayout.LayoutParams lineLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 5);
        lineLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //lineLayoutParams.setMargins(0,100,0,100);

        line.setLayoutParams(lineLayoutParams);
        linearLayout.addView(line, lineLayoutParams);

        newCommodity.addView(linearLayout, linearLayout.getLayoutParams());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){

        if (requestCode == 1){
            searchTouchTime = 0;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
