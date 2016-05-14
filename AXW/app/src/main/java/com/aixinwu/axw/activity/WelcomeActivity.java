package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.aixinwu.axw.R;

/**
 * Created by liangyuding on 2016/4/25.
 */
public class WelcomeActivity extends Activity {

    private final long SPLASH_LENGTH = 4000;
    Handler handler = new Handler();
    private TextView time;
    private MyCountDownTimer mc;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        time = (TextView)findViewById(R.id.time);


        mc = new MyCountDownTimer(3000, 1000);
        mc.start();

        handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_LENGTH);//2秒后跳转至应用主界面MainActivity

    }


    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture
         *            表示以毫秒为单位 倒计时的总数
         *
         *            例如 millisInFuture=1000 表示1秒
         *
         * @param countDownInterval
         *            表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *            例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            time.setText("剩余\n0秒");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.i("MainActivity", millisUntilFinished + "");
            time.setText("剩余\n"+millisUntilFinished / 1000 + "秒");
        }
    }
}
