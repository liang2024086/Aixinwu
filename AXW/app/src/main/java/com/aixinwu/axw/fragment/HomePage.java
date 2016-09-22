package com.aixinwu.axw.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
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
import com.aixinwu.axw.activity.Buy;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.activity.ProductDetailActivity;
import com.aixinwu.axw.model.HomepageGuide;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.view.BaseViewPager;
import com.aixinwu.axw.view.CycleViewPager;
import com.aixinwu.axw.view.MyScrollView;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    private String surl = GlobalParameterApplication.getSurl();
    private String MyToken;
    private static List<Bean> dbData = new ArrayList<Bean>();
    private String visitCounter = "" ;
    private String money = "" ;
    private String user = "" ;
    private String item = "" ;
    private TextView number1;
    private TextView number2;
    private TextView number3;
    private TextView number4;


    private View view;


    private int searchTouchTime = 0;

    private ArrayList<HomepageGuide> homepageGuides = new ArrayList<HomepageGuide>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.home_page,null);

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

        search = view.findViewById(R.id.homepage_search);
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

        number1 = (TextView) view.findViewById(R.id.number1);
        number2 = (TextView) view.findViewById(R.id.number2);
        number3 = (TextView) view.findViewById(R.id.number3);
        number4 = (TextView) view.findViewById(R.id.number4);

        scrollView = (MyScrollView) view.findViewById(R.id.homepageScroll);
        scrollView.setScrollViewListener(this);

        searchHomePage = view.findViewById(R.id.searchHomePage);
        searchHomePage.setVisibility(View.INVISIBLE);
        searchHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1", "搜索");
                getActivity().startActivity(intent);
            }
        });

        mThread.start();
        /*showCommodity = (RelativeLayout) view.findViewById(R.id.show_commodity);
        createShowCommodity();
        addFirst();
        addSecond();
        addThird();
        addForth();
        addFifth();
        addSixth();
*/
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
        //homepageGuides.add(new HomepageGuide(dbData.get(0).getItemId(),dbData.get(0).getPicId(),dbData.get(0).getPicId()+"|"+dbData.get(1).getPicId()+"|"+
        //        dbData.get(2).getPicId()+"|"+dbData.get(3).getPicId()+"|"+dbData.get(3).getPicId()+"|"+dbData.get(4).getPicId()+"|"));
        homepageGuides.add(new HomepageGuide(dbData.get(0).getItemId(),dbData.get(0).getPicId(),dbData.get(0).getType()));
        homepageGuides.add(new HomepageGuide(dbData.get(1).getItemId(),dbData.get(1).getPicId(),dbData.get(1).getType()));
        homepageGuides.add(new HomepageGuide(dbData.get(2).getItemId(),dbData.get(2).getPicId(),dbData.get(2).getType()));
        homepageGuides.add(new HomepageGuide(dbData.get(3).getItemId(),dbData.get(3).getPicId(),dbData.get(3).getType()));
        homepageGuides.add(new HomepageGuide(dbData.get(4).getItemId(),dbData.get(4).getPicId(),dbData.get(4).getType()));
        homepageGuides.add(new HomepageGuide(dbData.get(5).getItemId(),dbData.get(5).getPicId(),dbData.get(5).getType()));
        //homepageGuides.add(new HomepageGuide(R.mipmap.img1,dbData.get(0).getType()));
        //homepageGuides.add(new HomepageGuide(R.mipmap.img3,"待定"));
        //homepageGuides.add(new HomepageGuide(R.mipmap.img1,"待定"));

        //Toast.makeText(getActivity(),R.mipmap.img1+" ",Toast.LENGTH_SHORT).show();

        HomepageGuiedAdapter homepageGuiedAdapter = new HomepageGuiedAdapter(
                getActivity(),
                R.layout.homepage_guide_item,
                homepageGuides);

        GridView guides = (GridView) getActivity().findViewById(R.id.homepage_guide);
        guides.setAdapter (homepageGuiedAdapter);
        guides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomepageGuide guide = homepageGuides.get(i);
                Intent intent = new Intent();
                intent.putExtra("itemId", dbData.get(i).getItemId());
                intent.putExtra("caption",dbData.get(i).getType());
                intent.setClass(getActivity(), Buy.class);
                startActivity(intent);
            }
        });
    }

    private void addData(){

    }
/*
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

       //two edges
        View leftEdge = new View(getActivity());
        RelativeLayout.LayoutParams leftEdgeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        leftEdgeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftEdgeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        leftEdge.setLayoutParams(leftEdgeLayoutParams);
        firstCommodity.addView(leftEdge, leftEdgeLayoutParams);


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
*/


    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            getDbData();
            getStaticData();


            Message msg= new Message();
            msg.what = 23212;
            handler.sendMessage(msg);

        }
    });
    public Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 23212:
                    //                 lvResults.onRefreshComplete();

                    switch(dbData.size()){

                        case 0:break;
                    }


                    initializeGuide(view);   // don't need this guide liangyuding
                    number1.setText(visitCounter);
                    number2.setText(money);
                    number3.setText(user);
                    number4.setText(item);




                    break;
            }
        }

    };
    private void getDbData(){
        MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();

        {

            Log.i("UsedDeal", "get");
            try {
                URL url = new URL(surl + "/item_mainpage");
                try {
                    Log.i("UsedDeal","getconnection");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                    org.json.JSONObject outjson = null;
                    try {
                        JSONArray result = null;
                        outjson = new org.json.JSONObject(ostr);
                        result = outjson.getJSONArray("items");
                        for (int i = 0; i < result.length(); i++)
                            if (result.getJSONObject(i).getInt("status")==0)
                            {
                                String[] rr = result.getJSONObject(i).getString("images").split(",");
                                if (rr[0]=="") {
                                    BitmapFactory.Options cc = new BitmapFactory.Options();
                                    cc.inSampleSize = 20;
                                    dbData.add(new Bean(result.getJSONObject(i).getInt("ID"),"http://202.120.47.213:12345/img/1B4B907678CCD423", result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description")));
                                } else
                                    dbData.add(new Bean(result.getJSONObject(i).getInt("ID"),"http://202.120.47.213:12345/img/"+rr[0], result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description")));
                            }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }



    private void getStaticData(){
        MyToken= GlobalParameterApplication.getToken();
        JSONObject data = new JSONObject();
        {

            Log.i("UsedDeal", "get");
            try {
                URL url = new URL(surl + "/static");
                try {
                    Log.i("UsedDeal","getconnection");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                    org.json.JSONObject outjson = null;
                    try {
                        org.json.JSONObject result = null;
                        outjson = new org.json.JSONObject(ostr);
                        result = outjson.getJSONObject("staticInfo");

                        visitCounter = result.getString("visitCounter");
                        money = result.getString("money");
                        user = result.getString("user");
                        item = result.getString("item");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){

        if (requestCode == 1){
            searchTouchTime = 0;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
