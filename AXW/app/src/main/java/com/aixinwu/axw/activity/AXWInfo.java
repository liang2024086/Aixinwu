package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aixinwu.axw.R;

public class AXWInfo extends Activity {

    ImageView qrApp;
    ImageView qrWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_axwinfo);

        qrApp = (ImageView) findViewById(R.id.qrAPP);
        qrWechat = (ImageView) findViewById(R.id.qrWechat);

        qrApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), RawPictureActivity.class);
                intent.putExtra("img","APP");
                startActivity(intent);
            }
        });

        qrWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), RawPictureActivity.class);
                intent.putExtra("img","wechat");
                startActivity(intent);
            }
        });
    }
}
