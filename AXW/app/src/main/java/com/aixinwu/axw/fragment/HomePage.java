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
import com.aixinwu.axw.Adapter.ProductAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.Buy;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.activity.MainActivity;
import com.aixinwu.axw.activity.ProductDetailActivity;
import com.aixinwu.axw.activity.WelcomeActivity;
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
    private static List<Product> dbDataProduct = new ArrayList<Product>();
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
                    /*Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    intent.putExtra("param1", "搜索");
                    getActivity().startActivityForResult(intent, 1);*/
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
                GlobalParameterApplication.surl = "http://202.120.47.213:12301/api";
                GlobalParameterApplication.axwUrl = "http://202.120.47.213:12301/";
                GlobalParameterApplication.imgSurl = "http://202.120.47.213:12301/img/";
                //GlobalParameterApplication.login_status = 0;
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });

        mThread.start();
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
        /*for (int i = 0; i < dbData.size(); ++i){
            homepageGuides.add(new HomepageGuide(dbData.get(i).getItemId(),dbData.get(i).getPicId(),dbData.get(i).getType()));
        }*/

        int size = dbDataProduct.size();
        for (int i = size - 1; i >=  0; --i){
            if (size - 1 - i == 6) break;
            homepageGuides.add(new HomepageGuide(dbDataProduct.get(i).getId(),dbDataProduct.get(i).getImage_url(),dbDataProduct.get(i).getProduct_name()));
        }

        HomepageGuiedAdapter homepageGuiedAdapter = new HomepageGuiedAdapter(
                getActivity(),
                R.layout.homepage_guide_item,
                homepageGuides);

        GridView guides = (GridView) getActivity().findViewById(R.id.homepage_guide);
        //guides.setAdapter (homepageGuiedAdapter);
        guides.setAdapter(homepageGuiedAdapter);
        guides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomepageGuide guide = homepageGuides.get(i);
                /*Intent intent = new Intent();
                intent.putExtra("itemId", dbData.get(i).getItemId());
                intent.putExtra("caption",dbData.get(i).getType());
                intent.putExtra("pic_url",dbData.get(i).getPicId());
                intent.putExtra("description",dbData.get(i).getDoc());
                intent.setClass(getActivity(), Buy.class);
                startActivity(intent);*/

                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("productId", dbDataProduct.get(dbDataProduct.size()-i-1).getId());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }

    private void addData(){

    }


    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            //getDbData();
            dbDataProduct = LoveCoin.getDbData("exchange");
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


                    initializeGuide(view);
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
        dbData.clear();

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
                                    dbData.add(new Bean(result.getJSONObject(i).getInt("ID"),GlobalParameterApplication.imgSurl+"1B4B907678CCD423", result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description")));
                                } else
                                    dbData.add(new Bean(result.getJSONObject(i).getInt("ID"),GlobalParameterApplication.imgSurl+rr[0], result.getJSONObject(i).getString("caption"), result.getJSONObject(i).getString("description")));
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
