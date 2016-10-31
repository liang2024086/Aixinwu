package com.aixinwu.axw.activity;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aixinwu.axw.Adapter.VolunteerAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.fragment.LoveCoin;
import com.aixinwu.axw.model.VolunteerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class VolActivityList extends AppCompatActivity {

    private List<VolunteerActivity> volList = new ArrayList<VolunteerActivity>();
    private GridView volListGrid;

    private android.os.Handler dHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 479234:
                    VolunteerAdapter adapter3 = new VolunteerAdapter(
                            VolActivityList.this,
                            R.layout.volunteer_list_item,
                            volList
                    );

                    volListGrid.setAdapter(adapter3);
                    volListGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            VolunteerActivity product = volList.get(i);
                            Intent intent = new Intent(VolActivityList.this, VolunteerApply.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("volActivityId", product);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_activity_list);

        volListGrid = (GridView) findViewById(R.id.volList);

        new Thread(new Runnable() {
            @Override
            public void run() {
                volList = LoveCoin.getVolunteer();
                Message msg = new Message();
                msg.what = 479234;
                dHandler.sendMessage(msg);
            }
        }).start();

    }
}
