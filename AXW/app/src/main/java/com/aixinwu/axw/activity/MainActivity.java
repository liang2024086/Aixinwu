package com.aixinwu.axw.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.NotifyMessage;
import com.aixinwu.axw.Adapter.PicAdapter;
import com.aixinwu.axw.fragment.HomePage;
import com.aixinwu.axw.fragment.LoveCoin;
import com.aixinwu.axw.fragment.SubmitThings;
import com.aixinwu.axw.fragment.UsedDeal;
import com.aixinwu.axw.fragment.UserInfo;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.ADInfo;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.ViewFactory;
import com.aixinwu.axw.view.CycleViewPager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class MainActivity extends FragmentActivity{

    private View tab_home,tab_love_coin,tab_submit,tab_deal,tab_userinfo;
    private Fragment[] mFragments;
    FragmentManager fragmentManager;
    private boolean checkforfirst=true;
    FragmentTransaction transaction;
    public SharedPreferences sharedPreferences;
    private  int mId;
 
    private int mBackKeyPressedTimes = 0;

    private View redDot;

    private List<ImageView> views = new ArrayList<ImageView>();
    private List<ADInfo> infos = new ArrayList<ADInfo>();
    //private CycleViewPager cycleViewPager;
    private String surl = GlobalParameterApplication.getSurl();
    private String[] imageUrls = {
        	/*"http://aixinwu.sjtu.edu.cn/images/slider/p0.jpg",
        	"http://aixinwu.sjtu.edu.cn/images/slider/p1.jpg",
    		"http://aixinwu.sjtu.edu.cn/images/slider/p2.jpg",*/
    		"http://aixinwu.sjtu.edu.cn/images/welcome/main0.jpg",
            "http://aixinwu.sjtu.edu.cn/images/welcome/main1.jpg",
            "http://aixinwu.sjtu.edu.cn/images/welcome/main2.jpg"/*,
			"http://aixinwu.sjtu.edu.cn/images/slider/p3.jpg",
			"http://aixinwu.sjtu.edu.cn/images/slider/p4.jpg",
			"http://aixinwu.sjtu.edu.cn/images/slider/p5.jpg"*/};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try{
        	NotifyThread.start();

	        init();
	        configImageLoader();
	        initialize();
        }catch (Throwable e){
        	e.printStackTrace();
        }
        
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

        redDot = findViewById(R.id.redDot);
    }

    @Override
    public void onResume(){
        super.onResume();

        if (GlobalParameterApplication.wetherHaveNewVersion)
            redDot.setVisibility(View.VISIBLE);
        else
            redDot.setVisibility(View.GONE);
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
    public void onDestroy(){
        super.onDestroy();
   //     SharedPreferences.Editor editor = sharedPreferences.edit();
  //      editor.putInt("Chat_Num"+GlobalParameterApplication.getUserID(),GlobalParameterApplication.getChat_Num());
   //     editor.commit();
        //GlobalParameterApplication.setPause(false);
        //GlobalParameterApplication.stop();
    }

    public Handler NotifyHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 123112:
                    NotifyMessage st = (NotifyMessage) msg.getData().getSerializable("now");

                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    //构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
                        /*Notification notification = new Notification(R.drawable.aixinwu, "通知", System.currentTimeMillis());
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,new Intent(MainActivity.this,ChatList.class),0);//这是一个PendingIntent,关于它的使用昨天我刚写过一个,有兴趣可以去看
                        notification.contentIntent = pendingIntent;
                        notification.
                        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
                        notification.defaults = Notification.DEFAULT_SOUND;//声音默认
                        manager.notify(0, notification);//发动通知

                            Toast.makeText(MainActivity.this,"You have new message!!!",Toast.LENGTH_LONG);
*/                      Intent intent = new Intent(getApplicationContext(), ChatList.class);
                    //intent.putExtra("To", Integer.valueOf(st.getWho()));
                    //intent.putExtra("itemID",5);
                    PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), GlobalParameterApplication.notifyid,
                            intent, 0);
                    // 通过Notification.Builder来创建通知，注意API Level
                    // API11之后才支持
                    Notification notify2 = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.aixinwu) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                            // icon)
                            .setTicker( "您有来自用户"+st.getWho()+"的新消息，请注意查收")// 设置在status
                            // bar上显示的提示文字
                            .setContentTitle("来自用户"+st.getWho()+"的新消息")// 设置在下拉status
                            // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                            .setContentText("点击打开聊天列表")// TextView中显示的详细内容
                            .setContentIntent(pendingIntent2) // 关联PendingIntent
                            .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                            .getNotification(); // 需要注意build()是在API level
                    // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                    notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                    notify2.defaults = Notification.DEFAULT_SOUND;
                    manager.notify(GlobalParameterApplication.notifyid, notify2);
                    GlobalParameterApplication.notifyid++;
                    break;
            }
        }
    };
    public Thread NotifyThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                if (GlobalParameterApplication.getLogin_status() == 1){
                    NotifyMessage now = GlobalParameterApplication.sentMessages.peek();
                    if (now != null){
                        GlobalParameterApplication.sentMessages.remove();
                        Message msg = new Message();
                        msg.what = 123112;
                        Bundle b = new Bundle();
                        b.putSerializable("now", now);
                        msg.setData(b);
                        NotifyHandler.sendMessage(msg);
                    }
                }
            }
        }
    });
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){

        if (requestCode == 1){
            mFragments[0].onActivityResult(requestCode,resultCode,data);
        } else if (requestCode == 2){
            mFragments[3].onActivityResult(requestCode,resultCode,data);
        }else if (requestCode == 11){

            mFragments[2].onActivityResult(requestCode,resultCode,data);

        }else if (requestCode == 12){
            mFragments[2].onActivityResult(requestCode,resultCode,data);
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
            info.setContent("ͼƬ-->" + i );
            infos.add(info);
        }

        // �����һ��ImageView��ӽ���
        views.add(ViewFactory.getImageView(this, infos.get(infos.size() - 1).getUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(this, infos.get(i).getUrl()));
        }
        // ����һ��ImageView��ӽ���
        views.add(ViewFactory.getImageView(this, infos.get(0).getUrl()));

        // ����ѭ�����ڵ���setData����ǰ����
        ((CycleViewPager)mFragments[0]).setCycle(true);

        // �ڼ�������ǰ�����Ƿ�ѭ��
        ((CycleViewPager)mFragments[0]).setData(views, infos, mAdCycleViewListener);
        //�����ֲ�
        ((CycleViewPager)mFragments[0]).setWheel(true);

        // �����ֲ�ʱ�䣬Ĭ��5000ms
        ((CycleViewPager)mFragments[0]).setTime(2000);
        //����Բ��ָʾͼ���������ʾ��Ĭ�Ͽ���
        ((CycleViewPager)mFragments[0]).setIndicatorCenter();
    }

    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            if (((CycleViewPager)mFragments[0]).isCycle()) {
                position = position - 1;
                //Toast.makeText(MainActivity.this,
                //        "position-->" + info.getContent(), Toast.LENGTH_SHORT)
                //        .show();
            }

        }

    };

    /**
     * ����ImageLoder
     */
    private void configImageLoader() {
        // ��ʼ��ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // ����ͼƬ�����ڼ���ʾ��ͼƬ
                .showImageForEmptyUri(R.drawable.icon_empty) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
                .showImageOnFail(R.drawable.icon_error) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
                .cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���
                .cacheOnDisc(true) // �������ص�ͼƬ�Ƿ񻺴���SD����
                        // .displayer(new RoundedBitmapDisplayer(20)) // ���ó�Բ��ͼƬ
                .build(); // �������ù���DisplayImageOption����

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }
}
