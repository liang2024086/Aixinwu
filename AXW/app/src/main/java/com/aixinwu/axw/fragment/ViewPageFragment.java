package com.aixinwu.axw.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.MainActivity;

/**
 * Created by liangyuding on 2016/4/7.
 */
public class ViewPageFragment extends Fragment {

    private ImageView showLeft;
    //private Button showRight;
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private View tab_home,tab_love_coin,tab_submit,tab_deal;
    private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.view_pager, null);

        tab_home = mView.findViewById(R.id.tab_home);
        tab_love_coin = mView.findViewById(R.id.tab_love_coin);
        tab_submit = mView.findViewById(R.id.tab_submit);
        tab_deal = mView.findViewById(R.id.tab_deal);
        tab_home.setOnClickListener(new tabOnClickListener(0));
        tab_love_coin.setOnClickListener(new tabOnClickListener(1));
        tab_submit.setOnClickListener(new tabOnClickListener(2));
        tab_deal.setOnClickListener(new tabOnClickListener(3));

        showLeft = (ImageView) mView.findViewById(R.id.showLeft);
        //showRight = (Button) mView.findViewById(R.id.showRight);
        mPager = (ViewPager) mView.findViewById(R.id.pager);
        HomePage homePage = new HomePage();
        LoveCoin loveCoin = new LoveCoin();
        SubmitThings submitThings = new SubmitThings();
        UsedDeal usedDeal = new UsedDeal();
        pagerItemList.add(homePage);
        pagerItemList.add(loveCoin);
        pagerItemList.add(submitThings);
        pagerItemList.add(usedDeal);
        mAdapter = new MyAdapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (myPageChangeListener != null)
                    myPageChangeListener.onPageSelected(position);

                ((TextView) tab_home.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
                ((TextView) tab_deal.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
                ((TextView) tab_submit.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
                ((TextView) tab_love_coin.findViewById(R.id.tab_text)).setTextColor(Color.GRAY);
                ((ImageView) tab_home.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.home1_1);
                ((ImageView) tab_love_coin.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.heart1);
                ((ImageView) tab_submit.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.submit1);
                ((ImageView) tab_deal.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.deal1);

                switch (position) {
                    case 0:
                        ((TextView) tab_home.findViewById(R.id.tab_text)).setTextColor(Color.GREEN);
                        ((ImageView) tab_home.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.home2_1);
                        break;
                    case 1:
                        ((TextView) tab_love_coin.findViewById(R.id.tab_text)).setTextColor(Color.GREEN);
                        ((ImageView) tab_love_coin.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.heart2);
                        break;
                    case 2:
                        ((TextView) tab_submit.findViewById(R.id.tab_text)).setTextColor(Color.GREEN);
                        ((ImageView) tab_submit.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.submit2);
                        break;
                    case 3:
                        ((TextView) tab_deal.findViewById(R.id.tab_text)).setTextColor(Color.GREEN);
                        ((ImageView) tab_deal.findViewById(R.id.tab_icon1)).setImageResource(R.mipmap.deal2);
                        break;
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {


            }

            @Override
            public void onPageScrollStateChanged(int position) {


            }
        });

        mPager.setOffscreenPageLimit(4);

        return mView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        showLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showLeft();
            }
        });

        /*showRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showRight();
            }
        });*/
    }

    public boolean isFirst() {
        if (mPager.getCurrentItem() == 0)
            return true;
        else
            return false;
    }

    public boolean isEnd() {
        /*if (mPager.getCurrentItem() == pagerItemList.size() - 1)
            return true;
        else*/
            return false;
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pagerItemList.size();
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if (position < pagerItemList.size())
                fragment = pagerItemList.get(position);
            else
                fragment = pagerItemList.get(0);

            return fragment;

        }
    }

    private MyPageChangeListener myPageChangeListener;

    public void setMyPageChangeListener(MyPageChangeListener l) {

        myPageChangeListener = l;

    }

    public interface MyPageChangeListener {
        public void onPageSelected(int position);
    }

    public class tabOnClickListener implements View.OnClickListener{
        private int index = 0;

        public tabOnClickListener(int i){ index = i; }

        @Override
        public void onClick(View v){
            mPager.setCurrentItem(index);
        }
    }
}
