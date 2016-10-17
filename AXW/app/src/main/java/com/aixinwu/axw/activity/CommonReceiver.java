package com.aixinwu.axw.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.ReceiverAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.ReceiverInof;

import java.util.ArrayList;
import java.util.List;

public class CommonReceiver extends Activity {

    ListView receiverList;
    List<ReceiverInof> receiverInofList = new ArrayList<ReceiverInof>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_receiver);

        receiverList = (ListView) findViewById(R.id.receivers);
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));
        receiverInofList.add(new ReceiverInof("张小琪", "514xxxxxxx", "182xxxxxxxx"));


        ReceiverAdapter receiverAdapter = new ReceiverAdapter(
                this,
                R.layout.item_receiver,
                receiverInofList);

        receiverList.setAdapter(receiverAdapter);

        TextView addReceiver = (TextView)findViewById(R.id.addReceiver);
        addReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"add receiver",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common_receiver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
