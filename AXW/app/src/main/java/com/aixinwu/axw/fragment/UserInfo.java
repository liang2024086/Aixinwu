package com.aixinwu.axw.fragment;

//import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.AXWInfo;
import com.aixinwu.axw.activity.CashTransfer;
import com.aixinwu.axw.activity.ChatList;
import com.aixinwu.axw.activity.ItemList;
import com.aixinwu.axw.activity.LoginActivity;
import com.aixinwu.axw.activity.PersonalDetailActivity;
import com.aixinwu.axw.activity.SignupActivity;
import com.aixinwu.axw.activity.CommonReceiver;
import com.aixinwu.axw.activity.ConfirmOrder;
import com.aixinwu.axw.activity.MyCollection;
import com.aixinwu.axw.activity.ShoppingCartActivity;
import com.aixinwu.axw.activity.SettingActivity;
import com.aixinwu.axw.activity.ItemRecord;
import com.aixinwu.axw.activity.MyDonation;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.database.Sqlite;


import android.database.sqlite.SQLiteDatabase;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UserInfo extends Fragment {

    private int ss=0;
    private RelativeLayout ly_personalinfo;
    private RelativeLayout ly_logininfo;
    private Button btn_logoff;
    private String username;
    private String coins;

    private Sqlite userDbHelper;

    private String headProtrait;

    private ImageView headImageView;

    private TextView jobtitle;

    private View redDot;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.personalinfo, null);
        configImageLoader();
        userDbHelper = new Sqlite(getActivity());
        headImageView = (ImageView) view.findViewById(R.id.headImage);
        jobtitle = (TextView) view.findViewById(R.id.jobtitle);
        redDot = (View) view.findViewById(R.id.redDot);

        
        return view;
    }



    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ly_logininfo = (RelativeLayout) getActivity().findViewById(R.id.login);
        ly_personalinfo = (RelativeLayout) getActivity().findViewById(R.id.personal);
        btn_logoff = (Button) getActivity().findViewById(R.id.personal_exit);

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
    public void onStart () {
        //GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();

        if (GlobalParameterApplication.getLogin_status() == 1) {
            Toast.makeText(getActivity(), "ssssssss", Toast.LENGTH_LONG);
            ly_logininfo.setVisibility(View.GONE);
            ly_personalinfo.setVisibility(View.VISIBLE);
            btn_logoff.setVisibility(View.VISIBLE);
            

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getPersonalInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what=123123;
                    nHandler.sendMessage(msg);
                }
            }).start();
        }
        else if (GlobalParameterApplication.getLogin_status() == 0) {
            ly_logininfo.setVisibility(View.VISIBLE);
            ly_personalinfo.setVisibility(View.GONE);
            btn_logoff.setVisibility(View.GONE);
        }

        TextView login_btn = (TextView) getActivity().findViewById(R.id.personal_login_button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });


        TextView signup_btn = (TextView) getActivity().findViewById(R.id.signup);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Button logoff_btn = (Button) getActivity().findViewById(R.id.personal_exit);
        logoff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();


                
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("1111", "111111111");
                        // TODO Auto-generated method stub  
                        try{
                        SQLiteDatabase db = userDbHelper.getWritableDatabase();
                        db.execSQL("delete from AXWuser where userId = 1"); 
                    	db.close();}
                        catch(Throwable e){
                            e.printStackTrace();
                        }
                    }
                }).start();

                GlobalParameterApplication.setToken("");
                GlobalParameterApplication.setLogin_status(0);
                GlobalParameterApplication.stop();

                ly_logininfo.setVisibility(View.VISIBLE);
                ly_personalinfo.setVisibility(View.GONE);
                btn_logoff.setVisibility(View.GONE);

            }

        });

/*
        ly_personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalDetailActivity.class);
                getActivity().startActivity(intent);
            }

        });*/

        RelativeLayout transfer_coin = (RelativeLayout) getActivity().findViewById(R.id.transfer);
        transfer_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), CashTransfer.class);
                    startActivity(intent);}
            }
        });


        RelativeLayout ly_message = (RelativeLayout)getActivity().findViewById(R.id.message);
        RelativeLayout ly_myitem = (RelativeLayout)getActivity().findViewById(R.id.myitem);
        ly_myitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), ItemList.class);
                    startActivity(intent);}
            }
        });

        RelativeLayout ly_mySetting = (RelativeLayout)getActivity().findViewById(R.id.userSetting);
        ly_mySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);}
            }
        });

        RelativeLayout ly_myBought = (RelativeLayout)getActivity().findViewById(R.id.myBought);
        ly_myBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), ItemRecord.class);
                    startActivity(intent);}
            }
        });

        RelativeLayout ly_shopCart = (RelativeLayout)getActivity().findViewById(R.id.shopCart);
        ly_shopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                    startActivity(intent);}
            }
        });

        RelativeLayout ly_myCollection = (RelativeLayout)getActivity().findViewById(R.id.myCollection);
        ly_myCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), MyCollection.class);
                    startActivity(intent);}
            }
        });

        RelativeLayout ly_myDonation = (RelativeLayout)getActivity().findViewById(R.id.myDonation);
        ly_myDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), MyDonation.class);
                    startActivity(intent);}
            }
        });

        ly_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalParameterApplication.getLogin_status()==1){
                    Intent intent = new Intent(getActivity(), ChatList.class);
                    startActivity(intent);
                }
            }
        });

        RelativeLayout about_axw = (RelativeLayout)getActivity().findViewById(R.id.about_aixinwu);
        about_axw.setOnClickListener(new View.OnClickListener(){
        	@Override
        	public void onClick(View view){
        		Intent intent = new Intent(getActivity(),AXWInfo.class);
        		startActivity(intent);
        	}
        });

        super.onStart();
    }
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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }
    /*
public Thread mThread = new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            getPersonalInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what=123123;
        nHandler.sendMessage(msg);
    }
});
*/
    public Handler nHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123123) {
                TextView coinsText = (TextView) getActivity().findViewById(R.id.coins);
                TextView usernameText = (TextView) getActivity().findViewById(R.id.username);
                coinsText.setText("爱心币： " + coins);
                if (GlobalParameterApplication.whtherBindJC == 1)
            		jobtitle.setText("校园用户");
            	else jobtitle.setText("社区用户");
                usernameText.setText(username);
                if (headProtrait.length() != 0)
                    ImageLoader.getInstance().displayImage(GlobalParameterApplication.imgSurl+headProtrait,headImageView);
            }
        }
    };
    //=======================Connect to Server=================
    public String genJson(String token) {
        JSONObject matadata = new JSONObject();
        matadata.put("TimeStamp", 123124233);
        matadata.put("Device", "android");


        JSONObject data = new JSONObject();
        data.put("mataData", matadata);
        data.put("token", token);
        return data.toJSONString();
    }

    private void getPersonalInfo () throws IOException {
        String token = GlobalParameterApplication.getToken();
        String jsonstr = genJson(token);
        String surl = GlobalParameterApplication.getSurl();
        URL url = new URL(surl + "/usr_get");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        conn.getOutputStream().write(jsonstr.getBytes());

        String ostr = IOUtils.toString(conn.getInputStream());
        //System.out.println(ostr);

        org.json.JSONObject outjson;
        org.json.JSONObject userinfojson;
        String userinfo;
        try {
            outjson = new org.json.JSONObject(ostr);
            userinfo = outjson.getString("userinfo");


            userinfojson = new org.json.JSONObject(userinfo);
            System.out.println("HEELO\n"+userinfojson.toString());
            coins = userinfojson.getString("coins");
            String myUserName = userinfojson.getString("username");
            String myNickName = userinfojson.getString("nickname");
            if (myNickName.length() == 0)
                username = myUserName;
            else username = myNickName;

            headProtrait = userinfojson.getString("image");
            //username = userinfojson.getString("username");



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//======================================================================
}
