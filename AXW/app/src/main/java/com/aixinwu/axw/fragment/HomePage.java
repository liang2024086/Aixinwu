package com.aixinwu.axw.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.view.BaseViewPager;
import com.aixinwu.axw.view.CycleViewPager;


import org.w3c.dom.Text;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class HomePage extends CycleViewPager {

    private TextView info1,info2,info3,moreInfo;
    private LinearLayout newCommodity;
    private RelativeLayout popCommodity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home_page,null);

        //继承自父类
        viewPager = (BaseViewPager) view.findViewById(R.id.viewPager);
        indicatorLayout = (LinearLayout) view
                .findViewById(R.id.layout_viewpager_indicator);

        viewPagerFragmentLayout = (FrameLayout) view
                .findViewById(R.id.layout_viewager_content);

        setViewPagerScrollSpeed(1000);//设置滑动速度
        init();

        //自己的

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

        return view;
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
        super.onActivityResult(requestCode, resultCode, data);
    }
}
